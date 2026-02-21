package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentDoctorHomeBinding
import com.example.myapplication.databinding.FragmentLoginBinding

class DoctorHomeFragment: Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}