package com.mamxanh.app

import androidx.lifecycle.SavedStateHandle
import androidx.test.platform.app.InstrumentationRegistry
import com.mamxanh.app.core.domain.RepositoryResult
import com.mamxanh.app.feature.home.domain.repository.OnboardingRepository
import com.mamxanh.app.feature.home.domain.repository.OnboardingRequest
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingAction
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingEvent
import com.mamxanh.app.feature.home.presentation.ui.onboarding.OnboardingViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingMviTest {
    @Test
    fun restoredDraftSkipsOnceAndClearsTheSavedOperationOnAcknowledgement() {
        val saved = SavedStateHandle()
        val requests = mutableListOf<OnboardingRequest>()
        val submitted = CompletableDeferred<OnboardingRequest>()
        val repository =
            OnboardingRepository { request ->
                synchronized(requests) { requests.add(request) }
                submitted.complete(request)
                RepositoryResult.Success("done")
            }
        val original = OnboardingViewModel(repository, saved)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            original.dispatch(OnboardingAction.Next)
            original.dispatch(OnboardingAction.Choose(1, "cactus"))
            original.dispatch(OnboardingAction.Remind(true))
        }

        val restored = OnboardingViewModel(repository, saved)
        assertEquals(1, restored.state.value.step)
        assertEquals("cactus", restored.state.value.seed)
        assertTrue(restored.state.value.reminders)
        instrumentation.runOnMainSync {
            restored.dispatch(OnboardingAction.Skip)
            restored.dispatch(OnboardingAction.Skip)
        }
        val (request, event) =
            runBlocking {
                withTimeout(5_000) { submitted.await() to restored.events.first() }
            }
        assertEquals(OnboardingEvent.Finished("done"), event)
        assertTrue(request.skip)
        assertEquals("cactus", request.seed)
        assertTrue(request.reminders)
        assertEquals(1, synchronized(requests) { requests.size })
        assertEquals(request.operationKey, saved.get<String>("operationKey"))
        instrumentation.runOnMainSync { restored.acknowledgeCompletion() }
        assertFalse(saved.contains("operationKey"))
        assertEquals(0, restored.state.value.step)
    }
}
