package com.mapifesto.datasource_ors

import com.mapifesto.domain.Helpers
import com.mapifesto.domain.OrsSearchItem
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
        val similarity = Helpers.findSimilarity(searchString.lowercase(), name.lowercase())
        return (similarityFactor*similarity).toInt() + if(similarity == 1.0) exactMatchBonus else 0
    }
    fun wikiScore(item: OrsSearchItem): Int = if (item.wikiId == null) 0 else wikiBonus
    fun distanceScore(distance: Double?): Int = if (distance == null) 0 else (max(200 -30* ln(distance) + 30* ln(1000.0), 0.0)).toInt()
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
