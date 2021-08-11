package com.creativedrewy.nativ.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.creativedrewy.nativ.viewstate.ViewStateCache
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val viewStateCache: ViewStateCache
) : ViewModel() {

    init {
        Log.v("SOL", "Your Item: ${ viewStateCache.refItem } this was set: ${ viewStateCache.setThis }")
    }

}