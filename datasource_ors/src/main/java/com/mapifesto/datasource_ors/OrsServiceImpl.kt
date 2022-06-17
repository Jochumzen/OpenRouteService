package com.mapifesto.datasource_ors

import com.mapifesto.datasource_ors.EndPoints.SEARCH
import io.ktor.client.*
import io.ktor.client.request.*

class OrsServiceImpl(
    private val httpClient: HttpClient,
): OrsService {

    override suspend fun search(apiKey: String): SearchDto {
        return httpClient.get {
            url("$SEARCH?api_key=$apiKey")
        }
    }


}

