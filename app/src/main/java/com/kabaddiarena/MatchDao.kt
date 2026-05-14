package com.kabaddiarena

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MatchDao {

    @Insert
    suspend fun insertMatch(match: MatchEntity)

    @Query("SELECT * FROM match_table ORDER BY id DESC")
    suspend fun getAllMatches(): List<MatchEntity>

    @Query("DELETE FROM match_table")
    suspend fun deleteAllMatches()
}
