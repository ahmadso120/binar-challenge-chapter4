package com.sopian.challenge4.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sopian.challenge4.R
import com.sopian.challenge4.data.INotesRepository
import com.sopian.challenge4.data.LogoutProcessor
import com.sopian.challenge4.data.NotesRepository
import com.sopian.challenge4.data.source.NotesLocalDataSource
import com.sopian.challenge4.data.source.local.AppLocalData
import com.sopian.challenge4.data.source.local.room.NoteDatabase
import com.sopian.challenge4.databinding.FragmentHomeBinding
import com.sopian.challenge4.model.Note
import com.sopian.challenge4.utils.AppExecutors
import com.sopian.challenge4.utils.showMaterialAlertDialog

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val noteDatabase = NoteDatabase.getInstance(requireContext())

        val appExecutors = AppExecutors()

        val localDataSource = NotesLocalDataSource.getInstance(noteDatabase.noteDao())

        val notesRepository = NotesRepository.getInstance(localDataSource, appExecutors)

        val navController = findNavController()
        val navBackStackEntry = navController.getBackStackEntry(R.id.homeFragment)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains("resultKey")
            ) {
                val result = navBackStackEntry.savedStateHandle.get<Boolean>("resultKey")
                result?.let {
                    fetchData(notesRepository as INotesRepository)
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })

        binding.logoutTv.setOnClickListener {
            LogoutProcessor(requireContext(), notesRepository).execute()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.fab.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToNoteEntryDialogFragment()
            findNavController().navigate(action)
        }

        val username = AppLocalData.getUserLoggedIn(requireContext())?.username
        binding.welcome.text = HtmlCompat
            .fromHtml(
                "Welcome, <strong>$username!</strong>",
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        fetchData(notesRepository)
    }

    private fun fetchData(notesRepository: INotesRepository) {
        notesRepository.getNotes {
            setupAdapter(it, notesRepository)
        }
    }

    private fun setupAdapter(
        notes: List<Note>,
        notesRepository: INotesRepository
    ) {
        val adapter = NoteAdapter(
            onEdit = { note ->
                val action = HomeFragmentDirections
                    .actionHomeFragmentToNoteEntryDialogFragment(note.id)
                findNavController().navigate(action)
            },
            onDelete = { note ->
                requireActivity().showMaterialAlertDialog(
                    positiveButtonLable = "Ok",
                    negativeButtonLable = "Cancel",
                    title = "Confirm",
                    message = "Are you sure you want to delete this entry?",
                    actionOnPositiveButton = {
                        notesRepository.deleteNote(note)
                        refreshFragment(notesRepository)
                    }
                )
            }
        )

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvNoteList.layoutManager = layoutManager
        binding.rvNoteList.adapter = adapter
        adapter.submitList(notes)
    }

    private fun refreshFragment(notesRepository: INotesRepository) {
        val fragmentManager = (requireContext() as? AppCompatActivity)?.supportFragmentManager
        val currentFragment = fragmentManager?.findFragmentById(R.id.nav_host_fragment)
        val transaction = fragmentManager?.beginTransaction()
        currentFragment?.let {
            transaction?.detach(it)?.attach(it)?.commit()
        }

        fetchData(notesRepository)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NoteDatabase.destroyInstance()
        _binding = null
    }
}