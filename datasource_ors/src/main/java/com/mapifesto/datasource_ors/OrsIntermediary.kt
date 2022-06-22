package com.mapifesto.datasource_ors

import com.mapifesto.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

    fun combinedSearchReturningScoredItems(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    )

    fun handleCombinedSearchReturningScoredItems(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    )


    fun combinedSearch(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun handleCombinedSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )
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

    override fun combinedSearchReturningScoredItems(
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
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)

        }

        autocompleteCountry(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)
        }

        searchWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)
        }

        autocompleteWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)
        }
    }

    override fun handleCombinedSearchReturningScoredItems(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {
        if(combinedSearchData.errorMessage != null)
            callback(OrsDataState.Error(combinedSearchData.errorMessage!!))
        else
            combinedSearchData.scoredItems.sortByScore()
            callback(OrsDataState.OrsData(combinedSearchData.scoredItems))
    }

    override fun combinedSearch(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
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
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        if(combinedSearchData.errorMessage != null)
            callback(OrsDataState.Error(combinedSearchData.errorMessage!!))
        else
            combinedSearchData.scoredItems.sortByScore()

        callback(OrsDataState.OrsData(OrsSearchItems(items = combinedSearchData.scoredItems.combinedList.map { it.orsSearchItem } )))
    }
}

class OrsIntermediaryMockup: OrsIntermediary {
    override fun searchCountry(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun autocompleteCountry(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun searchWorld(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun autocompleteWorld(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun combinedSearchReturningScoredItems(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun handleCombinedSearchReturningScoredItems(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun combinedSearch(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun handleCombinedSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}
