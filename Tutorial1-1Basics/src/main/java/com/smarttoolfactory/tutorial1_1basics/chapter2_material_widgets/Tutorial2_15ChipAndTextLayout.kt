@file:OptIn(
    BuildCompat.PrereleaseSdkCheck::class,
    ExperimentalMaterialApi::class
)

package com.smarttoolfactory.tutorial1_1basics.chapter2_material_widgets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.BuildCompat
import coil.compose.rememberAsyncImagePainter
import com.google.modernstorage.photopicker.PhotoPicker
import com.smarttoolfactory.tutorial1_1basics.ui.Green400
import kotlinx.coroutines.delay
import java.util.UUID

@Immutable
data class ChipData(
    val uri: Uri,
    val text: String,
    val id: String = UUID.randomUUID().toString()
)

@Preview(showBackground = true)
@Composable
private fun ChipSampleAndTextLayoutSample() {

    val backgroundColor = Green400.copy(alpha = .6f)

    val chipDataSnapshotStateList = remember {
        mutableStateListOf<ChipData>()
    }

    ChipAndTextFieldLayout(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        list = chipDataSnapshotStateList,
        backgroundColor = backgroundColor,
        onChipCreated = {
            chipDataSnapshotStateList.add(it)
        },

        chip = { data: ChipData, index: Int ->
            MyChip(backgroundColor, data) {
                chipDataSnapshotStateList.removeAt(index)
            }
        }
    )
}

@Composable
private fun MyChip(
    backgroundColor: Color,
    data: ChipData,
    onDeleteClick: () -> Unit
) {
    Chip(
        modifier = Modifier,
        shape = RoundedCornerShape(50),
        enabled = false,
        onClick = {},
        border = BorderStroke(1.dp, Green400.copy(alpha = .9f)),
        colors = ChipDefaults.chipColors(
            disabledBackgroundColor = backgroundColor,
            disabledContentColor = Color.White
        ),
        leadingIcon = {
            Image(
                painter = rememberAsyncImagePainter(data.uri),
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .size(34.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds,
                contentDescription = null
            )
        }
    ) {
        Text(
            text = data.text,
            modifier = Modifier.weight(1f, fill = false),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Icon(
            modifier = Modifier
                .clip(CircleShape)
                .clickable {
                    onDeleteClick()
                }
                .background(Color.Black.copy(alpha = .4f))
                .size(16.dp)
                .padding(2.dp),
            imageVector = Icons.Filled.Close,
            tint = Color(0xFFE0E0E0),
            contentDescription = null
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChipAndTextFieldLayout(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    list: List<ChipData> = emptyList(),
    onChipCreated: (ChipData) -> Unit,
    chip: @Composable (data: ChipData, index: Int) -> Unit
) {

    var text by remember {
        mutableStateOf("")
    }

    val focusRequester = remember {
        FocusRequester()
    }

    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    val photoPicker =
        rememberLauncherForActivityResult(PhotoPicker()) { uris ->
            uris.firstOrNull()?.let { uri: Uri ->
                onChipCreated(
                    ChipData(
                        uri = uri,
                        text = text
                    )
                )
                text = ""
                // Open keyboard after new chip is added
                keyboardController?.show()
            }
        }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    FlowRow(
        modifier = modifier
            .drawWithContent {
                drawContent()
                drawLine(
                    backgroundColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 4.dp.toPx()
                )
            },
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        list.forEachIndexed { index, item ->
            key(item.id) {
                chip(item, index)
            }
        }

        Box(
            modifier = Modifier
                .height(54.dp)
                // This minimum width that TextField can have
                // if remaining space in same row is smaller it's moved to next line
                .widthIn(min = 80.dp)
                // TextField can grow as big as Composable width
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                modifier = Modifier.focusRequester(focusRequester),
                value = text,
                textStyle = TextStyle(
                    fontSize = 20.sp
                ),
                cursorBrush = SolidColor(backgroundColor),
                singleLine = true,
                onValueChange = { text = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (text.isNotEmpty()) {
                            keyboardController?.hide()
                            photoPicker.launch(
                                PhotoPicker.Args(
                                    PhotoPicker.Type.IMAGES_ONLY, 1
                                )
                            )
                        }
                    }
                )
            )
        }
    }
}
