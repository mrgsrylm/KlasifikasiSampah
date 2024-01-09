package io.github.mrsrylm.skso.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.mrsrylm.skso.R
import io.github.mrsrylm.skso.ui.theme.SKSOTheme

@Composable
fun ScreenAppbar() {
    Box(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Current Screen",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Subtitle",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.baseline_apps_24),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Your content goes here
        }

        // Add your scrolling content here, for example, a LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp) // Adjust this value based on your app bar height
        ) {
            // Add your scrollable items
        }
    }

}

@Preview(showBackground = false)
@Composable
fun ScreenAppBarComponent() {
    SKSOTheme {
        ScreenAppbar()
    }
}