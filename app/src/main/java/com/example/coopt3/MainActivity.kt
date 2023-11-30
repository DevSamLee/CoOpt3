package com.example.coopt3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.coopt3.models.Music
import com.example.coopt3.sealed.DataState
import com.example.coopt3.ui.theme.CoOpt3Theme
import com.example.coopt3.ui.theme.gray_fade
import com.example.coopt3.viewmodels.MainViewModel
import kotlin.random.Random

//Refactored you viewModels. You do not have to pass them down as params. Already set at the top level.

//Another option to use ViewModel is to assign it to a local variable in your local functions.
//This will also allow you to use several viewModels

//Code
// implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

// val localviewModel = viewModel{ MainViewModel}
// Text(localviewModel.variable)
// localviewModel.function()
// That will give you access to that viewmodel's variables and functions
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val items = (1..100).map {
            ListItem(
                height = Random.nextInt(100, 300).dp,
                color = Color(Random.nextLong(0xFFFFFFFF)).copy(1f)
            )
        }
        setContent {
            CoOpt3Theme {
                // Create a NavHostController
                val navController = rememberNavController()

                // Set up the navigation graph using NavHost
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable("staggeredGrid") {
                        StaggeredGridScreen(items)
                    }
                }

                // Top level layout
                Column {
                    TopAppBar(
                        title = {
                            Text(text = "Liked Music in 2023")
                        },
                    )

                    // NavHost to navigate between screens
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController)
                        }
                        composable("staggeredGrid") {
                            StaggeredGridScreen(items)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MainScreen(navController: NavHostController) {
        Column {
            // Content specific to the MainScreen
            SetData(navController)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun StaggeredGridScreen(items: List<ListItem>) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(150.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp
        ) {
            items(items) { item ->
                RandomColorBox(item = item)
            }
        }
    }

    @Composable
    fun SetData( navController: NavHostController) {
        when (val result = viewModel.response.value) {
            is DataState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is DataState.Success -> {
                ShowLazyList(result.data)
            }
            is DataState.Failure -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.message,
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    )
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error fetching data",
                        fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                    )
                }
            }
        }
    }

    @Composable
    fun ShowLazyList(musics: MutableList<Music>) {
        LazyColumn {
            items(musics) { music ->
                CardItem(music)
            }
        }
    }

    @Composable
    fun CardItem(music: Music) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = rememberImagePainter(music.Image),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "My content description",
                    contentScale = ContentScale.FillWidth
                )
                Text(
                    text = music.Title!!,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(gray_fade),
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
            }
        }
    }

    data class ListItem(
        val height: Dp,
        val color: Color
    )

    @Composable
    fun RandomColorBox(item: ListItem) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(item.height)
            .clip(RoundedCornerShape(10.dp))
            .background(item.color))
    }
}
