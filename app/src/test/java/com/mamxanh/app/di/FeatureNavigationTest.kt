package com.mamxanh.app.di

import com.mamxanh.app.core.presentation.navigation.HabiTreeFeatureDestination
import com.mamxanh.app.core.presentation.navigation.HabiTreeNavigator
import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.presentation.bio.BioDestination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Test
import org.koin.dsl.koinApplication

class FeatureNavigationTest {
    @Test
    fun liveAndPreviewRegistrationsResolveTheSameKeys() {
        val application = koinApplication { modules(featureNavigation.map { it.navigationModule }) }

        try {
            val live = HabiTreeRouteRegistry(application.koin.getAll<HabiTreeFeatureDestination>())
            for ((route, argument) in listOf("home" to "", "task" to "31", "tree" to "4", "shop" to "4", "settings" to "", "data" to "", "onboard" to "")) {
                val saved = BioDestination(route, argument)
                val key = live.keyFor(saved)
                assertNotSame(saved, key)
                assertEquals(route, key.routeId)
                assertEquals(argument, key.argument)
                assertEquals(previewRouteRegistry.keyFor(saved), key)
            }
            val shell = BioDestination("more", "")
            assertSame(shell, live.keyFor(shell))
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
