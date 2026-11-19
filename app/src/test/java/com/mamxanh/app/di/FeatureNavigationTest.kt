package com.mamxanh.app.di

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavigator
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.presentation.bio.BioDestination
import com.mamxanh.app.presentation.bio.BioRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.koin.dsl.koinApplication

class FeatureNavigationTest {
    @Test
    fun liveAndPreviewRegistrationsResolveTheSameKeys() {
        val application = koinApplication { modules(featureNavigation.map { it.navigationModule }) }

        try {
            val live = HabiTreeRouteRegistry(application.koin.getAll<HabiTreeFeatureDestination>())
            val arguments = mapOf(
                BioRoute.TASK to "31",
                BioRoute.TASK_NEW to "31",
                BioRoute.TREE to "4",
                BioRoute.SHOP to "4",
                BioRoute.INVENTORY to "2",
                BioRoute.FOCUS to "31",
                BioRoute.INSIGHTS to "31",
                BioRoute.SPECIES to "succulent",
                BioRoute.ACHIEVEMENTS to "first-plant",
            )
            for (route in BioRoute.entries.filterNot { it == BioRoute.MORE }) {
                val argument = arguments[route].orEmpty()
                val saved = BioDestination(route, argument)
                val key = live.keyFor(saved)
                assertNotSame(saved, key)
                assertEquals(route.id, key.routeId)
                assertEquals(argument, key.argument)
                assertEquals(previewRouteRegistry.keyFor(saved), key)
                assertNotNull("Missing live content for ${route.id}", live.contentFor(key))
                assertNotNull("Missing preview content for ${route.id}", previewRouteRegistry.contentFor(key))
            }
            val shell = BioDestination("more", "")
            assertSame(shell, live.keyFor(shell))
            assertNull(live.contentFor(shell))
            assertNull(previewRouteRegistry.contentFor(shell))
        } finally {
            application.close()
        }
    }

    @Test
    fun navigatorPreservesArgumentsAndKeepsAValidRoot() {
        val home = BioDestination("home", "")
        val tree = BioDestination("tree", "42")
        val garden = BioDestination("garden", "")
        val navigator = HabiTreeNavigator(mutableListOf(home))

        navigator.goTo(tree)
        navigator.goTo(tree)
        assertEquals(listOf(home, tree), navigator.backStack)
        assertEquals(home, navigator.previous)
        navigator.replaceCurrent(tree.copy(argument = "43"))
        assertEquals("43", navigator.current.argument)
        navigator.goBack(home)
        assertEquals(home, navigator.current)
        navigator.goTo(tree)
        navigator.goTo(garden, replaceStack = true)
        assertEquals(listOf(garden), navigator.backStack)
        navigator.goBack(home)
        assertEquals(listOf(home), navigator.backStack)
    }
}
