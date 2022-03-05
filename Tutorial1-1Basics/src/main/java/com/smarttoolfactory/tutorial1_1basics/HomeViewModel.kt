package com.smarttoolfactory.tutorial1_1basics


import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.model.SuggestionModel
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

class HomeViewModel : ViewModel() {


    val tutorialList = mutableListOf<List<TutorialSectionModel>>()


    fun getTutorials(query: String): List<TutorialSectionModel> {

        val filteredList = linkedSetOf<TutorialSectionModel>()

        tutorialList.forEach { list: List<TutorialSectionModel> ->

            list.forEach { tutorialSectionModel ->

                if (tutorialSectionModel.description.contains(query, ignoreCase = true)) {
                    filteredList.add(tutorialSectionModel)
                }

                tutorialSectionModel.tags.forEach {
                    if (it.contains(query, ignoreCase = true)) {
                        filteredList.add(tutorialSectionModel)
                    }
                }
            }
        }

//        println("🤖 ViewModel Query: $query, filteredList: ${filteredList.size}")

        return  filteredList.toList()
    }
}


val suggestionList = listOf(
    SuggestionModel("Modifier"),
    SuggestionModel("Row"),
    SuggestionModel("Column"),
    SuggestionModel("BottomSheet"),
    SuggestionModel("Dialog"),
    SuggestionModel("Checkbox"),
    SuggestionModel("Layout"),
    SuggestionModel("Modifier"),
    SuggestionModel("SubcomposeLayout"),
    SuggestionModel("Recomposition"),
    SuggestionModel("SideEffect"),
    SuggestionModel("PointerInput"),
    SuggestionModel("AwaitPointerScope"),
    SuggestionModel("Gesture"),
    SuggestionModel("Drag"),
    SuggestionModel("Transform"),
    SuggestionModel("Canvas"),
    SuggestionModel("DrawScope"),
    SuggestionModel("Path"),
    SuggestionModel("PathEffect"),
    SuggestionModel("PathOperation"),
    SuggestionModel("Blend Mode"),
)