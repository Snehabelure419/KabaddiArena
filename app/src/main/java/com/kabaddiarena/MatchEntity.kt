package com.kabaddiarena

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_table")
data class MatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val raidAttempts: Int = 0,
    val raidSuccess: Int = 0,
    val tacklePoints: Int = 0,
    val totalPoints: Int = 0,
    val successRate: Int = 0
)