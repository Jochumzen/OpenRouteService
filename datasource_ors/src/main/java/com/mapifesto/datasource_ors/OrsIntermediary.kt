package com.mapifesto.datasource_ors

import com.mapifesto.domain.*
import com.mapifesto.domain.Helpers.findSimilarity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Math.log
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max

data class OrsScoreParameters(
    private val searchCountryScoreIntercept: Int = 40,
    private val searchCountryScoreSlope: Int = 1,
    private val autocompleteCountryScoreIntercept: Int = 40,
    private val autocompleteCountryScoreSlope: Int = 1,
    private val searchWorldScoreIntercept: Int = 40,
    private val searchWorldScoreSlope: Int = 1,
    private val autocompleteWorldScoreIntercept: Int = 40,
    private val autocompleteWorldScoreSlope: Int = 1,
    private val similarityFactor: Int = 50,
    private val exactMatchBonus: Int = 10,
    private val wikiBonus: Int = 50,
    private val distanceIntercept: Int = 200,
    private val distanceSlope: Int = 30
) {
    fun searchCountryScore(orsRank: Int): Int =  searchCountryScoreIntercept - searchCountryScoreSlope*orsRank
    fun autocompleteCountryScore(orsRank: Int): Int =  autocompleteCountryScoreIntercept - autocompleteCountryScoreSlope*orsRank
    fun searchWorldScore(orsRank: Int): Int =  searchWorldScoreIntercept - searchWorldScoreSlope*orsRank
    fun autocompleteWorldScore(orsRank: Int): Int =  autocompleteWorldScoreIntercept - autocompleteWorldScoreSlope*orsRank
    fun similarityScore(searchString: String, name: String): Int {
        val similarity = findSimilarity(searchString.lowercase(), name.lowercase())
        return (similarityFactor*similarity).toInt() + if(similarity == 1.0) exactMatchBonus else 0
    }
    fun wikiScore(item: OrsSearchItem): Int = if (item.wikiId == null) 0 else wikiBonus
    fun distanceScore(distance: Double?): Int = if (distance == null) 0 else (max(200 -30* ln(distance) + 30*ln(1000.0), 0.0)).toInt()
    fun distanceZeroScore(): Int = exp(distanceIntercept.toDouble()/distanceSlope).toInt()
    companion object {
        fun factory(
            searchCountryScoreIntercept: Int,
            searchCountryScoreSlope: Int,
            autocompleteCountryScoreIntercept: Int,
            autocompleteCountryScoreSlope: Int = 1,
            searchWorldScoreIntercept: Int,
            searchWorldScoreSlope: Int,
            autocompleteWorldScoreIntercept: Int,
            autocompleteWorldScoreSlope: Int,
            similarityFactor: Int,
            exactMatchBonus: Int,
            wikiBonus: Int,
            distanceIntercept: Int,
            distanceSlope: Int,
        ): OrsScoreParameters {
            return OrsScoreParameters(
                searchCountryScoreIntercept = searchCountryScoreIntercept,
                searchCountryScoreSlope = searchCountryScoreSlope,
                autocompleteCountryScoreIntercept = autocompleteCountryScoreIntercept,
                autocompleteCountryScoreSlope = autocompleteCountryScoreSlope,
                searchWorldScoreIntercept = searchWorldScoreIntercept,
                searchWorldScoreSlope = searchWorldScoreSlope,
                autocompleteWorldScoreIntercept = autocompleteWorldScoreIntercept,
                autocompleteWorldScoreSlope = autocompleteWorldScoreSlope,
                similarityFactor = similarityFactor,
                exactMatchBonus = exactMatchBonus,
                wikiBonus = wikiBonus,
                distanceIntercept = distanceIntercept,
                distanceSlope = distanceSlope,
            )
        }
    }
}

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
    val sizeSearchCountry: Int,
    val sizeAutoCompleteCountry: Int,
    val sizeSearchWorld: Int,
    val sizeAutoCompleteWorld: Int,
    val language: String,
)

data class ScoredOrsSearchItem(
    val orsSearchItem: OrsSearchItem,
    var searchCountryScore: Int,
    var autocompleteCountryScore: Int,
    var searchWorldScore: Int,
    var autocompleteWorldScore: Int,
    val similarity: Int,
    val wiki: Int,
    val distance: String,
    val distanceScore: Int,
) {
    val totalScore get(): Int = searchCountryScore + autocompleteCountryScore + searchWorldScore + autocompleteWorldScore + similarity + wiki + distanceScore
}

data class ScoredOrsSearchItems(
    val combinedList: MutableList<ScoredOrsSearchItem> = mutableListOf(),
) {

    private fun getItemById(id: String): ScoredOrsSearchItem? {
        return combinedList.find { it.orsSearchItem.id == id }
    }

    fun sortByScore() {
        combinedList.sortByDescending { it.totalScore }
    }

    fun addFromSearchCountry(
        searchString: String,
        userPosition: LatLon?,
        items: List<OrsSearchItem>,
        orsScoreParameters: OrsScoreParameters,
    ) {
        items.forEachIndexed { index, newItem ->

            val orsRankingScore = orsScoreParameters.searchCountryScore(index)
            val existingItem = getItemById(newItem.id)
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = orsRankingScore,
                        autocompleteCountryScore = 0,
                        searchWorldScore = 0,
                        autocompleteWorldScore = 0,
                        similarity = orsScoreParameters.similarityScore(searchString, newItem.name),
                        wiki = orsScoreParameters.wikiScore(newItem),
                        distance = Helpers.distancePretty(distance),
                        distanceScore = orsScoreParameters.distanceScore(distance)
                    )
                )
            } else {
                existingItem.searchCountryScore = orsRankingScore
            }

        }
    }

    fun addFromAutocompleteCountry(
        searchString: String,
        userPosition: LatLon?,
        items: List<OrsSearchItem>,
        orsScoreParameters: OrsScoreParameters,
    ) {
        items.forEachIndexed { index, newItem ->

            val orsRankingScore = orsScoreParameters.autocompleteCountryScore(index)
            val existingItem = getItemById(newItem.id)
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = 0,
                        autocompleteCountryScore = orsRankingScore,
                        searchWorldScore = 0,
                        autocompleteWorldScore = 0,
                        similarity = orsScoreParameters.similarityScore(searchString, newItem.name),
                        wiki = orsScoreParameters.wikiScore(newItem),
                        distance = Helpers.distancePretty(distance),
                        distanceScore = orsScoreParameters.distanceScore(distance)
                    )
                )
            } else {
                existingItem.autocompleteCountryScore = orsRankingScore
            }

        }
    }

    fun addFromSearchWorld(
        searchString: String,
        userPosition: LatLon?,
        items: List<OrsSearchItem>,
        orsScoreParameters: OrsScoreParameters,
    ) {
        items.forEachIndexed { index, newItem ->

            val orsRankingScore = orsScoreParameters.searchWorldScore(index)
            val existingItem = getItemById(newItem.id)
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = 0,
                        autocompleteCountryScore = 0,
                        searchWorldScore = orsRankingScore,
                        autocompleteWorldScore = 0,
                        similarity = orsScoreParameters.similarityScore(searchString, newItem.name),
                        wiki = orsScoreParameters.wikiScore(newItem),
                        distance = Helpers.distancePretty(distance),
                        distanceScore = orsScoreParameters.distanceScore(distance)
                    )
                )
            } else {
                existingItem.searchWorldScore = orsRankingScore
            }

        }
    }

    fun addFromAutocompleteWorld(
        searchString: String,
        userPosition: LatLon?,
        items: List<OrsSearchItem>,
        orsScoreParameters: OrsScoreParameters,
    ) {
        items.forEachIndexed { index, newItem ->

            val orsRankingScore = orsScoreParameters.autocompleteWorldScore(index)
            val existingItem = getItemById(newItem.id)
            val distance = Helpers.distance(userPosition, newItem.latLon)

            if (existingItem == null) {
                combinedList.add(
                    ScoredOrsSearchItem(
                        orsSearchItem = newItem,
                        searchCountryScore = 0,
                        autocompleteCountryScore = 0,
                        searchWorldScore = 0,
                        autocompleteWorldScore = orsRankingScore,
                        similarity = orsScoreParameters.similarityScore(searchString, newItem.name),
                        wiki = orsScoreParameters.wikiScore(newItem),
                        distance = Helpers.distancePretty(distance),
                        distanceScore = orsScoreParameters.distanceScore(distance)
                    )
                )
            } else {
                existingItem.autocompleteWorldScore = orsRankingScore
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
    val orsScoreParameters: OrsScoreParameters,
) {
    fun setSearchCountry(result: OrsDataState<OrsSearchItems>) {

        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                scoredItems.addFromSearchCountry(searchString, userPosition, result.data.items, orsScoreParameters)
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
                scoredItems.addFromAutocompleteCountry(searchString, userPosition, result.data.items, orsScoreParameters)
            }
        }
        autocompleteCountryComplete = true
    }

    fun setSearchWorld(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                scoredItems.addFromSearchWorld(searchString, userPosition, result.data.items, orsScoreParameters)
            }
        }
        searchWorldComplete = true
    }

    fun setAutocompleteWorld(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                scoredItems.addFromAutocompleteWorld(searchString, userPosition, result.data.items, orsScoreParameters)
            }
        }
        autocompleteWorldComplete = true
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
        orsScoreParameters: OrsScoreParameters,
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
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {

        val combinedSearchData = CombinedSearchData(searchString = orsSearchMembers.searchString, userPosition = userPosition, orsScoreParameters = orsScoreParameters)

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
