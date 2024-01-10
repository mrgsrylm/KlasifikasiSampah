package io.github.mrgsrylm.skso.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.mrgsrylm.skso.common.ScreenStatus
import io.github.mrgsrylm.skso.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val organikVM by viewModel.organikTotal.observeAsState(initial = ScreenStatus.Loading)
    val organikState = organikVM
    val anorganikVM by viewModel.anorganikTotal.observeAsState(initial = ScreenStatus.Loading)
    val anorganikState = anorganikVM

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SKSO",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 4.dp, bottom = 4.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                IntroSection()
            }
            item {
                WasteCounterSection(
                    organikState = organikState,
                    anorganikState = anorganikState
                )
            }
            item {

            }
            items(50) { index ->
//                when {
//                    userState is ScreenStatus.Success -> {
//                        val userModel: UserModel = userState.uiData
//                        ListItem(userModel.email)
//                    }
//
//                    userState is ScreenStatus.Error -> {
//                        Text(text = "Error")
//                    }
//
//                    userState is ScreenStatus.Loading -> {
//                        Text(text = "Loading...")
//                    }
//                }
            }
        }
    }
}