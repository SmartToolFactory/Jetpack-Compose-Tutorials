package com.smarttoolfactory.tutorial1_1basics


import androidx.lifecycle.ViewModel
import com.smarttoolfactory.tutorial1_1basics.model.TutorialSectionModel

class HomeViewModel : ViewModel() {

    var selectedPage:Int = 0

    lateinit var componentTutorialList: List<TutorialSectionModel>
    lateinit var layoutTutorials: List<TutorialSectionModel>

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

        println("🤖 ViewModel Query: $query, filteredList: ${filteredList.size}")

        return if (query.isEmpty()) tutorialList[selectedPage] else filteredList.toList()
    }
}