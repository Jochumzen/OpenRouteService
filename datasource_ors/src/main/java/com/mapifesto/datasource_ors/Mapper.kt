package com.mapifesto.datasource_ors

import com.mapifesto.domain.OrsSearchItem
import com.mapifesto.domain.OrsSearchItems

object Mapper {

    fun createOrsSearchItems(dto: SearchDto): OrsSearchItems? {

        return OrsSearchItems(
            items =
                dto.features.map {
                    OrsSearchItem(
                        id = try {
                                it.properties.id.toLong()
                            }
                            catch (ex: NumberFormatException) {
                                return null
                            },
                        name = it.properties.name
                    )
                }
        )
    }

}