package com.rafsan.picsumphotoapp.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject


/**
 * Provide coroutines context.
 */
data class CoroutinesDispatcherProvider(
    val main: CoroutineDispatcher,
    val computation: CoroutineDispatcher,
    val io: CoroutineDispatcher
) {

    @Inject
    constructor() : this(Dispatchers.Main, Dispatchers.Default, Dispatchers.IO)
}