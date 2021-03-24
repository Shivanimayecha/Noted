package com.example.noted.RoomDB

import androidx.room.*

@Dao
interface NoteCategoryDao {

    @Insert
    suspend fun addNoteCategory(noteCategory: NoteCategory)

    @Query("SELECT * FROM notecategory ORDER BY cat_id DESC")
    suspend fun getNoteCategory(): List<NoteCategory>

    @Update
    suspend fun updateNoteCat(noteCategory: NoteCategory)

    @Delete
    fun deleteNoteCat(noteCategory: NoteCategory)
}