package com.mamxanh.app.di

import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.feature.catalog.presentation.navigation.catalogFeatureNavigation
import com.mamxanh.app.feature.garden.presentation.navigation.gardenFeatureNavigation
import com.mamxanh.app.feature.home.presentation.navigation.homeFeatureNavigation
import com.mamxanh.app.feature.inventory.presentation.navigation.inventoryFeatureNavigation
import com.mamxanh.app.feature.settings.presentation.navigation.settingsFeatureNavigation
import com.mamxanh.app.feature.shop.presentation.navigation.shopFeatureNavigation
import com.mamxanh.app.feature.stats.presentation.navigation.statsFeatureNavigation
import com.mamxanh.app.feature.tasks.presentation.navigation.tasksFeatureNavigation
import com.mamxanh.app.feature.tree.presentation.navigation.treeFeatureNavigation

internal val featureNavigation =
    listOf(
        homeFeatureNavigation,
        tasksFeatureNavigation,
        gardenFeatureNavigation,
        catalogFeatureNavigation,
        shopFeatureNavigation,
        inventoryFeatureNavigation,
        treeFeatureNavigation,
        statsFeatureNavigation,
        settingsFeatureNavigation,
    )

internal val previewRouteRegistry = HabiTreeRouteRegistry(featureNavigation.flatMap { it.destinations })
