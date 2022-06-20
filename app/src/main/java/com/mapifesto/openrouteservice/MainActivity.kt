package com.mapifesto.openrouteservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapifesto.datasource_ors.OrsDataState
import com.mapifesto.datasource_ors.OrsIntermediary
import com.mapifesto.datasource_ors.OrsSearchMembers
import com.mapifesto.datasource_ors.ScoredOrsSearchItems
import com.mapifesto.domain.LatLon
import com.mapifesto.domain.OrsLayers
import com.mapifesto.domain.OrsSearchItems
import com.mapifesto.domain.OrsSources
import com.mapifesto.openrouteservice.ui.theme.OpenRouteServiceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var orsIntermediary: OrsIntermediary

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenRouteServiceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Compose(orsIntermediary = orsIntermediary)
                }
            }
        }
    }
}

@Composable
fun Compose(
    orsIntermediary: OrsIntermediary
) {

    val apiKey = "5b3ce3597851110001cf6248782b9145cbb64ba2a7b5962e1023c1de"
    var showWhat by remember {mutableStateOf("")}
    var errorMsg by remember {mutableStateOf("")}
    var orsSearchItems by remember { mutableStateOf<OrsSearchItems?>(null) }
    var scoredOrsSearchItems by remember { mutableStateOf<ScoredOrsSearchItems?>(null) }
    var outlinedText by remember {mutableStateOf("")}
    var languageText by remember {mutableStateOf("en-US")}
    var useFocused by remember {mutableStateOf(true)}
    var focusLat by remember {mutableStateOf("55.728392")}
    var focusLon by remember {mutableStateOf("13.176930")}
    var page by remember { mutableStateOf("start") }

    val layers = OrsLayers(
        address = false,
        venue = true,
        neighbourhood = false,
        locality = false,
        borough = false,
        localadmin = false,
        county = false,
        macrocounty = false,
        region = false,
        macroregion = false,
        country = false,
        coarse = false,      //All administrative layers, all but venue and address
    )

    val sources = OrsSources(
        openStreetMap = true,
        openAddresses = false,
        whosOnFirst = false,
        geonames = false
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column() {

            when (page) {

                "start" -> {
                    Text(text = "Ors Data source")
                    Button(
                        onClick = {
                            page = "search"
                        },
                    ) {
                            Text(text = "Search")
                    }
                    Button(
                        onClick = {
                            page = "settings"
                        },
                    ) {
                        Text(text = "Settings")
                    }
                }
                "search" -> {
                    Text(text = "Ors Data Source: Search Sweden")
                    Button(
                        onClick = {
                            page = "start"
                        },
                    ) {
                        Text(text = "Start")
                    }

                    Row() {
                        OutlinedTextField(
                            value = outlinedText,
                            onValueChange = {
                                outlinedText = it
                            }
                        )
                    }

                    Row {
                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.searchCountry(
                                    orsSearchMembers = OrsSearchMembers(
                                        apiKey = apiKey,
                                        searchString = outlinedText,
                                        boundaryCountry = "SE",
                                        layers = layers,
                                        sources = sources,
                                        size = 10,
                                        language = languageText,
                                        focus = if(useFocused) LatLon(lat = focusLat.toDouble(), lon = focusLon.toDouble()) else null
                                    )


                                ) {
                                    when(it) {
                                        is OrsDataState.Error -> { errorMsg = it.error}
                                        is OrsDataState.OrsData -> {
                                            showWhat = "Sweden results"
                                            orsSearchItems = it.data
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("S Sw")
                        }

                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.autocompleteCountry(
                                    orsSearchMembers = OrsSearchMembers(
                                        apiKey = apiKey,
                                        searchString = outlinedText,
                                        boundaryCountry = "SE",
                                        layers = layers,
                                        sources = sources,
                                        size = 10,
                                        language = languageText,
                                        focus = if(useFocused) LatLon(lat = focusLat.toDouble(), lon = focusLon.toDouble()) else null
                                    )

                                ) {
                                    when(it) {
                                        is OrsDataState.Error -> { errorMsg = it.error}
                                        is OrsDataState.OrsData -> {
                                            showWhat = "Auto Sweden results"
                                            orsSearchItems = it.data
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("A Sw")
                        }

                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.searchWorld(
                                    orsSearchMembers = OrsSearchMembers(
                                        apiKey = apiKey,
                                        searchString = outlinedText,
                                        layers = layers,
                                        sources = sources,
                                        size = 30,
                                        language = languageText,
                                        focus = if(useFocused) LatLon(lat = focusLat.toDouble(), lon = focusLon.toDouble()) else null
                                    )
                                ) {
                                    when(it) {
                                        is OrsDataState.Error -> { errorMsg = it.error}
                                        is OrsDataState.OrsData -> {
                                            showWhat = "World results"
                                            orsSearchItems = it.data
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("S W")
                        }

                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.autocompleteWorld(
                                    orsSearchMembers = OrsSearchMembers(
                                        apiKey = apiKey,
                                        searchString = outlinedText,
                                        layers = layers,
                                        sources = sources,
                                        size = 10,
                                        language = languageText,
                                        focus = if(useFocused) LatLon(lat = focusLat.toDouble(), lon = focusLon.toDouble()) else null
                                    )
                                ) {
                                    when(it) {
                                        is OrsDataState.Error -> { errorMsg = it.error}
                                        is OrsDataState.OrsData -> {
                                            showWhat = "Auto World results"
                                            orsSearchItems = it.data
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("A W")
                        }
                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.combinedSearch(
                                    orsSearchMembers = OrsSearchMembers(
                                        apiKey = apiKey,
                                        searchString = outlinedText,
                                        boundaryCountry = "SE",
                                        layers = layers,
                                        sources = sources,
                                        size = 10,
                                        language = languageText,
                                        focus = if(useFocused) LatLon(lat = focusLat.toDouble(), lon = focusLon.toDouble()) else null
                                    ),
                                    userPosition = LatLon(lat = 55.728392, lon = 13.176930)
                                ) {
                                    when(it) {
                                        is OrsDataState.Error -> { errorMsg = it.error}
                                        is OrsDataState.OrsData -> {
                                            showWhat = "Combined"
                                            scoredOrsSearchItems = it.data
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("Comb")
                        }
                    }

                    if(errorMsg != "") Text("Error: $errorMsg") else {
                        when(showWhat) {
                            "Sweden results", "World results", "Auto Sweden results", "Auto World results" -> {

                                LazyColumn(
                                    state = rememberLazyListState()
                                ) {
                                    items(orsSearchItems!!.items.map { "${it.name}, ${it.city?: "city?"} (${it.country}), ${it.latLon.print()}"}) {
                                        Text(it)
                                    }

                                }
                            }
                            "Combined" -> {
                                LazyColumn(
                                    state = rememberLazyListState()
                                ) {
/*                                    items(scoredOrsSearchItems!!.combinedList.map { "${it.orsSearchItem.name}, ${it.orsSearchItem.city?: "city"} (${it.orsSearchItem.country}), ${it.totalScore}"}) {
                                        Text(it)
                                    }*/

                                    items(scoredOrsSearchItems!!.combinedList) {
                                        Column() {
                                            Text("${it.orsSearchItem.name}, ${it.orsSearchItem.city?: "city?"} (${it.orsSearchItem.country}) ${it.distance}")
                                            Text("  ${it.searchCountryScore}+${it.autocompleteCountryScore}+${it.searchWorldScore}+${it.autocompleteWorldScore}+${it.similarity}+${it.wiki}+${it.distanceScore} = ${it.totalScore}")
                                        }
                                    }

                                }
                            }
                        }

                    }
                }
                "settings" -> {
                    Text(text = "Ors Data Source: Settings")
                    Button(
                        onClick = {
                            page = "start"
                        },
                    ) {
                        Text(text = "Start")
                    }
                    Row() {
                        Text("Lang: ")
                        OutlinedTextField(
                            value = languageText,
                            onValueChange = {
                                languageText = it
                            }
                        )
                    }
                    Row() {
                        Checkbox(checked = useFocused, onCheckedChange = {useFocused = it})
                        Text("Use Focus")

                    }
                    Row() {
                        OutlinedTextField(
                            value = focusLat,
                            onValueChange = {
                                focusLat = it
                            },
                            Modifier.width(150.dp),
                            enabled = useFocused

                        )
                        OutlinedTextField(
                            value = focusLon,
                            onValueChange = {
                                focusLon = it
                            },
                            Modifier.width(150.dp),
                            enabled = useFocused
                        )
                    }
                }
            }

            Row() {


            }
        }

    }

}