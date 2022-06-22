package com.mapifesto.datasource_ors

import com.mapifesto.domain.LatLon
import com.mapifesto.domain.OrsSearchItem

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
