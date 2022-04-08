package com.sopian.challenge4.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sopian.challenge4.R
import com.sopian.challenge4.data.NotesRepository
import com.sopian.challenge4.data.source.NotesLocalDataSource
import com.sopian.challenge4.data.source.local.room.NoteDatabase
import com.sopian.challenge4.databinding.NoteEntryDialogBinding
import com.sopian.challenge4.model.Note
import com.sopian.challenge4.utils.AppExecutors

class NoteEntryDialogFragment : DialogFragment() {

    private var _binding: NoteEntryDialogBinding? = null
    private val binding get() = _binding!!

    private val args: NoteEntryDialogFragmentArgs by navArgs()

    private enum class EditingState {
        NEW_NOTE,
        EXISTING_NOTE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NoteEntryDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteDatabase = NoteDatabase.getInstance(requireContext())

        val appExecutors = AppExecutors()

        val localDataSource = NotesLocalDataSource.getInstance(noteDatabase.noteDao())

        val notesRepository = NotesRepository.getInstance(localDataSource, appExecutors)

        var id: Int? = null

        val editingState =
            if (args.itemId > 0) EditingState.EXISTING_NOTE
            else EditingState.NEW_NOTE

        if (editingState == EditingState.EXISTING_NOTE) {
            notesRepository.getNote(args.itemId) {
                binding.label.text = resources.getString(R.string.label_edit_note_dialog)
                binding.doneButton.text = resources.getString(R.string.title_update)
                binding.noteEdt.setText(it.note)
                binding.titleEdt.setText(it.title)
                id = it.id
            }
        }

        with(binding) {
            cancelButton.setOnClickListener {
                dismiss()
            }

            doneButton.setOnClickListener {
                val data = Note(
                    id ?: 0,
                    titleEdt.text.toString().trim(),
                    noteEdt.text.toString().trim()
                )
                notesRepository.addNote(data)

//                setFragmentResult("entryNoteKey", bundleOf("resultKey" to true))
                val navController = findNavController()
                val savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
                savedStateHandle.set("resultKey", true)

                dismiss()
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NoteDatabase.closeDb()
        NoteDatabase.destroyInstance()
        _binding = null
    }
}