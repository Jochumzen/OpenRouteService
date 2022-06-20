package com.mapifesto.datasource_ors

import com.mapifesto.domain.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

interface OrsIntermediary {

    fun searchCountry(
        apiKey: String,
        searchString: String,
        focus: LatLon? = null,
        boundaryRectangle: BoundingBox? = null,
        boundaryCircleRadius: Double? = null,
        boundaryCircle: LatLon? = null,
        boundaryGid: String? = null,
        boundaryCountry: String,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun autocompleteCountry(
        apiKey: String,
        searchString: String,
        focus: LatLon? = null,
        boundaryRectangle: BoundingBox? = null,
        boundaryCircleRadius: Double? = null,
        boundaryCircle: LatLon? = null,
        boundaryGid: String? = null,
        boundaryCountry: String,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun searchWorld(
        apiKey: String,
        searchString: String,
        focus: LatLon? = null,
        boundaryRectangle: BoundingBox? = null,
        boundaryCircleRadius: Double? = null,
        boundaryCircle: LatLon? = null,
        boundaryGid: String? = null,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun autocompleteWorld(
        apiKey: String,
        searchString: String,
        focus: LatLon? = null,
        boundaryRectangle: BoundingBox? = null,
        boundaryCircleRadius: Double? = null,
        boundaryCircle: LatLon? = null,
        boundaryGid: String? = null,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )

    fun combinedSearch(
        apiKey: String,
        searchString: String,
        focus: LatLon? = null,
        boundaryRectangle: BoundingBox? = null,
        boundaryCircleRadius: Double? = null,
        boundaryCircle: LatLon? = null,
        boundaryGid: String? = null,
        boundaryCountry: String,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    )
}

class OrsIntermediaryImpl: OrsIntermediary {

    override fun searchCountry(
        apiKey: String,
        searchString: String,
        focus: LatLon?,
        boundaryRectangle: BoundingBox?,
        boundaryCircleRadius: Double?,
        boundaryCircle: LatLon?,
        boundaryGid: String?,
        boundaryCountry: String,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsSearchResultsCountry = OrsInteractors.build().getOrsSearchResultsCountry
        getOrsSearchResultsCountry.execute(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            boundaryCountry = boundaryCountry,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun autocompleteCountry(
        apiKey: String,
        searchString: String,
        focus: LatLon?,
        boundaryRectangle: BoundingBox?,
        boundaryCircleRadius: Double?,
        boundaryCircle: LatLon?,
        boundaryGid: String?,
        boundaryCountry: String,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsAutocompleteResultsCountry = OrsInteractors.build().getOrsAutocompleteResultsCountry
        getOrsAutocompleteResultsCountry.execute(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            boundaryCountry = boundaryCountry,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun searchWorld(
        apiKey: String,
        searchString: String,
        focus: LatLon?,
        boundaryRectangle: BoundingBox?,
        boundaryCircleRadius: Double?,
        boundaryCircle: LatLon?,
        boundaryGid: String?,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsSearchResultsWorld = OrsInteractors.build().getOrsSearchResultsWorld
        getOrsSearchResultsWorld.execute(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun autocompleteWorld(
        apiKey: String,
        searchString: String,
        focus: LatLon?,
        boundaryRectangle: BoundingBox?,
        boundaryCircleRadius: Double?,
        boundaryCircle: LatLon?,
        boundaryGid: String?,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {
        val getOrsAutocompleteResultsWorld = OrsInteractors.build().getOrsAutocompleteResultsWorld
        getOrsAutocompleteResultsWorld.execute(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ).onEach { dataState ->

            callback(dataState)

        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    override fun combinedSearch(
        apiKey: String,
        searchString: String,
        focus: LatLon?,
        boundaryRectangle: BoundingBox?,
        boundaryCircleRadius: Double?,
        boundaryCircle: LatLon?,
        boundaryGid: String?,
        boundaryCountry: String,
        layers: OrsLayers,
        sources: OrsSources,
        size: Int,
        language: String,
        callback: (OrsDataState<OrsSearchItems>) -> Unit
    ) {

        val combinedList: MutableList<OrsSearchItem> = mutableListOf()

        searchCountry(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            boundaryCountry = boundaryCountry,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ) {
            when(it) {
                is OrsDataState.Error -> {

                }
                is OrsDataState.OrsData -> {
                    combinedList.addAll(it.data.items)
                }
            }
        }

        autocompleteCountry(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            boundaryCountry = boundaryCountry,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ) {
            when(it) {
                is OrsDataState.Error -> {

                }
                is OrsDataState.OrsData -> {
                    combinedList.addAll(it.data.items)
                }
            }
        }

        searchWorld(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ) {
            when(it) {
                is OrsDataState.Error -> {

                }
                is OrsDataState.OrsData -> {
                    combinedList.addAll(it.data.items)
                }
            }
        }

        autocompleteWorld(
            apiKey = apiKey,
            searchString = searchString,
            focus = focus,
            boundaryRectangle = boundaryRectangle,
            boundaryCircleRadius = boundaryCircleRadius,
            boundaryCircle = boundaryCircle,
            boundaryGid = boundaryGid,
            layers = layers,
            sources = sources,
            size = size,
            language = language,
        ) {
            when(it) {
                is OrsDataState.Error -> {

                }
                is OrsDataState.OrsData -> {
                    combinedList.addAll(it.data.items)
                }
            }
        }
    }
}
