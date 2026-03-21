package com.example.myapplication.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentDoctorHomeBinding

class DoctorHomeFragment: Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: DoctorHomeViewModel

    private val adapter = PatientListAdapter { patient ->
        val action = DoctorHomeFragmentDirections.actionDoctorHomeToPatientDetails(patient.id)
        findNavController().navigate(action)
    }

    private val globalAdapter = GlobalPatientAdapter { patient ->
        viewModel.attachPatient(patient.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DoctorHomeViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.displayedPatients.observe(this) { patients ->
            if (binding.rvPatients.adapter == adapter) {
                adapter.submitList(patients)
            } else {
                globalAdapter.submitList(patients)
            }
        }

        viewModel.currentTab.observe(this) { tab ->
            if (tab == DoctorHomeTab.MY_PATIENTS) {
                binding.tabMyPatients.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_selected)
                binding.tabGlobalSearch.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_unselected)
                binding.rvPatients.adapter = adapter
            } else {
                binding.tabGlobalSearch.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_selected)
                binding.tabMyPatients.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tab_unselected)
                binding.rvPatients.adapter = globalAdapter
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabMyPatients.setOnClickListener {
            viewModel.setTab(DoctorHomeTab.MY_PATIENTS, binding.etSearch.text.toString())
        }
        binding.tabGlobalSearch.setOnClickListener {
            viewModel.setTab(DoctorHomeTab.GLOBAL_SEARCH, binding.etSearch.text.toString())
        }

        binding.etSearch.addTextChangedListener { text ->
            viewModel.onSearchQueryChanged(text.toString())
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("refresh_patients")?.observe(this) {
            if (it) {
                viewModel.loadMyPatients()
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh_patients")
            }
        }
        bindViews()
    }

    private fun bindViews() {

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_doctorHome_to_profile)
        }

        //recycleView
        binding.rvPatients.adapter = adapter
        binding.rvPatients.layoutManager = LinearLayoutManager(context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}