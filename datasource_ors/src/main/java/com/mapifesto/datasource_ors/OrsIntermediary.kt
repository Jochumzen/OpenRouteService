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
) {
    fun removeBoundaryCountry(): OrsSearchMembers {
        return this.copy(boundaryCountry = null)
    }
}

enum class FullSearchCase {
    POI, LOCATION
}

interface OrsIntermediary {

    //This method is a straight implementation of the interactor GetOrsSearchResult which is a straight implementation of OpenRouteService /geocode/search
    fun search(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    //This method is a straight implementation of the interactor GetOrsAutoCompleteResult which is a straight implementation of OpenRouteService /geocode/autocomplete
    fun autocomplete(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    //This method is used only in this project to evaluate the scoring system for combining search-results
    fun combinedSearchReturningScoredItems(
        orsSearchMembers: OrsSearchMembers,
        userPosition: LatLon?,
        orsScoreParameters: OrsScoreParameters,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    )

    //This method is used only in this project to evaluate the scoring system for combining search-results
    fun handleCombinedSearchReturningScoredItems(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<ScoredOrsSearchItems>) -> Unit
    )

    //This is the main method used by Search in MapiGanja
    fun fullSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        fullSearchCase: FullSearchCase,
        boundingBox: BoundingBox?,
        searchRadius: Float?,
        areaRestrictions: List<String>,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    //This is the main method used by Search in MapiGanja
    fun handleFullSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    //This method is used in MapiGanja to find an area/location. Used in SearchSettings to restrict area.
    fun locationSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    //This method is used in MapiGanja to find an area/location. Used in SearchSettings to restrict area.
    fun handleLocationSearch(
        combinedSearchData: CombinedSearchData,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )
}

class OrsIntermediaryImpl: OrsIntermediary {

    override fun search(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsSearchResults = OrsInteractors.build().getOrsSearchResults
        getOrsSearchResults.execute(
            orsSearchMembers = orsSearchMembers
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }


    override fun autocomplete(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsAutocompleteResults = OrsInteractors.build().getOrsAutocompleteResults
        getOrsAutocompleteResults.execute(
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
            includeCountrySearch = true,
        )

        //Search: Country
        search(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearchCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)

        }

        //AutoComplete: Country
        autocomplete(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocompleteCountry(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)
        }

        //Search: World
        search(
            orsSearchMembers = orsSearchMembers.removeBoundaryCountry()
        ) {
            combinedSearchData.setSearch(it)

            if (combinedSearchData.combinedSearchComplete())
                handleCombinedSearchReturningScoredItems(combinedSearchData, callback)
        }

        //Autocomplete: World
        autocomplete(
            orsSearchMembers = orsSearchMembers.removeBoundaryCountry()
        ) {
            combinedSearchData.setAutocomplete(it)

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

    override fun fullSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        fullSearchCase: FullSearchCase,
        boundingBox: BoundingBox?,
        searchRadius: Float?,
        areaRestrictions: List<String>,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {

        val layers = when(fullSearchCase) {
            FullSearchCase.POI -> OrsLayers.createOnlyVenue()
            FullSearchCase.LOCATION -> OrsLayers.createOnlyCourse()
        }

        val sources = when(fullSearchCase) {
            FullSearchCase.POI -> OrsSources.createOnlyOSM()
            FullSearchCase.LOCATION -> OrsSources.createOnlyWhosOnFirst()
        }

        val includeCountrySearch = false //fullSearchCase == FullSearchCase.POI && boundingBox == null && searchRadius == null && areaRestrictions.isEmpty()

        val orsSearchMembers = OrsSearchMembers(
            apiKey = "5b3ce3597851110001cf6248782b9145cbb64ba2a7b5962e1023c1de",
            searchString = searchString,
            layers = layers,
            sources = sources,
            sizeSearchCountry = 20,
            sizeAutoCompleteCountry = 20,
            sizeSearchWorld = 20,
            sizeAutoCompleteWorld = 20,
            boundaryRectangle = boundingBox,
            boundaryCircle = if(searchRadius == null) null else focusAndUserPosition,
            boundaryCircleRadius = searchRadius?.toDouble(),
            boundaryGid = if(areaRestrictions.isEmpty()) null else areaRestrictions.first(),
            language = "en-US",
            focus = focusAndUserPosition
        )

        val combinedSearchData = CombinedSearchData(
            searchString = searchString,
            userPosition = focusAndUserPosition,
            orsScoreParameters = OrsScoreParameters(),
            includeCountrySearch = includeCountrySearch,
        )

        search(
            orsSearchMembers = orsSearchMembers.removeBoundaryCountry()
        ) {
            combinedSearchData.setSearch(it)

            if (combinedSearchData.combinedSearchComplete())
                handleFullSearch(combinedSearchData, callback)

        }

        autocomplete(
            orsSearchMembers = orsSearchMembers.removeBoundaryCountry()
        ) {
            combinedSearchData.setAutocomplete(it)

            if (combinedSearchData.combinedSearchComplete())
                handleFullSearch(combinedSearchData, callback)
        }

        if(includeCountrySearch) {
            search(
                orsSearchMembers = orsSearchMembers
            ) {
                combinedSearchData.setSearchCountry(it)

                if (combinedSearchData.combinedSearchComplete())
                    handleFullSearch(combinedSearchData, callback)
            }

            autocomplete(
                orsSearchMembers = orsSearchMembers
            ) {
                combinedSearchData.setAutocompleteCountry(it)

                if (combinedSearchData.combinedSearchComplete())
                    handleFullSearch(combinedSearchData, callback)
            }
        }


    }

    override fun handleFullSearch(
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
            layers = OrsLayers.createOnlyCourse(),
            sources = OrsSources.createOnlyWhosOnFirst(),
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
            includeCountrySearch = false,
        )

        search(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setSearch(it)

            if (combinedSearchData.combinedSearchComplete())
                handleLocationSearch(combinedSearchData, callback)
        }

        autocomplete(
            orsSearchMembers = orsSearchMembers
        ) {
            combinedSearchData.setAutocomplete(it)

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
    override fun search(
        orsSearchMembers: OrsSearchMembers,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun autocomplete(
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

    override fun fullSearch(
        searchString: String,
        focusAndUserPosition: LatLon?,
        fullSearchCase: FullSearchCase,
        boundingBox: BoundingBox?,
        searchRadius: Float?,
        areaRestrictions: List<String>,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        TODO("Not yet implemented")
    }


    override fun handleFullSearch(
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
