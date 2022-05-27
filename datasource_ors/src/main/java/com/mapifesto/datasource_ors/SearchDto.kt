package com.mapifesto.datasource_ors

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(

     @SerialName("geocoding")
     val geocoding: Geocoding,

     @SerialName("type")
     val type: String,

     @SerialName("features")
     val features: List<Feature>,

     @SerialName("bbox")
     val bbox: List<Double>,

) {
     @Serializable
     data class Geocoding (

          @SerialName("version")
          val version: String,

          @SerialName("attribution")
          val attribution: String,

          @SerialName("query")
          val query: Query,

          @SerialName("warnings")
          val warnings: List<String>,

          @SerialName("engine")
          val engine: Engine,

          @SerialName("timestamp")
          val timestamp: Long,
     ) {

          @Serializable
          data class Query(

               @SerialName("text")
               val text: String,

               @SerialName("size")
               val size: Int,

               @SerialName("sources")
               val sources: List<String>,

               @SerialName("layers")
               val layers: List<String>,

               @SerialName("private")
               val private: Boolean,

               @SerialName("lang")
               val lang: Lang,

               @SerialName("querySize")
               val querySize: Int,

               @SerialName("parser")
               val parser: String,

               @SerialName("parsed_text")
               val parsedText: ParsedText

          ) {

               @Serializable
               data class Lang(

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
               data class ParsedText(

                    @SerialName("city")
                    val city: String,

                    @SerialName("subject")
                    val subject: String,

                    @SerialName("locality")
                    val locality: String,
               )
          }

          @Serializable
          data class Engine(
               @SerialName("name")
               val name: String,

               @SerialName("author")
               val author: String,

               @SerialName("version")
               val version: String,
          )
          
     }

     @Serializable
     data class Feature(
          @SerialName("type")
          val type: String,

          @SerialName("geometry")
          val geometry: Geometry,

          @SerialName("properties")
          val properties: Properties,

          @SerialName("bbox")
          val bbox: List<Double>,
     ) {

          @Serializable
          data class Geometry (
               @SerialName("type")
               val type: String,

               @SerialName("coordinates")
               val coordinates: List<Double>,
          )

          @Serializable
          data class Properties (
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
               val housenumber: String,

               @SerialName("street")
               val street: String,

               @SerialName("postalcode")
               val postalcode: String,

               @SerialName("confidence")
               val confidence: Double,

               @SerialName("match_type")
               val matchType: String,

               @SerialName("distance")
               val distance: String,

               @SerialName("accuracy")
               val accuracy: String,

               @SerialName("country")
               val country: String,

               @SerialName("country_gid")
               val countryGid: String,

               @SerialName("country_a")
               val countryA: String,

               @SerialName("macroregion")
               val macroregion: String,

               @SerialName("macroregion_gid")
               val macroregionGid: String,

               @SerialName("region")
               val region: String,

               @SerialName("region_gid")
               val regionGid: String,

               @SerialName("region_a")
               val regionA: String,

               @SerialName("county")
               val county: String,

               @SerialName("county_gid")
               val countyGid: String,

               @SerialName("county_a")
               val countyA: String,

               @SerialName("localadmin")
               val localadmin: String,

               @SerialName("localadmin_gid")
               val localadminGid: String,

               @SerialName("locality")
               val locality: String,

               @SerialName("locality_gid")
               val localityGid: String,

               @SerialName("continent")
               val continent: String,

               @SerialName("continent_gid")
               val continentGid: String,

               @SerialName("label")
               val label: String,

               @SerialName("neighbourhood")
               val neighbourhood: String,

               @SerialName("neighbourhood_gid")
               val neighbourhoodGid: String,

               @SerialName("addendum")
               val addendum: Addendum
          ) {

               @Serializable
               data class Addendum(

                    @SerialName("osm")
                    val osm: List<String>
               )

          }
          
     }

}


