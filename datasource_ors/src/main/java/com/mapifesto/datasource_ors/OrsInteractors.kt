package com.mapifesto.datasource_ors

class OrsInteractors(
    val getOrsSearchResults: GetOrsSearchResults,
    val getOrsAutocompleteResults: GetOrsAutocompleteResults,
) {

    companion object Factory {
        fun build(): OrsInteractors{
            val service = OrsService.build()
            return OrsInteractors(
                getOrsSearchResults = GetOrsSearchResults(
                    service = service,
                ),
                getOrsAutocompleteResults = GetOrsAutocompleteResults(
                    service = service,
                ),
            )
        }
    }
}