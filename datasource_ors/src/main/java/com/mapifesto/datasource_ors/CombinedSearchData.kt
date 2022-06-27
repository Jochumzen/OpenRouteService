package com.mapifesto.datasource_ors

import com.mapifesto.domain.LatLon
import com.mapifesto.domain.OrsSearchItems

data class CombinedSearchData(
    val searchString: String,
    val userPosition: LatLon?,
    val scoredItems: ScoredOrsSearchItems = ScoredOrsSearchItems(),
    var errorMessage: String? = null,
    var searchCountryComplete: Boolean = false,
    var autocompleteCountryComplete: Boolean = false,
    var searchComplete: Boolean = false,
    var autocompleteComplete: Boolean = false,
    val orsScoreParameters: OrsScoreParameters,
    val includeCountrySearch: Boolean
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

    fun setSearch(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                scoredItems.addFromSearch(searchString, userPosition, result.data.items, orsScoreParameters)
            }
        }
        searchComplete = true
    }

    fun setAutocomplete(result: OrsDataState<OrsSearchItems>) {
        when(result) {
            is OrsDataState.Error -> {
                errorMessage += result.error
            }
            is OrsDataState.OrsData -> {
                scoredItems.addFromAutocomplete(searchString, userPosition, result.data.items, orsScoreParameters)
            }
        }
        autocompleteComplete = true
    }

    fun combinedSearchComplete(): Boolean {
        return if (includeCountrySearch)
            searchCountryComplete && autocompleteCountryComplete && searchComplete && autocompleteComplete
        else
            searchComplete && autocompleteComplete
    }
}
