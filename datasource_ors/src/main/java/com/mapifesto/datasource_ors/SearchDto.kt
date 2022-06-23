package com.mapifesto.datasource_ors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(

     @SerialName("geocoding")
     val geocoding: GeocodingDto,

     @SerialName("type")
     val type: String,

     @SerialName("features")
     val features: List<FeatureDto>,

     @SerialName("bbox")
     val bbox: List<Double>? = null,

     ) {
     @Serializable
     data class GeocodingDto (

          @SerialName("version")
          val version: String,

          @SerialName("attribution")
          val attribution: String,

          @SerialName("query")
          val query: QueryDto,

          @SerialName("warnings")
          val warnings: List<String>? = null,

          @SerialName("engine")
          val engine: EngineDto,

          @SerialName("timestamp")
          val timestamp: Long,
     ) {

          @Serializable
          data class QueryDto(

               @SerialName("text")
               val text: String,

               @SerialName("size")
               val size: Int,

               @SerialName("sources")
               val sources: List<String>? = null,

               @SerialName("layers")
               val layers: List<String>,

               @SerialName("private")
               val private: Boolean,

               @SerialName("boundary.country")
               val boundaries: List<String>? = null,

               @SerialName("lang")
               val lang: LangDto,

               @SerialName("querySize")
               val querySize: Int,

               @SerialName("parser")
               val parser: String? = null,

               @SerialName("parsed_text")
               val parsedText: ParsedTextDto? = null



          ) {

               @Serializable
               data class LangDto(

                    @SerialName("name")
                    val name: String,

                    @SerialName("iso6391")
                    val iso6391: String,

                    @SerialName("iso6393")
                    val iso6393: String,

                    @SerialName("via")
                    val via: String,

                    @SerialName("defaulted")
                    val defaulted: Boolean,

               )

               @Serializable
               data class ParsedTextDto(

                    @SerialName("city")
                    val city: String? = null,

                    @SerialName("subject")
                    val subject: String,

                    @SerialName("street")
                    val street: String? = null,

                    @SerialName("locality")
                    val locality: String? = null,

                    @SerialName("admin")
                    val admin: String? = null,
               )
          }

          @Serializable
          data class EngineDto(
               @SerialName("name")
               val name: String,

               @SerialName("author")
               val author: String,

               @SerialName("version")
               val version: String,
          )
          
     }

     @Serializable
     data class FeatureDto(
          @SerialName("type")
          val type: String,

          @SerialName("geometry")
          val geometry: GeometryDto,

          @SerialName("properties")
          val properties: PropertiesDto,

          @SerialName("bbox")
          val bbox: List<Double>? = null,
     ) {

          @Serializable
          data class GeometryDto (
               @SerialName("type")
               val type: String,

               @SerialName("coordinates")
               val coordinates: List<Double>,
          )

          @Serializable
          data class PropertiesDto (
               @SerialName("id")
               val id: String,

               @SerialName("gid")
               val gid: String,

               @SerialName("layer")
               val layer: String,

               @SerialName("source")
               val source: String,

               @SerialName("source_id")
               val sourceId: String,

               @SerialName("name")
               val name: String,

               @SerialName("housenumber")
               val housenumber: String? = null,

               @SerialName("street")
               val street: String? = null,

               @SerialName("postalcode")
               val postalcode: String? = null,

               @SerialName("confidence")
               val confidence: Double? = null,

               @SerialName("match_type")
               val matchType: String? = null,

               @SerialName("distance")
               val distance: Double? = null,

               @SerialName("accuracy")
               val accuracy: String,

               @SerialName("country")
               val country: String? = null,

               @SerialName("country_gid")
               val countryGid: String? = null,

               @SerialName("country_a")
               val countryA: String? = null,

               @SerialName("macroregion")
               val macroregion: String? = null,

               @SerialName("macroregion_gid")
               val macroregionGid: String? = null,

               @SerialName("region")
               val region: String? = null,

               @SerialName("region_gid")
               val regionGid: String? = null,

               @SerialName("region_a")
               val regionA: String? = null,

               @SerialName("county")
               val county: String? = null,

               @SerialName("county_gid")
               val countyGid: String? = null,

               @SerialName("county_a")
               val countyA: String? = null,

               @SerialName("localadmin")
               val localadmin: String? = null,

               @SerialName("localadmin_gid")
               val localadminGid: String? = null,

               @SerialName("locality")
               val locality: String? = null,

               @SerialName("locality_gid")
               val localityGid: String? = null,

               @SerialName("continent")
               val continent: String,

               @SerialName("continent_gid")
               val continentGid: String,

               @SerialName("label")
               val label: String,

               @SerialName("neighbourhood")
               val neighbourhood: String? = null,

               @SerialName("neighbourhood_gid")
               val neighbourhoodGid: String? = null,

               @SerialName("addendum")
               val addendum: AddendumDto? = null
          ) {

               @Serializable
               data class AddendumDto(

                    @SerialName("osm")
                    val osm: OsmDto? = null,

                    @SerialName("geonames")
                    val geonames: GeonamesDto? = null,

                    @SerialName("concordances")
                    val concordances: ConcordancesDto? = null,

               ) {

                    @Serializable
                    data class OsmDto(

                         @SerialName("operator")
                         val operator: String? = null,

                         @SerialName("wikidata")
                         val wikidata: String? = null,

                         @SerialName("website")
                         val website: String? = null,

                    )

                    @Serializable
                    data class GeonamesDto(

                         @SerialName("feature_code")
                         val featureCode: String,
                    )

                    @Serializable
                    data class ConcordancesDto(

                         @SerialName("gn:id")
                         val gnId: Long? = null,

                         @SerialName("qs_pg:id")
                         val qsPqId: Long? = null,

                    )

               }

          }
          
     }

}


