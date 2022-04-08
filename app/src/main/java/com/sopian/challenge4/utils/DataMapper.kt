package com.sopian.challenge4.utils

import com.sopian.challenge4.data.source.local.entity.NoteEntity
import com.sopian.challenge4.model.Note

object DataMapper {
    fun mapEntitiesToDomain(input: List<NoteEntity>): List<Note> =
        input.map {
            Note(
                it.id,
                it.title,
                it.note
            )
        }

    fun mapEntityToDomain(input: NoteEntity) = Note(
        input.id,
        input.title,
        input.note
    )

    fun mapDomainToEntity(input: Note) = NoteEntity(
        input.id,
        input.title,
        input.note
    )
}