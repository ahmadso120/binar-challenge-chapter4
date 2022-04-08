package com.sopian.challenge4.data.source.local.room

import androidx.room.*
import com.sopian.challenge4.data.source.local.entity.NoteEntity

@Dao
interface NoteDao {

    @Query("SELECT * FROM note ORDER BY id DESC")
    fun getNotes(): List<NoteEntity>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNote(id: Int): NoteEntity

    @Insert
    fun insertNote(noteEntity: NoteEntity)

    @Update
    fun updateNote(noteEntity: NoteEntity)

    @Delete
    fun deleteNote(noteEntity: NoteEntity)
}
