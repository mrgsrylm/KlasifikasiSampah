package io.github.mrsrylm.skso.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.mrsrylm.skso.ui.component.WeatherData

@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            IntroSection()
        }
        item {
            WasteCounterSection()
        }
        item {

        }
        items(50) { index ->
            ListItem("Item $index")
        }
    }
}






