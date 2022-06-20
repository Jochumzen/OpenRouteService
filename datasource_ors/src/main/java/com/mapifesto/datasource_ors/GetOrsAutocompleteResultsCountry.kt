package com.mapifesto.datasource_ors

import com.mapifesto.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetOrsAutocompleteResultsCountry(
    private val service: OrsService,
) {
    fun execute(
        orsSearchMembers: OrsSearchMembers
    ): Flow<OrsDataState<OrsSearchItems>> = flow {

        var errorMessage: String? = null

        val orsSearchDtoResult: SearchDto? = try {
            service.autocomplete(
                apiKey = orsSearchMembers.apiKey,
                text = orsSearchMembers.searchString,
                focusPointLon = orsSearchMembers.focus?.let {
                    it.lon.toString()
                },
                focusPointLat = orsSearchMembers.focus?.let {
                    it.lat.toString()
                },
                boundaryRectMinLon = orsSearchMembers.boundaryRectangle?.let {
                    it.southWest.lon.toString()
                },
                boundaryRectMinLat = orsSearchMembers.boundaryRectangle?.let {
                    it.southWest.lat.toString()
                },
                boundaryRectMaxLon = orsSearchMembers.boundaryRectangle?.let {
                    it.northEast.lon.toString()
                },
                boundaryRectMaxLat = orsSearchMembers.boundaryRectangle?.let {
                    it.northEast.lat.toString()
                },
                boundaryCircleLon = orsSearchMembers.boundaryCircle?.let {
                    it.lon.toString()
                },
                boundaryCircleLat = orsSearchMembers.boundaryCircle?.let {
                    it.lat.toString()
                },
                boundaryCircleRadius = orsSearchMembers.boundaryCircleRadius?.let {
                    it.toString()
                },
                boundaryGid = orsSearchMembers.boundaryGid?.let {
                    it
                },
                boundaryCountry = orsSearchMembers.boundaryCountry,
                layers = orsSearchMembers.layers.asString(),
                sources = orsSearchMembers.sources.asString(),
                size = orsSearchMembers.size.toString(),
                language = orsSearchMembers.language,
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