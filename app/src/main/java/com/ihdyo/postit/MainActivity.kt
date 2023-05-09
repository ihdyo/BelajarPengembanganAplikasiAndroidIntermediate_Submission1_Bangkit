package com.ihdyo.postit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ihdyo.postit.R.string
import com.ihdyo.postit.data.remote.ApiResponse
import com.ihdyo.postit.databinding.ActivityMainBinding
import com.ihdyo.postit.activity.ProfileActivity
import com.ihdyo.postit.activity.StoryAdapter
import com.ihdyo.postit.viewmodel.StoryViewModel
import com.ihdyo.postit.activity.AddStoryActivity
import com.ihdyo.postit.utils.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val storyViewModel: StoryViewModel by viewModels()

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

    private lateinit var pref: SessionManager
    private var token: String? = null

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding?.root)
        pref = SessionManager(this)
        token = pref.getToken

        initAction()
        initUI()

        getAllStories("Bearer $token")
    }

    private fun initUI() {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
    }

    private fun initAction() {
        binding.fabNewStory.setOnClickListener {
            AddStoryActivity.start(this)
        }
    }

    private fun getAllStories(token: String) {
        storyViewModel.getAllStories(token).observe(this) { response ->
            when (response) {
                is ApiResponse.Loading -> isLoading(true)
                is ApiResponse.Success -> {
                    isLoading(false)
                    val adapter = StoryAdapter(response.data.listStory)
                    binding.rvStories.adapter = adapter
                }
                is ApiResponse.Error -> isLoading(false)
                else -> {
                    Timber.e(getString(string.unknown_state))
                }
            }
        }
    }

    private fun isLoading(loading: Boolean) {
        if (loading) {
            binding.apply {
                shimmerLoading.visibility = View.VISIBLE
                shimmerLoading.startShimmer()
                rvStories.visibility = View.INVISIBLE
            }
        } else {
            binding.apply {
                rvStories.visibility = View.VISIBLE
                shimmerLoading.stopShimmer()
                shimmerLoading.visibility = View.INVISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getAllStories("Bearer $token")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menuProfile -> {
                ProfileActivity.start(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}