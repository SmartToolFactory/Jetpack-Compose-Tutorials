@file:OptIn(ExperimentalMaterialApi::class)

package com.smarttoolfactory.tutorial3_1navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExposedSelectionMenu(
    title: String,
    index: Int,
    options: List<String>,
    onSelected: (Int) -> Unit,
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedOptionText by rememberSaveable { mutableStateOf(options[index]) }

    ExposedDropdownMenuBox(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        }
    ) {

        TextField(
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(title) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = {
                expanded = false

            }
        ) {
            options.forEachIndexed { index: Int, selectionOption: String ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth(),
                    content = {
                        Text(text = selectionOption)
                    },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        onSelected(index)
                    }
                )
            }
        }
    }
}