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

    fun poiSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun handlePoiSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun locationSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun handleLocationSearch(
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

        val combinedSearchData = CombinedSearchData(
            searchString = orsSearchMembers.searchString,
            userPosition = userPosition,
            orsScoreParameters = orsScoreParameters,
            case = OrsSearchCase.POI,
        )

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

    override fun poiSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {

        val orsSearchMembers = OrsSearchMembers(
            apiKey = "5b3ce3597851110001cf6248782b9145cbb64ba2a7b5962e1023c1de",
            searchString = searchString,
            boundaryCountry = "SE",
            layers = OrsLayers(
                address = false,
                venue = true,
                neighbourhood = false,
                locality = false,
                borough = false,
                localadmin = false,
                county = false,
                macrocounty = false,
                region = false,
                macroregion = false,
                country = false,
                coarse = false,
            ),
            sources = OrsSources(
                openStreetMap = true,
                openAddresses = false,
                whosOnFirst = false,
                geonames = false
            ),
            sizeSearchCountry = 20,
            sizeAutoCompleteCountry = 20,
            sizeSearchWorld = 20,
            sizeAutoCompleteWorld = 20,
            language = "en-US",
            focus = focusAndUserPosition
        )
        val combinedSearchData = CombinedSearchData(
            searchString = searchString,
            userPosition = focusAndUserPosition,
            orsScoreParameters = OrsScoreParameters(),
            case = OrsSearchCase.POI,
        )

        searchCountry(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handlePoiSearch(combinedSearchData, callback)

        }

        autocompleteCountry(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handlePoiSearch(combinedSearchData, callback)
        }

        searchWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handlePoiSearch(combinedSearchData, callback)
        }

        autocompleteWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handlePoiSearch(combinedSearchData, callback)
        }
    }

    override fun handlePoiSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        if(combinedSearchData.errorMessage != null)
            callback(OrsDataState.Error(combinedSearchData.errorMessage!!))
        else {
            combinedSearchData.scoredItems.sortByScore()
            callback(OrsDataState.OrsData(OrsSearchItems(items = combinedSearchData.scoredItems.combinedList.map { it.orsSearchItem } )))
        }
    }

    override fun locationSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {

        val orsSearchMembers = OrsSearchMembers(
            apiKey = "5b3ce3597851110001cf6248782b9145cbb64ba2a7b5962e1023c1de",
            searchString = searchString,
            layers = OrsLayers(
                address = false,
                venue = false,
                neighbourhood = false,
                locality = false,
                borough = false,
                localadmin = false,
                county = false,
                macrocounty = false,
                region = false,
                macroregion = false,
                country = false,
                coarse = true,
            ),
            sources = OrsSources(
                openStreetMap = false,
                openAddresses = false,
                whosOnFirst = true,
                geonames = false
            ),
            sizeSearchCountry = 20,
            sizeAutoCompleteCountry = 20,
            sizeSearchWorld = 20,
            sizeAutoCompleteWorld = 20,
            language = "en-US",
            focus = focusAndUserPosition
        )
        val combinedSearchData = CombinedSearchData(
            searchString = searchString,
            userPosition = focusAndUserPosition,
            orsScoreParameters = OrsScoreParameters(),
            case = OrsSearchCase.LOCATION
        )

        searchWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handleLocationSearch(combinedSearchData, callback)
        }

        autocompleteWorld(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteWorld(it)

            if (combinedSearchData.combinedSearchComplete())
                handleLocationSearch(combinedSearchData, callback)
        }
    }

    override fun handleLocationSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        if(combinedSearchData.errorMessage != null)
            callback(OrsDataState.Error(combinedSearchData.errorMessage!!))
        else {
            combinedSearchData.scoredItems.sortByScore()
            callback(OrsDataState.OrsData(OrsSearchItems(items = combinedSearchData.scoredItems.combinedList.map { it.orsSearchItem } )))
        }



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

    override fun poiSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }


    override fun handlePoiSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun locationSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }


    override fun handleLocationSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

}
