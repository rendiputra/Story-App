package com.rendiputra.storyapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rendiputra.storyapp.R
import com.rendiputra.storyapp.databinding.FragmentRegisterBinding
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.util.validateEmail
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        observeRegisterState()
    }

    private fun setupButtons() {
        arrayOf(binding.btnRegister, binding.btnLogin)
            .forEach { button -> button.setOnClickListener(this) }
    }

    private fun observeRegisterState() {
        authViewModel.registerState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> toggleLoading(true)
                is Response.Empty -> toggleLoading(false)
                is Response.Success -> {
                    toggleLoading(false)
                    Snackbar.make(binding.root, response.data.message, Snackbar.LENGTH_LONG).show()
                    navigateToLoginScreen()
                }
                is Response.Error -> {
                    toggleLoading(false)
                    response.message?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_register -> register()
            R.id.btn_login -> navigateToLoginScreen()
        }
    }

    private fun navigateToLoginScreen() {
        parentFragmentManager.popBackStack()
    }

    private fun register() {
        if (validateFormLogin()) return

        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        authViewModel.register(
            name = name,
            email = email,
            password = password
        )
    }

    private fun validateFormLogin(): Boolean {

        if (binding.edtName.text?.isEmpty() == true) {
            binding.tilName.error = getString(R.string.validation_error_name)
            return true
        } else binding.tilName.isErrorEnabled = false

        if (binding.edtEmail.text?.isEmpty() == true) {
            binding.tilEmail.error = getString(R.string.validation_error_email)
            return true
        } else binding.tilEmail.isErrorEnabled = false

        if (!validateEmail(binding.edtEmail.text.toString())) {
            binding.tilEmail.error = getString(R.string.email_not_valid)
            return true
        } else binding.tilEmail.isErrorEnabled = false

        if (binding.edtPassword.text?.isEmpty() == true) {
            binding.tilPassword.error = getString(R.string.validation_error_password)
            return true
        } else binding.tilPassword.isErrorEnabled = false

        if (binding.edtPassword.text?.length!! < 6) {
            binding.tilPassword.error = getString(R.string.password_smaller_6)
            return true
        } else binding.tilPassword.isErrorEnabled = false

        return false
    }

    private fun toggleLoading(state: Boolean) {
        binding.btnRegister.isEnabled = !state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}