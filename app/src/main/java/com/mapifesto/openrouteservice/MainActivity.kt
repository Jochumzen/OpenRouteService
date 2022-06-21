package com.mapifesto.openrouteservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mapifesto.datasource_ors.*
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

    var sizeSC by remember { mutableStateOf("10") }
    var sizeAC by remember { mutableStateOf("10") }
    var sizeSW by remember { mutableStateOf("10") }
    var sizeAW by remember { mutableStateOf("10") }

    var scInt by remember { mutableStateOf("40") }
    var scSl by remember { mutableStateOf("1") }
    var acInt by remember { mutableStateOf("40") }
    var acSl by remember { mutableStateOf("1") }
    var swInt by remember { mutableStateOf("40") }
    var swSl by remember { mutableStateOf("1") }
    var awInt by remember { mutableStateOf("40") }
    var awSl by remember { mutableStateOf("1") }

    var simFac by remember { mutableStateOf("50") }
    var exact by remember { mutableStateOf("10") }
    var wiki by remember { mutableStateOf("50") }
    var dInt by remember { mutableStateOf("200") }
    var dSl by remember { mutableStateOf("30") }

    var dZeroAt by remember { mutableStateOf("") }


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

    var orsScoreParameters = OrsScoreParameters()

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
                                        sizeSearchCountry = sizeSC.toInt(),
                                        sizeAutoCompleteCountry = sizeSC.toInt(),
                                        sizeSearchWorld = sizeSW.toInt(),
                                        sizeAutoCompleteWorld = sizeAW.toInt(),
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
                                        sizeSearchCountry = sizeSC.toInt(),
                                        sizeAutoCompleteCountry = sizeSC.toInt(),
                                        sizeSearchWorld = sizeSW.toInt(),
                                        sizeAutoCompleteWorld = sizeAW.toInt(),
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
                                        sizeSearchCountry = sizeSC.toInt(),
                                        sizeAutoCompleteCountry = sizeSC.toInt(),
                                        sizeSearchWorld = sizeSW.toInt(),
                                        sizeAutoCompleteWorld = sizeAW.toInt(),
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
                                        sizeSearchCountry = sizeSC.toInt(),
                                        sizeAutoCompleteCountry = sizeSC.toInt(),
                                        sizeSearchWorld = sizeSW.toInt(),
                                        sizeAutoCompleteWorld = sizeAW.toInt(),
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
                                        sizeSearchCountry = sizeSC.toInt(),
                                        sizeAutoCompleteCountry = sizeSC.toInt(),
                                        sizeSearchWorld = sizeSW.toInt(),
                                        sizeAutoCompleteWorld = sizeAW.toInt(),
                                        language = languageText,
                                        focus = if(useFocused) LatLon(lat = focusLat.toDouble(), lon = focusLon.toDouble()) else null
                                    ),
                                    userPosition = LatLon(lat = 55.728392, lon = 13.176930),
                                    orsScoreParameters = orsScoreParameters
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
                                    items(orsSearchItems!!.items.map { "${it.name}, ${it.city?: "city?"} (${it.country?: "country?"}), ${it.latLon.print()}"}) {
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
                    //dZeroAt = orsScoreParameters.distanceZeroScore().toString()
                    Text(text = "Ors Data Source: Settings")
                    Button(
                        onClick = {
                            page = "start"
                        },
                    ) {
                        Text(text = "Start")
                    }
                    Row(
                        modifier = Modifier.height(30.dp)

                    ) {
                        Text("Lang: ")
                        BasicTextField(
                            value = languageText,
                            onValueChange = {
                                languageText = it
                            },

                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Checkbox(checked = useFocused, onCheckedChange = {useFocused = it}, modifier = Modifier.height(20.dp))
                        Text("Use Focus")

                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(" Lat:  ")
                        BasicTextField(
                            value = focusLat,
                            onValueChange = {
                                focusLat = it
                            },
                            Modifier.width(80.dp),
                            enabled = useFocused

                        )
                        Text(" Lon:  ")
                        BasicTextField(
                            value = focusLon,
                            onValueChange = {
                                focusLon = it
                            },
                            Modifier.width(80.dp),
                            enabled = useFocused
                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("size SC:  ")
                        BasicTextField(
                            value = sizeSC,
                            onValueChange = {
                                sizeSC = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("AC:  ")
                        BasicTextField(
                            value = sizeAC,
                            onValueChange = {
                                sizeAC = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                        Text("SW:  ")
                        BasicTextField(
                            value = sizeSW,
                            onValueChange = {
                                sizeSW = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("AW:  ")
                        BasicTextField(
                            value = sizeAW,
                            onValueChange = {
                                sizeAW = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("SC int: ")
                        BasicTextField(
                            value = scInt,
                            onValueChange = {
                                scInt = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("sl: ")
                        BasicTextField(
                            value = scSl,
                            onValueChange = {
                                scSl = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("AC int: ")
                        BasicTextField(
                            value = acInt,
                            onValueChange = {
                                acInt = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("sl: ")
                        BasicTextField(
                            value = acSl,
                            onValueChange = {
                                acSl = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("SW int: ")
                        BasicTextField(
                            value = swInt,
                            onValueChange = {
                                swInt = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("sl: ")
                        BasicTextField(
                            value = swSl,
                            onValueChange = {
                                swSl = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("AW int: ")
                        BasicTextField(
                            value = awInt,
                            onValueChange = {
                                awInt = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("sl: ")
                        BasicTextField(
                            value = awSl,
                            onValueChange = {
                                awSl = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Distance int: ")
                        BasicTextField(
                            value = dInt,
                            onValueChange = {
                                dInt = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                        Text("sl: ")
                        BasicTextField(
                            value = dSl,
                            onValueChange = {
                                dSl = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused
                        )
                        Text("0 at $dZeroAt km")
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Wiki: ")
                        BasicTextField(
                            value = wiki,
                            onValueChange = {
                                wiki = it
                            },
                            Modifier.width(30.dp),
                            enabled = useFocused

                        )
                    }
                    Row(
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Sim factor: ")
                        BasicTextField(
                            value = simFac,
                            onValueChange = {
                                simFac = it
                            },
                            Modifier.width(50.dp),
                            enabled = useFocused

                        )
                        Text("exact: ")
                        BasicTextField(
                            value = exact,
                            onValueChange = {
                                exact = it
                            },
                            Modifier.width(50.dp),
                            enabled = useFocused
                        )
                    }

                    Row() {
                        Button(
                            onClick = {
                                orsScoreParameters = OrsScoreParameters.factory(
                                    searchCountryScoreIntercept = scInt.toInt(),
                                    searchCountryScoreSlope = scSl.toInt(),
                                    autocompleteCountryScoreIntercept = acInt.toInt(),
                                    autocompleteCountryScoreSlope = acSl.toInt(),
                                    searchWorldScoreIntercept = swInt.toInt(),
                                    searchWorldScoreSlope = swSl.toInt(),
                                    autocompleteWorldScoreIntercept = acInt.toInt(),
                                    autocompleteWorldScoreSlope = acSl.toInt(),
                                    similarityFactor = simFac.toInt(),
                                    exactMatchBonus = exact.toInt(),
                                    wikiBonus = wiki.toInt(),
                                    distanceIntercept = dInt.toInt(),
                                    distanceSlope = dSl.toInt(),
                                )
                                dZeroAt = orsScoreParameters.distanceZeroScore().toString()
                                val z = 1
                            }
                        ) {
                            Text("Update")
                        }
                    }

                }
            }


        }

    }

}