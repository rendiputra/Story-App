package com.rendiputra.storyapp.data.network.response

import com.rendiputra.storyapp.domain.Story
import com.google.gson.annotations.SerializedName

data class StoryItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("lon")
	val lon: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)

fun List<StoryItem>.asDomain(): List<Story> =
	map { storyItem ->
		storyItem.asDomain()
	}

fun StoryItem.asDomain(): Story =
	Story(
		id = id ?: "",
		name = name ?: "",
		description = description ?: "",
		photoUrl = photoUrl ?: "",
		createdAt = createdAt ?: ""
	)
