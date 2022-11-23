package com.rendiputra.storyapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.rendiputra.storyapp.R
import com.rendiputra.storyapp.databinding.ActivityDetailBinding
import com.rendiputra.storyapp.domain.Response
import com.rendiputra.storyapp.domain.Story
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "extra_id"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_TOKEN = "extra_token"
    }

    private lateinit var binding: ActivityDetailBinding

    private val detailViewModel: DetailViewModel by viewModels()

    private lateinit var storyId: String
    private lateinit var storyName: String
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyId = intent.getStringExtra(EXTRA_ID) ?: ""
        storyName = intent.getStringExtra(EXTRA_NAME) ?: ""
        token = intent.getStringExtra(EXTRA_TOKEN) ?: ""

        setupToolbar()

        detailViewModel.getDetailStory(
            "Bearer $token",
            storyId
        )

        observeDetailStory()
    }

    private fun setupToolbar() {
        binding.toolbar.title = storyName
        binding.toolbar.setOnClickListener { finish() }
    }

    private fun setupDetailStory(story: Story) {
        binding.ivStory.load(story.photoUrl) {
            placeholder(R.drawable.ic_image)
            placeholder(R.drawable.ic_image_error)
        }
        binding.tvName.text = story.name
        binding.tvDescription.text = story.description
    }

    private fun observeDetailStory() {
        detailViewModel.story.observe(this) { response ->
            when (response) {
                is Response.Loading -> toggleLoading(true)
                is Response.Empty -> toggleLoading(false)
                is Response.Success -> {
                    toggleLoading(false)
                    setupDetailStory(response.data)
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
}