package com.devicetracker.ui.home

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.devicetracker.R
import com.devicetracker.ui.theme.DeviceTrackerTheme
import kotlinx.coroutines.launch


enum class HomeScreenPage(
    @StringRes val titleResId: Int,
    @DrawableRes val drawableResId: Int) {
    MEMBERS(R.string.str_member, R.drawable.ic_baseline_users),
    DEVICES(R.string.str_devices, R.drawable.ic_device_landscape_24)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val pages: Array<HomeScreenPage> = HomeScreenPage.values()

    val pagerState = rememberPagerState(pageCount = {pages.size})
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold (topBar = {
        HomeTopAppBar()
    }){
        HomeScreenMainContent(
            pagerState = pagerState,
            pages = pages,
            Modifier.padding(top = it.calculateTopPadding()))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar() {
    CenterAlignedTopAppBar(title = {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center){
              Text(
                  text = stringResource(id = R.string.app_name),
                  style = MaterialTheme.typography.displaySmall
              )
    }})
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeScreenMainContent(
    pagerState: PagerState,
    pages: Array<HomeScreenPage>,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        val coroutineScope = rememberCoroutineScope()

        TabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, homeScreenPage ->
                val title = stringResource(id = homeScreenPage.titleResId)
                Tab(selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = title)},
                    icon = {
                        Icon(painter = painterResource(id = homeScreenPage.drawableResId), contentDescription = title )
                    },
                    unselectedContentColor = MaterialTheme.colorScheme.secondary
                )

            }
        }

        HorizontalPager(state = pagerState,
            verticalAlignment = Alignment.Top) {index ->
            when(pages[index]) {
                HomeScreenPage.MEMBERS -> {
                    /*Text(text = "Member page will goes here",
                        Modifier.align(alignment =  Alignment.CenterHorizontally).fillMaxSize())*/
                    DeviceListScreen()
                }
                HomeScreenPage.DEVICES-> {
                    Text(text = "Device page will goes here",
                        Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .fillMaxSize())
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    DeviceTrackerTheme {
        HomeScreen()
    }
}