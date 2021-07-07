
package com.creativedrewy.nativ.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.creativedrewy.nativ.ui.theme.NATIVTheme
import com.creativedrewy.nativ.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

@AndroidEntryPoint
class MainActivity : ComponentActivity(), CoroutineScope by MainScope() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NATIVTheme {
                Surface(
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Andrew")
                }
            }
        }

        viewModel.loadNfts()
    }
}

@Composable
fun Greeting(name: String) {
    LazyColumn() {
        items(
            count = 10
        ) {
            Text(text = "Hello $name!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NATIVTheme {
        Greeting("Android")
    }
}