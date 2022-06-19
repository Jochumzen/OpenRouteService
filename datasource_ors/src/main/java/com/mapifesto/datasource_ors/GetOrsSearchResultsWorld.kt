package com.mapifesto.datasource_ors

import com.mapifesto.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetOrsSearchResultsWorld(
    private val service: OrsService,
) {
    fun execute(
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
    ): Flow<OrsDataState<OrsSearchItems>> = flow {

        var errorMessage: String? = null

        val orsSearchDtoResult: SearchDto? = try {
            service.search(
                apiKey = apiKey,
                text = searchString,
                focusPointLon = focus?.let {
                    it.lon.toString()
                },
                focusPointLat = focus?.let {
                    it.lat.toString()
                },
                boundaryRectMinLon = boundaryRectangle?.let {
                    it.southWest.lon.toString()
                },
                boundaryRectMinLat = boundaryRectangle?.let {
                    it.southWest.lat.toString()
                },
                boundaryRectMaxLon = boundaryRectangle?.let {
                    it.northEast.lon.toString()
                },
                boundaryRectMaxLat = boundaryRectangle?.let {
                    it.northEast.lat.toString()
                },
                boundaryCircleLon = boundaryCircle?.let {
                    it.lon.toString()
                },
                boundaryCircleLat = boundaryCircle?.let {
                    it.lat.toString()
                },
                boundaryCircleRadius = boundaryCircleRadius?.let {
                    it.toString()
                },
                boundaryGid = boundaryGid?.let {
                    it
                },
                boundaryCountry = null,
                layers = layers.asString(),
                sources = sources.asString(),
                size = size.toString(),
            )
        } catch (e: Exception) {
            e.printStackTrace()
            errorMessage = e.message?: "No error message provided"
            null
        }

        if (orsSearchDtoResult == null) {
            emit(OrsDataState.Error("Error executing MyInteractor. Error message: $errorMessage"))
            return@flow
        }

        val orsSearchItems: OrsSearchItems? = Mapper.createOrsSearchItems(orsSearchDtoResult)

        if (orsSearchItems == null)
            emit(OrsDataState.Error("Map to domain object failed"))
        else
            emit(OrsDataState.OrsData(orsSearchItems))
    }
}