package com.drsync.storyapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.drsync.storyapp.R
import com.drsync.storyapp.databinding.ActivityMainBinding
import com.drsync.storyapp.models.User
import com.drsync.storyapp.ui.login.LoginActivity
import com.drsync.storyapp.ui.inputstory.InputStoryActivity
import com.drsync.storyapp.util.Constant.TAG
import com.drsync.storyapp.util.Constant.tokenBearer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val INSERT_RESULT = 200
    }

    private lateinit var binding: ActivityMainBinding
    private  var user: User? = null
    private lateinit var mAdapter: MainAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getUser {
            user = it
        }

        getStories()

        binding.fabTambah.setOnClickListener {
            Intent(this@MainActivity, InputStoryActivity::class.java).also {
                launcherInsertStory.launch(it)
            }
        }

        setupRecyclerData()
        showLoading()
    }

    private fun getStories() {
        val token = user?.tokenBearer.toString()
        viewModel.getStories(token) {
            Log.d(TAG, "onCreate: $it")
            mAdapter.submitList(it)
        }
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this@MainActivity){
            binding.progressBar.isVisible = it
        }
    }

    private fun setupRecyclerData() {
        mAdapter = MainAdapter()
        binding.rvStory.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                user?.let { viewModel.logout(it) }
                Intent(this@MainActivity, LoginActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
        return true
    }

    private fun reGetStory() {
        getStories()
        setupRecyclerData()
    }

    private val launcherInsertStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == INSERT_RESULT){
            reGetStory()
        }
    }

}