package com.smarttoolfactory.tutorial1_1basics.chapter9_animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Preview
@Composable
fun CoroutinesTest() {

    val mutex = remember {
        Mutex()
    }

    val mutatorMutex = remember {
        MutatorMutex()
    }


    val scope = rememberCoroutineScope()

    val animatable = remember {
        Animatable(0f)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {

        Text(
            "Value: ${animatable.value.toInt()}", fontSize = 26.sp
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    mutex.withLock {
                        animatable.snapTo(0f)
                        try {
                            animatable.animateTo(
                                targetValue = 100f,
                                animationSpec = tween(5000, easing = LinearEasing)
                            )
                        } catch (e: CancellationException) {
                            println("Exception: ${e.message}")
                        }
                    }
                }
            }
        ) {
            Text("Start with Mutex")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    animatable.snapTo(0f)
                    try {
                        animatable.animateTo(
                            targetValue = 100f,
                            animationSpec = tween(5000, easing = LinearEasing)
                        )
                    } catch (e: CancellationException) {
                        println("Exception: $e")
                    }
                }
            }
        ) {
            Text("Start")
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {

                scope.launch {
                  try {
                      mutatorMutex.mutate {
                          animatable.snapTo(0f)
                          try {
                              animatable.animateTo(
                                  targetValue = 100f,
                                  animationSpec = tween(5000, easing = LinearEasing)
                              )
                          } catch (e: CancellationException) {
                              println("Exception: $e")
                          }
                      }
                  }catch (e: Exception){
                      println("MutatorMutexException: $e")
                  }
                }
            }
        ) {
            Text("Start with Mutex")
        }

    }
}