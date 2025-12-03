package com.smarttoolfactory.tutorial2_1unit_testing

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttoolfactory.tutorial2_1unit_testing.ui.theme.ComposeTutorialsTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val viewModel by viewModels<DispatcherTestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.get()

        setContent {
            ComposeTutorialsTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

class DispatcherTestViewModel() : ViewModel() {

    private val useCase = FlowUseCase()

    /*
        Default dispatcher for ViewModel is Dispatchers.Main.immediate which executes coroutines
        eagerly. Changing dispatcher to  Dispatchers.Main causes them to wait, changes
        printing order to start, end, launch1, launch2
     */

    init {
        println("Init start")
        viewModelScope.launch(
//            Dispatchers.Main
        ) {
            println("Launch1")
        }
        viewModelScope.launch(
//            Dispatchers.Main
        ) {
            println("Launch2")
        }
        println("Init end")
    }

    fun get() {
        viewModelScope.launch {
            useCase.getResultFlowWithDelay().collect {
                println("Collect in ${Thread.currentThread().name}")
            }
        }
    }
}

class FlowUseCase(val dispatcher: CoroutineDispatcher = Dispatchers.Default) {
    fun getResultFlowWithDelay() = flow {
        emit(getResultWithDelay())
    }
        .map { res: List<Int> ->
            println("Map in ${Thread.currentThread().name}")
            res.map {
                "Item $it"
            }
        }
        .flowOn(dispatcher)

    private fun getResult(): List<Int> {
        return listOf(1, 2, 3)
    }

    private suspend fun getResultWithDelay(): List<Int> {
        delay(100)
        return listOf(1, 2, 3)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeTutorialsTheme {
        Greeting("Android")
    }
}