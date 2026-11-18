package com.mamxanh.app.di

import com.mamxanh.app.core.presentation.navigation.HabiTreeRouteRegistry
import com.mamxanh.app.feature.home.presentation.navigation.homeFeatureNavigation
import com.mamxanh.app.feature.tasks.presentation.navigation.tasksFeatureNavigation
import com.mamxanh.app.feature.garden.presentation.navigation.gardenFeatureNavigation
import com.mamxanh.app.feature.shop.presentation.navigation.shopFeatureNavigation
import com.mamxanh.app.feature.inventory.presentation.navigation.inventoryFeatureNavigation
import com.mamxanh.app.feature.tree.presentation.navigation.treeFeatureNavigation
import com.mamxanh.app.feature.stats.presentation.navigation.statsFeatureNavigation

internal val featureNavigation = listOf(
    homeFeatureNavigation,
    tasksFeatureNavigation,
    gardenFeatureNavigation,
    shopFeatureNavigation,
    inventoryFeatureNavigation,
    treeFeatureNavigation,
    statsFeatureNavigation,
)

internal val previewRouteRegistry = HabiTreeRouteRegistry(featureNavigation.flatMap { it.destinations })
