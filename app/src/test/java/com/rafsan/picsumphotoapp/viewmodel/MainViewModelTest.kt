package com.rafsan.picsumphotoapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import com.rafsan.picsumphotoapp.data.model.ImageList
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.network.repository.ImageListRepository
import com.rafsan.picsumphotoapp.ui.main.MainViewModel
import com.rafsan.picsumphotoapp.util.FakeDataUtil
import com.rafsan.picsumphotoapp.util.MainCoroutineRule
import com.rafsan.picsumphotoapp.util.provideFakeCoroutinesDispatcherProvider
import com.rafsan.picsumphotoapp.util.runBlockingTest
import com.rafsan.picsumphotoapp.utils.NetworkHelper
import com.rafsan.picsumphotoapp.utils.NetworkResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class MainViewModelTest {
    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var picsumApi: PicsumApi

    @Mock
    private lateinit var networkHelper: NetworkHelper

    @Mock
    private lateinit var imageListRepo: ImageListRepository

    private val testDispatcher = coroutineRule.testDispatcher

    @Mock
    private lateinit var responseObserver: Observer<NetworkResult<ImageList>>
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(
            repository = imageListRepo,
            networkHelper = networkHelper,
            coroutinesDispatcherProvider = provideFakeCoroutinesDispatcherProvider(testDispatcher)
        )
    }

    @Test
    fun `when calling for results then return loading`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)
            viewModel.imageResponse.observeForever(responseObserver)
            whenever(imageListRepo.getImages(1))
                .thenReturn(NetworkResult.Loading())

            //When
            viewModel.fetchImages()

            //Then
            assertThat(viewModel.imageResponse.value).isNotNull()
            assertThat(viewModel.imageResponse.value?.data).isNull()
            assertThat(viewModel.imageResponse.value?.message).isNull()
        }
    }

    @Test
    fun `test if list is loaded with picsum response`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)

            viewModel.imageResponse.observeForever(responseObserver)
            // Stub repository with fake images
            whenever(imageListRepo.getImages(1))
                .thenAnswer { (FakeDataUtil.getFakeImagesResponse()) }

            //When
            viewModel.fetchImages()

            //then
            assertThat(viewModel.imageResponse.value).isNotNull()
            val images = viewModel.imageResponse.value?.data
            assertThat(images?.isNotEmpty())
            // compare the response with fake list
            assertThat(images).hasSize(FakeDataUtil.getFakeImages().size)
            // compare the data and also order
            assertThat(images).containsExactlyElementsIn(
                FakeDataUtil.getFakeImages()
            ).inOrder()
        }
    }

    @Test
    fun `test for failure`() {
        coroutineRule.runBlockingTest {
            whenever(networkHelper.isNetworkConnected())
                .thenReturn(true)
            // Stub repository with fake favorites
            whenever(imageListRepo.getImages(1))
                .thenAnswer { NetworkResult.Error("Error occurred", null) }

            //When
            viewModel.fetchImages()

            //then
            val response = viewModel.imageResponse.value
            assertThat(response?.message).isNotNull()
            assertThat(response?.message).isEqualTo("Error occurred")
        }
    }

    @After
    fun release() {
        Mockito.framework().clearInlineMocks()
        viewModel.imageResponse.removeObserver(responseObserver)
    }
}