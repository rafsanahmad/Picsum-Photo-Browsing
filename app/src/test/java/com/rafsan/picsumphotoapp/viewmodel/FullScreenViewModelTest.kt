package com.rafsan.picsumphotoapp.viewmodel

import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import com.rafsan.picsumphotoapp.ext.toBitmap
import com.rafsan.picsumphotoapp.ui.detail.FullScreenViewModel
import com.rafsan.picsumphotoapp.util.FakeDataUtil
import com.rafsan.picsumphotoapp.util.MainCoroutineRule
import com.rafsan.picsumphotoapp.util.provideFakeCoroutinesDispatcherProvider
import com.rafsan.picsumphotoapp.util.runBlockingTest
import com.rafsan.picsumphotoapp.utils.NetworkHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.net.URL

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class FullScreenViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var responseUri: Observer<Uri>

    private val testDispatcher = coroutineRule.testDispatcher
    private lateinit var viewModel: FullScreenViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = FullScreenViewModel(
            networkHelper = networkHelper,
            coroutinesDispatcherProvider = provideFakeCoroutinesDispatcherProvider(testDispatcher)
        )
        responseUri = Observer { }
    }

    @Test
    fun `when image download returns uri`() {
        coroutineRule.runBlockingTest {
            val item = FakeDataUtil.getFakeImage()
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)
            viewModel.imageUri.observeForever(responseUri)

            //When
            viewModel.downloadImage(item)

            //Then
            assertThat(viewModel.imageUri.value).isNotNull()
        }
    }

    @Test
    fun `Url to Bitmap returns not null`() {
        val item = FakeDataUtil.getFakeImage()
        val url = URL(item.download_url)
        val bitmap = url.toBitmap()
        assertThat(bitmap).isNotNull()
    }

    @After
    fun release() {
        Mockito.framework().clearInlineMocks()
        viewModel.imageUri.removeObserver(responseUri)
    }
}