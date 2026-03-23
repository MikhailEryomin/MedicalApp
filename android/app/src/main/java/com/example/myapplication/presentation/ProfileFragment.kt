package com.example.myapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.R
import com.example.myapplication.SessionManager
import com.example.myapplication.databinding.FragmentPatientDetailsBinding
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.domain.UserRole

class ProfileFragment: Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("There is no binding")
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this) {
            binding.apply {
                tvProfileEmail.text = it.email
                tvProfileName.text = "${it.firstName} ${it.lastName}"
                tvProfileInitials.text = "${it.firstName.first()} ${it.lastName.first()}"
                tvProfileRole.text = it.roleLabel

                // ЛОГИКА СКРЫТИЯ НАСТРОЕК УВЕДОМЛЕНИЙ ДЛЯ ВРАЧА
                // Если roleLabel равен "Doctor" (как ты задал во ViewModel)
                if (it.roleLabel == UserRole.DOCTOR.displayName) {
                    // Прячем заголовок "Notifications" и саму карточку со свитчами
                    cardNotifications.visibility = View.GONE
                    notifictationsTitle.visibility = View.GONE
                } else {
                    // Для Пациента показываем
                    cardNotifications.visibility = View.VISIBLE
                    notifictationsTitle.visibility = View.VISIBLE
                }
            }
        }
        viewModel.isLoggedOut.observe(this) { loggedOut ->
            if (loggedOut) findNavController().navigate(R.id.action_profile_to_auth)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBackSettings.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            findNavController().navigate(R.id.action_profile_to_auth)
        }

        // Восстанавливаем состояние при открытии экрана
        binding.switchReminders.isChecked = SessionManager.isRemindersEnabled
        binding.switchPush.isChecked = SessionManager.isPushEnabled

        // Сохраняем при клике
        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            SessionManager.isRemindersEnabled = isChecked
            if (isChecked) {
                // TODO: Запустить AlarmManager
            } else {
                // TODO: Отменить AlarmManager
            }
        }

//        binding.switchPush.setOnCheckedChangeListener { _, isChecked ->
//            SessionManager.isPushEnabled = isChecked
//            if (isChecked) {
//                // TODO: Подписаться на Firebase
//            } else {
//                // TODO: Отписаться от Firebase
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}