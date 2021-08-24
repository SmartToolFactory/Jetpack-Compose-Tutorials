package com.smarttoolfactory.tutorial1_1basics.model

import androidx.compose.ui.graphics.vector.ImageVector

fun separateIntoActionAndOverflow(
    items: List<ActionItemSpec>,
    defaultIconSpace: Int
): Pair<List<ActionItemSpec>, List<ActionItemSpec>> {

    var (alwaysCount, neverCount, ifRoomCount) = Triple(0, 0, 0)

    for (item in items) {
        when (item.visibility) {
            ActionItemMode.ALWAYS_SHOW -> alwaysCount++
            ActionItemMode.NEVER_SHOW -> neverCount++
            ActionItemMode.IF_ROOM -> ifRoomCount++
        }
    }

    val needsOverflow = alwaysCount + ifRoomCount > defaultIconSpace || neverCount > 0
    val actionIconSpace = defaultIconSpace - (if (needsOverflow) 1 else 0)

    val actionItems = ArrayList<ActionItemSpec>()
    val overflowItems = ArrayList<ActionItemSpec>()

    var ifRoomsToDisplay = actionIconSpace - alwaysCount
    for (item in items) {
        when (item.visibility) {
            ActionItemMode.ALWAYS_SHOW -> {
                actionItems.add(item)
            }
            ActionItemMode.NEVER_SHOW -> {
                overflowItems.add(item)
            }
            ActionItemMode.IF_ROOM -> {
                if (ifRoomsToDisplay > 0) {
                    actionItems.add(item)
                    ifRoomsToDisplay--
                } else {
                    overflowItems.add(item)
                }

            }
        }
    }
    return Pair(actionItems, overflowItems)

}

// Kind of equivalent to a menu XML entry, except for the onClick lambda
data class ActionItemSpec(
    val name: String,
    val icon: ImageVector,
    val visibility: ActionItemMode = ActionItemMode.IF_ROOM,
    val onClick: () -> Unit,
)

// Whether to show the action item as an icon or not (or if room)
enum class ActionItemMode {
    ALWAYS_SHOW, IF_ROOM, NEVER_SHOW
}