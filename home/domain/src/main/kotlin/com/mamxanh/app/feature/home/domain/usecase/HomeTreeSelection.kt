package com.mamxanh.app.feature.home.domain.usecase

interface HomeTreeSelectionRepository {
    fun read(): Long

    fun write(treeId: Long)
}

class HomeTreeSelection(
    private val repository: HomeTreeSelectionRepository,
) {
    fun savedId(): String = repository.read().toString()

    fun save(id: String) {
        repository.write(id.toLongOrNull() ?: 0L)
    }

    fun resolve(
        currentId: String,
        availableIds: List<String>,
    ): String = currentId.takeIf(availableIds::contains) ?: availableIds.firstOrNull() ?: "0"
}
