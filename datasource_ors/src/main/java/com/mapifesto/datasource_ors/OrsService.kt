package com.mapifesto.datasource_ors

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*

interface OrsService {

    // Suspend functions

    //suspend fun search(apiKey: String): SearchDto

    companion object Factory {
        fun build(): OrsService {
            return OrsServiceImpl(
                httpClient = HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(
                            kotlinx.serialization.json.Json {
                                ignoreUnknownKeys = false
                                isLenient = false
                            }
                        )
                    }
                }
            )
        }
    }
}