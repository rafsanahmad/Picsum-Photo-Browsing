package com.rafsan.picsumphotoapp.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rafsan.picsumphotoapp.base.BaseActivity
import com.rafsan.picsumphotoapp.databinding.ActivityMainBinding
import com.rafsan.picsumphotoapp.ui.main.adapter.ImageAdapter
import com.rafsan.picsumphotoapp.utils.Constants.Companion.QUERY_PER_PAGE
import com.rafsan.picsumphotoapp.utils.EndlessRecyclerOnScrollListener
import com.rafsan.picsumphotoapp.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var imageAdapter: ImageAdapter
    private lateinit var onScrollListener: EndlessRecyclerOnScrollListener

    override fun onViewReady(savedInstanceState: Bundle?) {
        super.onViewReady(savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupObservers()
    }

    override fun setBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private fun setupUI() {
        binding.itemErrorMessage.btnRetry.setOnClickListener {
            mainViewModel.fetchImages()
            hideErrorMessage()
        }

        // scroll listener for recycler view
        onScrollListener = object : EndlessRecyclerOnScrollListener(QUERY_PER_PAGE) {
            override fun onLoadMore() {
                mainViewModel.fetchImages()
            }
        }
        //Swipe refresh listener
        val refreshListener = SwipeRefreshLayout.OnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            mainViewModel.refresh()
            mainViewModel.fetchImages()
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener);
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter()
        val rLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rLayoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.rvImages.apply {
            adapter = imageAdapter
            layoutManager = rLayoutManager
            addOnScrollListener(onScrollListener)
        }
        imageAdapter.setOnItemClickListener { item ->
            //Navigate to detail

        }
    }

    private fun setupObservers() {
        mainViewModel.imageResponse.observe(this, Observer { response ->
            when (response) {
                is NetworkResult.Success -> {
                    hideProgressBar()
                    hideErrorMessage()
                    response.data?.let { images ->
                        imageAdapter.differ.submitList(images)
                    }
                }

                is NetworkResult.Loading -> {
                    showProgressBar()
                }

                is NetworkResult.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        showErrorMessage(it)
                    }
                }
            }
        })

        mainViewModel.errorToast.observe(this, Observer { value ->
            if (value.isNotEmpty()) {
                Toast.makeText(this@MainActivity, value, Toast.LENGTH_LONG).show()
            } else {
                mainViewModel.hideErrorToast()
            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.itemErrorMessage.errorCard.visibility = View.VISIBLE
        binding.itemErrorMessage.tvErrorMessage.text = message
        onScrollListener.isError = true
    }

    private fun hideErrorMessage() {
        binding.itemErrorMessage.errorCard.visibility = View.GONE
        onScrollListener.isError = false
    }
}