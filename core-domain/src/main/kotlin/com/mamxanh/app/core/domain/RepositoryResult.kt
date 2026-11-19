package com.mamxanh.app.core.domain

sealed interface RepositoryResult<out Value> {
    data class Success<Value>(val value: Value) : RepositoryResult<Value>
    data class Error(val message: String) : RepositoryResult<Nothing>
}
