package com.mapifesto.datasource_ors

class OrsInteractors(
    val getOrsSearchResultsCountry: GetOrsSearchResultsCountry,
    val getOrsAutocompleteResultsCountry: GetOrsAutocompleteResultsCountry,
    val getOrsSearchResultsWorld: GetOrsSearchResultsWorld,
    val getOrsAutocompleteResultsWorld: GetOrsAutocompleteResultsWorld
) {

    companion object Factory {
        fun build(): OrsInteractors{
            val service = OrsService.build()
            return OrsInteractors(
                getOrsSearchResultsCountry = GetOrsSearchResultsCountry(
                    service = service,
                ),
                getOrsAutocompleteResultsCountry = GetOrsAutocompleteResultsCountry(
                    service = service,
                ),
                getOrsSearchResultsWorld = GetOrsSearchResultsWorld(
                    service = service,
                ),
                getOrsAutocompleteResultsWorld = GetOrsAutocompleteResultsWorld(
                    service = service,
                ),
            )
        }
    }
}