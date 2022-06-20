package com.mapifesto.datasource_ors

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

interface OrsService {

    // Suspend functions

    suspend fun search(
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
        language: String,
    ): SearchDto

    suspend fun autocomplete(
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
        language: String,
    ): SearchDto

    companion object Factory {
        fun build(): OrsService {
            return OrsServiceImpl(
                httpClient = HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(
                            kotlinx.serialization.json.Json {
                                ignoreUnknownKeys = true
                                isLenient = false
                            }
                        )
                    }
                }
            )
        }
    }
}