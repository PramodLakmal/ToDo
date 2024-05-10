package com.pramodlakmal.taskmanager.model

import androidx.annotation.Keep

@Keep
data class Today(
    val date: Int,
    val day: String,
    val monthYear: String
)