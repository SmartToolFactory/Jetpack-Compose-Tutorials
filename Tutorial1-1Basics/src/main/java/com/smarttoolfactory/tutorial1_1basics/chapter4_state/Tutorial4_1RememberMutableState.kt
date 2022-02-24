package com.smarttoolfactory.tutorial1_1basics.chapter4_state

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.tutorial1_1basics.ui.Blue400
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import com.smarttoolfactory.tutorial1_1basics.ui.Orange400
import com.smarttoolfactory.tutorial1_1basics.ui.Pink400
import com.smarttoolfactory.tutorial1_1basics.ui.components.StyleableTutorialText

@Composable
fun Tutorial4_1Screen() {
    TutorialContent()
}

@Composable
private fun TutorialContent() {
    Column(modifier = Modifier.fillMaxSize()) {

        StyleableTutorialText(
            text = "Remember stores the initial calculation from composition. This value survives " +
                    "recomposition which acts as cache for functions.",
            bullets = false
        )

        StyleableTutorialText(
            text = "At outer level there is no **recomposition** since " +
                    "mutableState(counter) is not observed by outer level. " +
                    "Because of that **only in this counter myVal gets updated**.",
            bullets = false
        )
        Counter1()
        Spacer(modifier = Modifier.height(8.dp))
        StyleableTutorialText(
            text = "In counter 2 and 3 **myVal is always 0** because " +
                    "its initial value is remembered in outer composable",
            bullets = false
        )
        Counter2()
        Spacer(modifier = Modifier.height(8.dp))
        Counter3()
        Spacer(modifier = Modifier.height(8.dp))
        StyleableTutorialText(
            text = "Since **MyData** is remembered at each recomposition initial one in " +
                    "**remember** is retained. Because of this it's initial value is " +
                    "displayed in inner composition",
            bullets = false
        )
        Counter4()
    }
}

@Composable
private fun Counter1() {

    Column(
        modifier = Modifier
            .background(Orange400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        var counter by remember { mutableStateOf(0) }
        val myData = remember { MyData() }
        var myVal = 0

        println("🔥 Counter1() composition: myVal: $myVal")
        Text("myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                // 🔥 MyVal gets updated only here because outer composable is not recomposed
                // because Text above button does not listen for changes in mutableStateOf counter
                myVal++
                // Since no composition above my data object does not change even without remember
                myData.value = myData.value + 1
            }) {
            Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

@Composable
private fun Counter2() {

    Column(
        modifier = Modifier
            .background(Blue400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        var counter by remember { mutableStateOf(0) }
        val myData = remember { MyData() }
        var myVal = remember { 0 }

        println("🎃 Counter2() composition: myVal: $myVal")
        Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                // 🎃 MyVal do
                myVal++
                myData.value = myData.value + 1
            }) {
            Text("myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

@Composable
private fun Counter3() {

    Column(
        modifier = Modifier
            .background(Pink400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        var counter by remember { mutableStateOf(0) }
        val myData = remember { MyData() }
        var myVal = 0

        println("🍏 Counter3() composition: myVal: $myVal")
        Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                myVal++
                myData.value = myData.value + 1
            }) {
            Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")
        }
    }
}

@Composable
private fun Counter4() {

    Column(
        modifier = Modifier
            .background(Green400)
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        var counter by remember { mutableStateOf(0) }
        var myData = remember { MyData() }
        var myVal = remember { 0 }

        println("🍒 Counter4() composition: myVal: $myVal, myData: $myData")
        Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            onClick = {
                counter++
                myVal++
                // 🔥 Since MyData is remembered at each composition the one initially instantiated
                // inside remember is retained
                myData = MyData(myData.value + 1)
            }) {
            Text("Counter: $counter, myVal: $myVal, myData value: ${myData.value}")
        }
    }
}


class MyData(var value: Int = 0)

