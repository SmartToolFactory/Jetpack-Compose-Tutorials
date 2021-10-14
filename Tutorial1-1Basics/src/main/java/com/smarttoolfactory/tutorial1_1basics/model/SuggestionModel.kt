package com.smarttoolfactory.tutorial1_1basics.model

data class SuggestionModel(val tag: String) {
    val id = tag.hashCode()
}
