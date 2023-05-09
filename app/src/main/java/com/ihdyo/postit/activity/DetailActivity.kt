package com.ihdyo.postit.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ihdyo.postit.R
import com.ihdyo.postit.data.model.Story
import com.ihdyo.postit.databinding.ActivityDetailBinding
import com.ihdyo.postit.utils.ConstVal
import com.ihdyo.postit.utils.ext.setImageUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var story: Story

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initIntent()
        initUI()
    }

    private fun initUI() {
        with(binding) {
            imgStoryThumbnail.setImageUrl(story.photoUrl, true)
            tvStoryTitle.text = story.name
            tvStoryDesc.text = story.description
        }
        title = getString(R.string.detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initIntent() {
        story = intent.getParcelableExtra(ConstVal.BUNDLE_KEY_STORY)!!
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}