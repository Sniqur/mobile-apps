package com.lab4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.lab4.ui.screens.subjectDetails.SubjectDetailsScreen
import com.lab4.ui.screens.subjectsList.SubjectsListScreen
import kotlinx.serialization.Serializable


@Serializable
data object SubjectsListRoute : NavKey

@Serializable
data class SubjectDetailsRoute(val id: Int) : NavKey

@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
) {
    // Back stack that holds screens; start destination is SubjectsListRoute
    val backStack = rememberNavBackStack(SubjectsListRoute)
    // NavDisplay renders the top of the back stack
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        // Map: route key -> Composable content to show
        entryProvider = entryProvider {
            entry<SubjectsListRoute> {
                SubjectsListScreen(
                    onDetailsScreen = { id ->
                        backStack.add(SubjectDetailsRoute(id))
                    }
                )
            }
            // Subject details screen; receives strongly-typed route args
            entry<SubjectDetailsRoute> { route -> SubjectDetailsScreen(route) }
        }
    )
}