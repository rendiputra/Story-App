package com.rendiputra.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.rendiputra.storyapp.R
import com.rendiputra.storyapp.databinding.FragmentLoginBinding
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.ui.home.MainActivity
import com.rendiputra.storyapp.util.validateEmail
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        observeLoginState()
    }

    private fun setupButtons() {
        arrayOf(binding.btnLogin, binding.btnRegister)
            .forEach { button -> button.setOnClickListener(this) }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> login()
            R.id.btn_register -> navigateToRegisterScreen()
        }
    }

    private fun observeLoginState() {
        authViewModel.loginState.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Loading -> toggleLoading(true)
                is Response.Empty -> toggleLoading(false)
                is Response.Success -> {
                    toggleLoading(false)
                    authViewModel.updateAuthToken(response.data.token)
                    navigateToHomeScreen()
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

    private fun navigateToRegisterScreen() {
        parentFragmentManager.commit {
            replace<RegisterFragment>(R.id.fragment_container_view)
            addToBackStack("register")
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun login() {
        if (validateFormLogin()) return

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        authViewModel.login(
            email = email,
            password = password
        )
    }

    private fun validateFormLogin(): Boolean {

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
        binding.btnLogin.isEnabled = !state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}