package com.ihdyo.postit.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ihdyo.postit.R
import com.ihdyo.postit.databinding.ActivityProfileBinding
import com.ihdyo.postit.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sessionManager: SessionManager

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ProfileActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        initUI()
        initAction()
    }

    private fun initUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.profile)

        binding.tvUserName.text = sessionManager.getUserName
        binding.tvUserEmail.text = sessionManager.getEmail
    }

    private fun initAction() {
        binding.btnLogout.setOnClickListener {
            openLogoutDialog()
        }
    }

    private fun openLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirmation))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                sessionManager.clearPreferences()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}