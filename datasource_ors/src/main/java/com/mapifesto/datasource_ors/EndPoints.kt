package com.mapifesto.datasource_ors

object EndPoints {

    private const val ORS_URL = "https://api.openrouteservice.org"
    private const val SEARCH_API = "/geocode/search"
    const val SEARCH = "$ORS_URL$SEARCH_API"

    private const val AUTOCOMPLETE_API = "/geocode/autocomplete"
    const val AUTOCOMPLETE = "$ORS_URL$AUTOCOMPLETE_API"

}