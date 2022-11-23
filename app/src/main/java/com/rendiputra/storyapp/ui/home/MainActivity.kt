package com.rendiputra.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.rendiputra.storyapp.R
import com.rendiputra.storyapp.adapter.StoryListAdapter
import com.rendiputra.storyapp.databinding.ActivityMainBinding
import com.rendiputra.storyapp.databinding.ItemStoryBinding
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.domain.Story
import com.rendiputra.storyapp.ui.add_story.AddStoryActivity
import com.rendiputra.storyapp.ui.auth.AuthActivity
import com.rendiputra.storyapp.ui.auth.AuthViewModel
import com.rendiputra.storyapp.ui.detail.DetailActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), StoryListAdapter.OnStoryClick {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyListAdapter: StoryListAdapter

    private val authViewModel: AuthViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObserver()

        observeAuthToken()
        observeStories()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> logout()
            }
            true
        }
    }

    private fun setupRecyclerView() {
        storyListAdapter = StoryListAdapter(this)
        binding.rvStory.adapter = storyListAdapter
    }

    private fun setupObserver() {
        binding.fabAddNewStory.setOnClickListener { navigateToAddNewStoryScreen() }
    }

    private fun observeAuthToken() {
        authViewModel.authToken.observe(this) { authToken ->
            if (authToken.isEmpty()) {
                navigateToAuthScreen()
            } else {
                token = authToken
                mainViewModel.getStories("Bearer $authToken")
            }
        }
    }

    private fun observeStories() {
        mainViewModel.stories.observe(this) { response ->
            when (response) {
                is Response.Loading -> toggleLoading(true)
                is Response.Empty -> {
                    toggleLoading(true)
                    Snackbar.make(binding.root, getString(R.string.empty_story), Snackbar.LENGTH_INDEFINITE).show()
                }
                is Response.Success -> {
                    toggleLoading(false)
                    storyListAdapter.submitList(response.data)
                }
                is Response.Error -> {
                    toggleLoading(false)
                    response.message?.let {
                        Snackbar.make(binding.root, it, Snackbar.LENGTH_INDEFINITE).show()
                    }
                }
            }
        }
    }

    private fun toggleLoading(state: Boolean) {
        val progressVisibility = if (state) View.VISIBLE else View.GONE
        binding.progressBar.visibility = progressVisibility
    }

    private fun navigateToAuthScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToAddNewStoryScreen() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.logout_message)
            .setPositiveButton(R.string.yes) { _, _ -> authViewModel.removeAuthToken() }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
            .show()
    }

    override fun onStoryClicked(story: Story, binding: ItemStoryBinding) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra(DetailActivity.EXTRA_ID, story.id)
            putExtra(DetailActivity.EXTRA_NAME, story.name)
            putExtra(DetailActivity.EXTRA_TOKEN, token)
        }
        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
            this,
            Pair(binding.ivStory, "iv_story"),
            Pair(binding.tvStory, "tv_story")
        )
        startActivity(intent, optionsCompat.toBundle())
    }
}