package com.rafsan.picsumphotoapp.paging

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.nhaarman.mockitokotlin2.whenever
import com.rafsan.picsumphotoapp.data.model.ImageListItem
import com.rafsan.picsumphotoapp.data.paging.datasource.ImagePagingDataSource
import com.rafsan.picsumphotoapp.network.api.PicsumApi
import com.rafsan.picsumphotoapp.util.FakeDataUtil
import com.rafsan.picsumphotoapp.util.MainCoroutineRule
import com.rafsan.picsumphotoapp.util.runBlockingTest
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class PagingDataSourceTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var coroutineRule = MainCoroutineRule()

    @Mock
    private lateinit var picsumApi: PicsumApi

    lateinit var pagingSource: ImagePagingDataSource
    val fakeList = FakeDataUtil.getFakeImages()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        pagingSource = ImagePagingDataSource(picsumApi)
    }


    @Test
    fun `image paging source refresh - success`() = coroutineRule.runBlockingTest {
        whenever(picsumApi.getImages(0))
            .thenReturn(FakeDataUtil.getFakeImagesResponse())
        val expectedResult = PagingSource.LoadResult.Page(
            data = listOf(fakeList[0], fakeList[1]),
            prevKey = null,
            nextKey = 1
        )
        assertEquals(
            expectedResult, pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `image paging source append - success`() = coroutineRule.runBlockingTest {
        whenever(picsumApi.getImages(0))
            .thenReturn(FakeDataUtil.getFakeImagesResponse())
        val expectedResult = PagingSource.LoadResult.Page(
            data = listOf(fakeList[0], fakeList[1]),
            prevKey = null,
            nextKey = 1
        )
        assertEquals(
            expectedResult, pagingSource.load(
                PagingSource.LoadParams.Append(
                    key = 0,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }


    @Test
    fun `image paging source prepend - success`() = coroutineRule.runBlockingTest {
        whenever(picsumApi.getImages(0))
            .thenReturn(FakeDataUtil.getFakeImagesResponse())
        val expectedResult = PagingSource.LoadResult.Page(
            data = listOf(fakeList[0], fakeList[1]),
            prevKey = null,
            nextKey = 1
        )
        assertEquals(
            expectedResult, pagingSource.load(
                PagingSource.LoadParams.Prepend(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `image paging source load - failure - http error`() = coroutineRule.runBlockingTest {
        val error = RuntimeException("404", Throwable())
        whenever(picsumApi.getImages(0))
            .thenThrow(error)
        val expectedResult = PagingSource.LoadResult.Error<Int, ImageListItem>(error)
        assertEquals(
            expectedResult, pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `image paging source load - failure - received null`() = coroutineRule.runBlockingTest {
        whenever(picsumApi.getImages(1))
            .thenReturn(null)
        val expectedResult =
            PagingSource.LoadResult.Error<Int, ImageListItem>(NullPointerException())
        assertEquals(
            expectedResult.toString(), pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 0,
                    loadSize = 1,
                    placeholdersEnabled = false
                )
            ).toString()
        )
    }

    @After
    fun release() {
        Mockito.framework().clearInlineMocks()
    }
}