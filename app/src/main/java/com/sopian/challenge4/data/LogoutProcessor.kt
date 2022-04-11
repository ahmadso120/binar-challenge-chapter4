package com.sopian.challenge4.data

import android.content.Context
import com.sopian.challenge4.data.source.local.AppLocalData

class LogoutProcessor(
    private val context: Context,
    private val notesRepository: INotesRepository
) {

    fun execute() {
        AppLocalData.dropUserLoggedIn(context)
        clearExistingNote()
    }

    private fun clearExistingNote() {
        notesRepository.getNotes {
            it.forEach { note ->
                notesRepository.deleteNote(note)
            }
        }
    }
}