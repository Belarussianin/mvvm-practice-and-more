package com.example.mvvm_practice.ui.storage.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalUserDao {

    @Query("SELECT * FROM local_user_table ORDER BY user_id ASC")
    fun getLocalUsersASC(): Flow<List<LocalUser>>

    @Query("SELECT * FROM local_user_table ORDER BY user_id DESC")
    fun getLocalUsersDESC(): Flow<List<LocalUser>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: LocalUser)

    @Query("DELETE FROM local_user_table WHERE user_id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM local_user_table")
    suspend fun deleteAll()
}