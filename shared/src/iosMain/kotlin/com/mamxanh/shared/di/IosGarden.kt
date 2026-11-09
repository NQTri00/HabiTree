package com.mamxanh.shared.di

import com.mamxanh.shared.data.GameDatabase
import com.mamxanh.shared.data.IosGameDatabase
import com.mamxanh.shared.feature.garden.domain.GardenRepository
import com.mamxanh.shared.feature.home.domain.HomeDashboardRepository
import com.mamxanh.shared.feature.shop.domain.ShopRepository
import com.mamxanh.shared.feature.stats.domain.StatisticsRepository
import com.mamxanh.shared.feature.tasks.domain.TaskRepository
import com.mamxanh.shared.feature.tree.domain.TreeRepository
import com.mamxanh.shared.network.NetworkConfig
import com.mamxanh.shared.network.RemoteService
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.onClose

/** An isolated native graph; call blocking repositories on a worker and close after their work ends. */
class IosGarden(databaseName: String = "mam_xanh.db", backendBaseUrl: String? = null) {
    private val application = koinApplication {
        modules(
            sharedModule(NetworkConfig(backendBaseUrl)),
            module {
                single { IosGameDatabase(databaseName) } onClose { it?.close() }
                single<GameDatabase> { get<IosGameDatabase>() }
            }
        )
    }

    val home: HomeDashboardRepository get() = application.koin.get()
    val tasks: TaskRepository get() = application.koin.get()
    val garden: GardenRepository get() = application.koin.get()
    val shop: ShopRepository get() = application.koin.get()
    val statistics: StatisticsRepository get() = application.koin.get()
    val tree: TreeRepository get() = application.koin.get()
    val remote: RemoteService get() = application.koin.get()

    fun close() = application.close()
}
