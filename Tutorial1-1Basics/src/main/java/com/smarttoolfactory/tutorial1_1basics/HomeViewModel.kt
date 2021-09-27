package com.smarttoolfactory.tutorial1_1basics


import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

class HomeViewModel : ViewModel() {

    lateinit var componentTutorialList: List<TutorialSectionModel>

    fun getTutorials(query: String): List<TutorialSectionModel> {

        val filteredList = linkedSetOf<TutorialSectionModel>()

        componentTutorialList.forEach { tutorialSectionModel ->

            if (tutorialSectionModel.description.contains(query)) {
                filteredList.add(tutorialSectionModel)
            }

            tutorialSectionModel.tags.forEach {
                if (it.contains(query)) {
                    filteredList.add(tutorialSectionModel)
                }
            }
        }

        println("🤖 ViewModel Query: $query, filteredList: ${filteredList.size}")

        return if (query.isEmpty()) componentTutorialList else filteredList.toList()
    }
}