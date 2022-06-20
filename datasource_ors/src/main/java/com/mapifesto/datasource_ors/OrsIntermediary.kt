package com.mapifesto.datasource_ors

import com.mapifesto.domain.*
import com.mapifesto.domain.Helpers.findSimilarity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Math.log
import kotlin.math.ln
import kotlin.math.max

data class OrsSearchMembers(
    val apiKey: String,
    val searchString: String,
    val focus: LatLon? = null,
    val boundaryRectangle: BoundingBox? = null,
    val boundaryCircleRadius: Double? = null,
    val boundaryCircle: LatLon? = null,
    val boundaryGid: String? = null,
    val boundaryCountry: String? = null,
    val layers: OrsLayers,
    val sources: OrsSources,
    val size: Int,
    val language: String,
)

data class ScoredOrsSearchItem(
    val orsSearchItem: OrsSearchItem,
    var searchCountryScore: Int,
    var autocompleteCountryScore: Int,
    var searchWorldScore: Int,
    var autocompleteWorldScore: Int,
    var similarity: Int,
    var wiki: Int,
    var distance: String,
    var distanceScore: Int,
) {
    val totalScore get(): Int = searchCountryScore + autocompleteCountryScore + searchWorldScore + autocompleteWorldScore + similarity + wiki + distanceScore
}

data class ScoredOrsSearchItems(
    val combinedList: MutableList<ScoredOrsSearchItem> = mutableListOf(),
) {
    //fun containsId(id: String): Boolean = combinedList.any { it.orsSearchItem.id == id}
    private fun getItemById(id: String): ScoredOrsSearchItem? {
        return combinedList.find { it.orsSearchItem.id == id }
    }

    fun sortByScore() {
        combinedList.sortByDescending { it.totalScore }
    }

    fun addFromSearchCountry(searchString: String, userPosition: LatLon?, items: List<OrsSearchItem>) {
        items.forEachIndexed { index, newItem ->

            val score = 40 - index
            val existingItem = getItemById(newItem.id)
            val similarity = findSimilarity(searchString.lowercase(), newItem.name.lowercase())
            val distance = Helpers.distance(userPosition, newItem.latLon)


            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = score,
                        autocompleteCountryScore = 0,
                        searchWorldScore = 0,
                        autocompleteWorldScore = 0,
                        similarity = (50*similarity).toInt() + if(similarity == 1.0) 10 else 0,
                        wiki = if (newItem.wikiId == null) 0 else 50,
                        distance = Helpers.distancePretty(distance),
                        //distanceScore = if (distance == null) 0 else (5000000/distance).toInt()
                        distanceScore = if (distance == null) 0 else (max(200 -30* ln(distance) + 30*ln(1000.0), 0.0)).toInt()
                    )
                )
            } else {
                existingItem.searchCountryScore = score
            }

        }
    }

    fun addFromAutocompleteCountry(searchString: String, userPosition: LatLon?, items: List<OrsSearchItem>) {
        items.forEachIndexed { index, newItem ->

            val score = 40 - index
            val existingItem = getItemById(newItem.id)
            val similarity = findSimilarity(searchString.lowercase(), newItem.name.lowercase())
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = 0,
                        autocompleteCountryScore = score,
                        searchWorldScore = 0,
                        autocompleteWorldScore = 0,
                        similarity = (50*similarity).toInt() + if(similarity == 1.0) 10 else 0,
                        wiki = if (newItem.wikiId == null) 0 else 50,
                        distance = Helpers.distancePretty(distance),
                        //distanceScore = if (distance == null) 0 else (5000000/distance).toInt()
                        distanceScore = if (distance == null) 0 else (max(200 -30* ln(distance) + 30*ln(1000.0), 0.0)).toInt()
                    )
                )
            } else {
                existingItem.autocompleteCountryScore = score
            }

        }
    }

    fun addFromSearchWorld(searchString: String, userPosition: LatLon?, items: List<OrsSearchItem>) {
        items.forEachIndexed { index, newItem ->

            val score = 40 - index
            val existingItem = getItemById(newItem.id)
            val similarity = findSimilarity(searchString.lowercase(), newItem.name.lowercase())
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = 0,
                        autocompleteCountryScore = 0,
                        searchWorldScore = score,
                        autocompleteWorldScore = 0,
                        similarity = (50*similarity).toInt() + if(similarity == 1.0) 10 else 0,
                        wiki = if (newItem.wikiId == null) 0 else 50,
                        distance = Helpers.distancePretty(distance),
                        //distanceScore = if (distance == null) 0 else (5000000/distance).toInt()
                        distanceScore = if (distance == null) 0 else (max(200 -30* ln(distance) + 30*ln(1000.0), 0.0)).toInt()
                    )
                )
            } else {
                existingItem.searchWorldScore = score
            }

        }
    }

    fun addFromAutocompleteWorld(searchString: String, userPosition: LatLon?, items: List<OrsSearchItem>) {
        items.forEachIndexed { index, newItem ->

            val score = 40 - index
            val existingItem = getItemById(newItem.id)
            val similarity = findSimilarity(searchString.lowercase(), newItem.name.lowercase())
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = 0,
                        autocompleteCountryScore = 0,
                        searchWorldScore = 0,
                        autocompleteWorldScore = score,
                        similarity = (50*similarity).toInt() + if(similarity == 1.0) 10 else 0,
                        wiki = if (newItem.wikiId == null) 0 else 50,
                        distance = Helpers.distancePretty(distance),
                        //distanceScore = if (distance == null) 0 else (5000000/distance).toInt()
                        distanceScore = if (distance == null) 0 else (max(200 -30* ln(distance) + 30*ln(1000.0), 0.0)).toInt()
                    )
                )
            } else {
                existingItem.autocompleteWorldScore = score
            }

        }
    }

}

data class CombinedSearchData(
    val searchString: String,
    val userPosition: LatLon?,
    val scoredItems: ScoredOrsSearchItems = ScoredOrsSearchItems(),
    var errorMessage: String? = null,
    var searchCountryComplete: Boolean = false,
    var autocompleteCountryComplete: Boolean = false,
    var searchWorldComplete: Boolean = false,
    var autocompleteWorldComplete: Boolean = false,
) {
    fun setSearchCountry(result: OrsDataState<OrsSearchItems>) {

        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                searchCountryComplete = true
                scoredItems.addFromSearchCountry(searchString, userPosition, result.data.items)
            }
        }
        searchCountryComplete = true
    }

    fun setAutocompleteCountry(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                autocompleteCountryComplete = true
                scoredItems.addFromAutocompleteCountry(searchString, userPosition, result.data.items)
            }
        }
        searchCountryComplete = true
    }

    fun setSearchWorld(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                searchWorldComplete = true
                scoredItems.addFromSearchWorld(searchString, userPosition, result.data.items)
            }
        }
        searchCountryComplete = true
    }

    fun setAutocompleteWorld(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                autocompleteWorldComplete = true
                scoredItems.addFromAutocompleteWorld(searchString, userPosition, result.data.items)
            }
        }
        searchCountryComplete = true
    }

    fun combinedSearchComplete(): Boolean = searchCountryComplete && autocompleteCountryComplete && searchWorldComplete && autocompleteWorldComplete
}

interface OrsIntermediary {

    fun searchCountry(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun autocompleteCountry(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun searchWorld(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun autocompleteWorld(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun combinedSearch(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    )

    fun handleCombinedSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {

    }
}

class OrsIntermediaryImpl: OrsIntermediary {

    override fun searchCountry(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsSearchResultsCountry = OrsInteractors.build().getOrsSearchResultsCountry
        getOrsSearchResultsCountry.execute(
            orsSearchMembers = orsSearchMembers
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun autocompleteCountry(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsAutocompleteResultsCountry = OrsInteractors.build().getOrsAutocompleteResultsCountry
        getOrsAutocompleteResultsCountry.execute(
            orsSearchMembers = orsSearchMembers
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun searchWorld(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsSearchResultsWorld = OrsInteractors.build().getOrsSearchResultsWorld
        getOrsSearchResultsWorld.execute(
            orsSearchMembers = orsSearchMembers
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun autocompleteWorld(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsAutocompleteResultsWorld = OrsInteractors.build().getOrsAutocompleteResultsWorld
        getOrsAutocompleteResultsWorld.execute(
            orsSearchMembers = orsSearchMembers
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun combinedSearch(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {

        val combinedSearchData = CombinedSearchData(searchString = orsSearchMembers.searchString, userPosition = userPosition)

        searchCountry(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearch(combinedSearchData, callback)

        }

        autocompleteCountry(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearch(combinedSearchData, callback)
        }

        searchWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearch(combinedSearchData, callback)
        }

        autocompleteWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearch(combinedSearchData, callback)
        }
    }

    override fun handleCombinedSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {
        if(combinedSearchData.errorMessage != null)
            callback(OrsDataState.Error(combinedSearchData.errorMessage!!))
        else
            combinedSearchData.scoredItems.sortByScore()
            callback(OrsDataState.OrsData(combinedSearchData.scoredItems))
    }
}
