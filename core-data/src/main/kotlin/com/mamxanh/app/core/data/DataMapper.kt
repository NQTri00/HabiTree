package com.mamxanh.app.core.data

interface DataMapper<Domain, Data> {
    fun toDomain(value: Data): Domain

    fun toData(value: Domain): Data
}
