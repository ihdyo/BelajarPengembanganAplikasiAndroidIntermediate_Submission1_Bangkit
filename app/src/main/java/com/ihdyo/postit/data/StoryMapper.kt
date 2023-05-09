package com.ihdyo.postit.data

import com.ihdyo.postit.data.local.entity.StoryEntity
import com.ihdyo.postit.data.model.Story

fun storyToStoryEntity(story: Story): StoryEntity {
    return StoryEntity(
        id = story.id,
        photoUrl = story.photoUrl
    )
}