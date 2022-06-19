package com.mapifesto.openrouteservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mapifesto.datasource_ors.OrsDataState
import com.mapifesto.datasource_ors.OrsIntermediary
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
    var outlinedText by remember {mutableStateOf("")}
    var page by remember { mutableStateOf("start") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column() {

            when (page) {

                "start" -> {
                    Text(text = "Ors Data source")
                    Button(
                        onClick = {
                            page = "search everything"
                        },
                    ) {
                            Text(text = "Search")
                    }
                }
                "search everything" -> {
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
                                    apiKey = apiKey,
                                    searchString = outlinedText,
                                    boundaryCountry = "SE",
                                    layers = OrsLayers(
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
                                    ),
                                    sources = OrsSources(
                                        openStreetMap = true,
                                        openAddresses = false,
                                        whosOnFirst = false,
                                        geonames = false
                                    ),
                                    size = 10,
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
                            Text("S Sweden")
                        }
                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.searchWorld(
                                    apiKey = apiKey,
                                    searchString = outlinedText,
                                    layers = OrsLayers(
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
                                    ),
                                    sources = OrsSources(
                                        openStreetMap = true,
                                        openAddresses = false,
                                        whosOnFirst = false,
                                        geonames = false
                                    ),
                                    size = 10,
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
                            Text("S World")
                        }
                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.autocompleteCountry(
                                    apiKey = apiKey,
                                    searchString = outlinedText,
                                    boundaryCountry = "SE",
                                    layers = OrsLayers(
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
                                    ),
                                    sources = OrsSources(
                                        openStreetMap = true,
                                        openAddresses = false,
                                        whosOnFirst = false,
                                        geonames = false
                                    ),
                                    size = 10,
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
                            Text("A Sweden")
                        }
                        Button(
                            onClick = {
                                showWhat = ""
                                errorMsg = ""
                                orsIntermediary.autocompleteWorld(
                                    apiKey = apiKey,
                                    searchString = outlinedText,
                                    focus = LatLon(
                                        lat = 55.0,
                                        lon = 13.0
                                    ),
                                    layers = OrsLayers(
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
                                    ),
                                    sources = OrsSources(
                                        openStreetMap = true,
                                        openAddresses = false,
                                        whosOnFirst = false,
                                        geonames = false
                                    ),
                                    size = 10,
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
                            Text("A World")
                        }
                    }

                    if(errorMsg != "") Text("Error: $errorMsg") else {
                        when(showWhat) {
                            "Sweden results", "World results", "Auto Sweden results", "Auto World results" -> {

                                LazyColumn(
                                    state = rememberLazyListState()
                                ) {
                                    items(orsSearchItems!!.items.map { "${it.name}, ${it.country}, ${it.latLon.print()}"}) {
                                        Text(it)
                                    }

                                }
                            }
                        }

                    }
                }
            }

            Row() {


            }
        }

    }

}