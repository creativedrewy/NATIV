package com.creativedrewy.nativ.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.creativedrewy.nativ.metaplex.MetaplexNftUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val metaplexNftUseCase: MetaplexNftUseCase
): ViewModel() {

    var viewState: MutableLiveData<List<String>> = MutableLiveData(listOf())

    fun loadNfts() {
        viewModelScope.launch {
            val nftData = metaplexNftUseCase.getMetaplexNftsForAccount("6aEBYFt9sX1R3rPsiYWiLK1QA5vj84Sj89wC2fNLYyMw")

            val results = nftData.map { it.description }
            viewState.postValue(results)
        }
    }

}