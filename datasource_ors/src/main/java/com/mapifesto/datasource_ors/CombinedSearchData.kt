package com.mapifesto.datasource_ors

import com.mapifesto.domain.LatLon
import com.mapifesto.domain.OrsSearchItems

enum class OrsSearchCase {
    POI, LOCATION
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
    val case: OrsSearchCase
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

    fun combinedSearchComplete(): Boolean {
        return when (case) {
            OrsSearchCase.POI -> searchCountryComplete && autocompleteCountryComplete && searchWorldComplete && autocompleteWorldComplete
            OrsSearchCase.LOCATION -> searchWorldComplete && autocompleteWorldComplete
        }
    }
}
