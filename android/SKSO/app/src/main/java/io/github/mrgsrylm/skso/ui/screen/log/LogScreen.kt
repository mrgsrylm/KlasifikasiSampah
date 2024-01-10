package io.github.mrgsrylm.skso.ui.screen.log

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.github.mrgsrylm.skso.R
import io.github.mrgsrylm.skso.common.ScreenStatus
import io.github.mrgsrylm.skso.common.unixTsToDate
import io.github.mrgsrylm.skso.common.unixTsToTime
import io.github.mrgsrylm.skso.data.model.LogModel
import io.github.mrgsrylm.skso.ui.theme.SKSOTheme
import io.github.mrgsrylm.skso.viewmodel.LogViewModel

@Composable
fun LogScreen(
    viewModel: LogViewModel = hiltViewModel()
) {
    val logVM by viewModel.logModels.observeAsState(initial = ScreenStatus.Loading)
    val logState = logVM

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.log_date),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.log_time),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
            Text(
                text = stringResource(id = R.string.log_waste),
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )
        }

        when {
            logState is ScreenStatus.Loading -> {
                Text(text = "Loading..")
            }

            logState is ScreenStatus.Success -> {
                LazyColumn {
                    val data: List<LogModel> = logState.uiData

                    items(data.size) { index ->
                        val log = data[index]
                        LogItem(
                            date = unixTsToDate(log.classifiedAt),
                            time = unixTsToTime(log.classifiedAt),
                            waste = log.result
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
fun LogScreenPreview() {
    SKSOTheme {
        LogScreen()
    }
}