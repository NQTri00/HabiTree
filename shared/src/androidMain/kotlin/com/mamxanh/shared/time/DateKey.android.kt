package com.mamxanh.shared.time

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun currentDateKey(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
