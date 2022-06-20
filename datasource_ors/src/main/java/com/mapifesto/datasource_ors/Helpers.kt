package com.mapifesto.datasource_ors

import com.mapbox.geojson.Point
import com.mapbox.turf.TurfMeasurement
import com.mapifesto.domain.LatLon

object Helpers {
    private fun mapLatLonToPoint(latLon: LatLon?): Point? {
        return latLon?.let { Point.fromLngLat(latLon.lon, latLon.lat)}
    }



    fun distance(pos1: LatLon?, pos2: LatLon): Double? {
        val latLonAsPoint = mapLatLonToPoint(pos1)
        return if (latLonAsPoint != null)  TurfMeasurement.distance(latLonAsPoint, Point.fromLngLat(pos2.lon,pos2.lat))*1000 else null
    }

    // Pretty distance with one point and a LatLon
    fun distancePretty(pos1: LatLon?, pos2: LatLon): String {
        val distanceDouble = distance(pos1, pos2) ?: return "Unknown distance"
        return when (distanceDouble.toInt()) {
            in 0..999 -> "${distanceDouble.toInt()} meters"
            in 1000..99999 -> "${(distanceDouble/1000).toInt()}.${((distanceDouble - 1000*(distanceDouble/1000).toInt())/100).toInt()} km"
            else -> "${(distanceDouble/1000).toInt()} km"
        }
    }
    fun distancePretty(distance: Double?): String {
        val distanceDouble = distance ?: return "Unknown distance"
        return when (distanceDouble.toInt()) {
            in 0..999 -> "${distanceDouble.toInt()} meters"
            in 1000..99999 -> "${(distanceDouble/1000).toInt()}.${((distanceDouble - 1000*(distanceDouble/1000).toInt())/100).toInt()} km"
            else -> "${(distanceDouble/1000).toInt()} km"
        }
    }
}