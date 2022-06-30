package com.mapifesto.datasource_ors

import com.mapifesto.domain.LatLon
import com.mapifesto.domain.OrsSearchItem
import com.mapifesto.domain.OrsSearchItems

object Mapper {

    fun createOrsSearchItems(dto: SearchDto): OrsSearchItems? {

        return OrsSearchItems(
            items =
                dto.features.map {
                    OrsSearchItem(
                        id = it.properties.id,
                        name = it.properties.name,
                        latLon = LatLon(
                            lon = it.geometry.coordinates[0],
                            lat = it.geometry.coordinates[1]
                        ),
                        country = it.properties.country,
                        locality = it.properties.locality,
                        city = it.properties.locality,
                        wikiId = it.properties.addendum?.osm?.wikidata,
                        region = it.properties.region,
                        county = it.properties.county,
                        layer = it.properties.layer,
                        gid = it.properties.gid,
                        street = it.properties.street,
                        houseNumber = it.properties.housenumber,
                    )
                }
        )
    }

}