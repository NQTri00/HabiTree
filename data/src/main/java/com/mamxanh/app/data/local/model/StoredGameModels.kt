package com.mamxanh.app.data.local.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

typealias Task = com.mamxanh.shared.model.Task
typealias Species = com.mamxanh.shared.model.Species
typealias Tree = com.mamxanh.shared.model.Tree
typealias Garden = com.mamxanh.shared.model.Garden
typealias Statistics = com.mamxanh.shared.model.Statistics
typealias TreeWateringResult = com.mamxanh.shared.model.TreeWateringResult

val catalog get() = com.mamxanh.shared.model.catalog
fun today() = com.mamxanh.shared.time.currentDateKey()
fun key(date: Date) = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date)
