package com.mapifesto.datasource_ors

import com.mapifesto.datasource_ors.EndPoints.AUTOCOMPLETE
import com.mapifesto.datasource_ors.EndPoints.SEARCH
import io.ktor.client.*
import io.ktor.client.request.*

class OrsServiceImpl(
    private val httpClient: HttpClient,
): OrsService {

    override suspend fun search(
        apiKey: String,
        text: String,
        focusPointLon: String?,
        focusPointLat: String?,
        boundaryRectMinLon: String?,
        boundaryRectMinLat: String?,
        boundaryRectMaxLon: String?,
        boundaryRectMaxLat: String?,
        boundaryCircleLon: String?,
        boundaryCircleLat: String?,
        boundaryCircleRadius: String?,
        boundaryGid: String?,
        boundaryCountry: String?,
        sources: String?,
        layers: String?,
        size: String?,
    ): SearchDto {

        return httpClient.get {
            url("$SEARCH")
            parameter(key = "api_key", value = apiKey)
            parameter(key = "text", value = text)
            parameter(key = "focus.point.lon", value = focusPointLon)
            parameter(key = "focus.point.at", value = focusPointLat)
            parameter(key = "boundary.rect.min.lon", value = boundaryRectMinLon)
            parameter(key = "boundary.rect.min.lat", value = boundaryRectMinLat)
            parameter(key = "boundary.rect.max.lon", value = boundaryRectMaxLon)
            parameter(key = "boundary.rect.max.lat", value = boundaryRectMaxLat)
            parameter(key = "boundary.circle.lon", value = boundaryCircleLon)
            parameter(key = "boundary.circle.lat", value = boundaryCircleLat)
            parameter(key = "boundary.circle.radius", value = boundaryCircleRadius)
            parameter(key = "boundary.gid", value = boundaryGid)
            parameter(key = "boundary.country", value = boundaryCountry)
            parameter(key = "sources", value = sources)
            parameter(key = "layers", value = layers)
            parameter(key = "size", value = size)
        }
    }

    override suspend fun autocomplete(
        apiKey: String,
        text: String,
        focusPointLon: String?,
        focusPointLat: String?,
        boundaryRectMinLon: String?,
        boundaryRectMinLat: String?,
        boundaryRectMaxLon: String?,
        boundaryRectMaxLat: String?,
        boundaryCircleLon: String?,
        boundaryCircleLat: String?,
        boundaryCircleRadius: String?,
        boundaryGid: String?,
        boundaryCountry: String?,
        sources: String?,
        layers: String?,
        size: String?
    ): SearchDto {

        return httpClient.get {
            url("$AUTOCOMPLETE")
            parameter(key = "api_key", value = apiKey)
            parameter(key = "text", value = text)
            parameter(key = "focus.point.lon", value = focusPointLon)
            parameter(key = "focus.point.at", value = focusPointLat)
            parameter(key = "boundary.rect.min.lon", value = boundaryRectMinLon)
            parameter(key = "boundary.rect.min.lat", value = boundaryRectMinLat)
            parameter(key = "boundary.rect.max.lon", value = boundaryRectMaxLon)
            parameter(key = "boundary.rect.max.lat", value = boundaryRectMaxLat)
            parameter(key = "boundary.circle.lon", value = boundaryCircleLon)
            parameter(key = "boundary.circle.lat", value = boundaryCircleLat)
            parameter(key = "boundary.circle.radius", value = boundaryCircleRadius)
            parameter(key = "boundary.gid", value = boundaryGid)
            parameter(key = "boundary.country", value = boundaryCountry)
            parameter(key = "sources", value = sources)
            parameter(key = "layers", value = layers)
            parameter(key = "size", value = size)

            //url("https://api.openrouteservice.org/geocode/autocomplete?api_key=5b3ce3597851110001cf6248782b9145cbb64ba2a7b5962e1023c1de&text=Eiffel%20Tower&sources=openstreetmap&layers=venue")

        }

    }
}

