package com.smarttoolfactory.tutorial1_1basics

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import com.smarttoolfactory.tutorial1_1basics.SearchDisplay.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/**
 * Creates a [SearchState] that is remembered across compositions.
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 * @param searchResults results of latest search
 *
 */
@Composable
fun <I, R, S> rememberSearchState(
    initialResults: List<I> = emptyList(),
    suggestions: List<S> = emptyList(),
    searchResults: List<R> = emptyList()
): SearchState<I, R, S> {
    return remember {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            searchResults = searchResults
        )
    }
}

/**
 * Creates a [SearchState] that is remembered across compositions. Uses [LaunchedEffect]
 *  and [snapshotFlow] to set states of search and return result or error state.
 *
 *  * First state when **Search Composable** is not focused is [SearchDisplay.InitialResults].
 *
 *  * When search gets focus state goes into [SearchDisplay.Suggestions] which some suggestion
 *  can be displayed to user.
 *
 *  * Immediately after user starts typing [SearchState.searchInProgress] sets to `true`
 *  to not get results while recomposition happens.
 *
 *  After [timeoutMillis] has passed [SearchState.searching] is set to `true`, progress icon
 *   can be displayed here.
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 * @param searchResults results of latest search
 * @param timeoutMillis timeout before user finishes typing. After this
 * timeout [SearchState.searching]
 * is set to true.
 *
 *
 * @param onQueryResult this lambda is for getting results from db, REST api or a ViewModel.
 *
 */
@Composable
fun <I, R, S> rememberSearchState(
    initialResults: List<I> = emptyList(),
    suggestions: List<S> = emptyList(),
    searchResults: List<R> = emptyList(),
    timeoutMillis: Long = 0,
    onQueryResult: (TextFieldValue) -> List<R>,
    ): SearchState<I, R, S> {

    return remember {
        SearchState(
            initialResults = initialResults,
            suggestions = suggestions,
            searchResults = searchResults
        )
    }.also { state ->
        LaunchedEffect(key1 = Unit) {

            snapshotFlow { state.query }
                .distinctUntilChanged()
                .filter { query: TextFieldValue ->
                    query.text.isNotEmpty() && !state.sameAsPreviousQuery()
                }
                .map { query: TextFieldValue ->
                    if (timeoutMillis>0) {
                        state.searching = false
                    }
                    state.searchInProgress = true
                    query
                }
                .debounce(timeoutMillis)
                .mapLatest { query: TextFieldValue ->
                    state.searching = true
                    // This delay is for showing circular progress bar, it's optional
                    delay(300)
                    onQueryResult(query)
                }
                .collect { result ->
                    state.searchResults = result
                    state.searchInProgress = false
                    state.searching = false
                }
        }
    }
}

/**
 *  A state object that can be hoisted to control and observe scrolling for [SearchBar]
 *  or [SearchTextField].
 *
 * Create instance using [rememberSearchState].
 *
 * @param initialResults results that can be displayed before doing a search
 * @param suggestions chip or cards that can be suggested to user when Search composable is focused
 * but query is empty
 * @param searchResults results of latest search
 */
class SearchState<I, R, S> internal constructor(
    initialResults: List<I>,
    suggestions: List<S>,
    searchResults: List<R>
) {
    /**
     * Query [TextFieldValue] that contains text and query selection position.
     */
    var query by mutableStateOf(TextFieldValue())

    /**
     * Flag  Search composable(TextField) focus state.
     */
    var focused by mutableStateOf(false)

    /**
     * Initial results to show initially before any search commenced. Show these items when
     * [searchDisplay] is in [SearchDisplay.InitialResults].
     */
    var initialResults by mutableStateOf(initialResults)

    /**
     * Suggestions might contain keywords and display chips to show when Search Composable
     * is focused but query is empty.
     */
    var suggestions by mutableStateOf(suggestions)

    /**
     * Results of a search action. If this list is empty [searchDisplay] is
     * [SearchDisplay.NoResults] state otherwise in [SearchDisplay.Results] state.
     */
    var searchResults by mutableStateOf(searchResults)

    /**
     * Last query text, it might be used to prevent doing search when current query and previous
     * query texts are same.
     */
    var previousQueryText = ""
        private set

    /**
     * Check if search initial conditions are met and a search operation is going on.
     * This flag is for showing progressbar.
     */
    var searching by mutableStateOf(false)

    /**
     * Check if a search is initiated. Search is initiated after a specific condition for example
     * a query with more than 2 chars is passed but user is still typing.
     * If  debounce or delay before user stops typing is not needed it can be
     * set to value of [searching].
     */
    var searchInProgress = searching



    val searchDisplay: SearchDisplay
        get() = when {
            !focused && query.text.isEmpty() -> SearchDisplay.InitialResults
            focused && query.text.isEmpty() -> SearchDisplay.Suggestions
            searchInProgress -> SearchDisplay.SearchInProgress
            !searchInProgress && searchResults.isEmpty() -> {
                previousQueryText = query.text
                SearchDisplay.NoResults
            }
            else -> {
                previousQueryText = query.text
                SearchDisplay.Results
            }
        }

    override fun toString(): String {
        return "🚀 STATE\n" +
                "query: ${query.text}, focused: $focused\n" +
                "searchInProgress: $searchInProgress searching: $searching\n" +
                " searchDisplay: $searchDisplay\n\n"
    }

    /**
     * Check if user is running same query as the previous one
     */
    fun sameAsPreviousQuery() = query.text == previousQueryText
}

/**
 * Enum class with different values to set search state based on text, focus, initial state and
 * results from search.
 *
 *
 * *  **[InitialResults]** represents the initial state before search or when search results are
 * empty and focus is not on a search Composable
 *
 * * **[Suggestions]** represents the state where search Composable gained focus but query is empty.
 *
 * * **[SearchInProgress]** represents initiation of search but not actively searching. For instance
 * search might require at least 3 letters or some specific condition. After condition is passed
 * [SearchState.searching] is true. This is useful for not having a search when first Composable
 * is composed.
 *
 * * **[Results]** represents the state after a successful search operation that returned non
 * empty results
 *
 * * **[NoResults]** represents the state which there are no results returned from a search operation
 *
 */
enum class SearchDisplay {
    InitialResults, Suggestions, SearchInProgress, Results, NoResults
}
