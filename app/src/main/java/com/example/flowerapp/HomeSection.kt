package com.example.flowerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.flowerapp.databinding.HomeSectionBinding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



class HomeSection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_section)

        // Start carousel part(don't touch)
//        setContent {
//            MaterialTheme {
//                MyApp() // Launch the MyApp composable
//            }
//        }

    }

//    @OptIn(ExperimentalPagerApi::class)
//    @Composable
//    fun MyApp(modifier: Modifier = Modifier) {
//
//        val images = listOf(
//            R.drawable.deal,
//            R.drawable.deal
//        )
//        val pagerState = rememberPagerState(
//            pageCount =
//            images.size
//        )
//        LaunchedEffect(Unit) {
//            while (true) {
//                delay(2000)
//                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
//                pagerState.scrollToPage(nextPage)
//            }
//        }
//        val scope = rememberCoroutineScope()
//
//        Column(
//            modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(modifier = modifier.wrapContentSize()) {
//                HorizontalPager(
//                    state = pagerState,
//                    modifier
//                        .wrapContentSize()
//
//                ) { currentPage ->
//
//                    Card(
//                        modifier
//                            .wrapContentSize()
//                            .padding(26.dp),
//                        elevation = CardDefaults.cardElevation(8.dp)
//                    ) {
//                        Image(
//                            painter = painterResource(id = images[currentPage]),
//                            contentDescription = ""
//                        )
//                    }
//                }
//            }
//
//            PageIndicator(
//                pageCount = images.size,
//                currentPage = pagerState.currentPage,
//                modifier = modifier
//            )
//
//        }
//    }
//
//    @Composable
//    fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier) {
//
//        Row(
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = modifier
//        ) {
//            repeat(pageCount) {
//                IndicatorDots(isSelected = it == currentPage, modifier = modifier)
//            }
//        }
//    }
//
//    @Composable
//    fun IndicatorDots(isSelected: Boolean, modifier: Modifier) {
//        val size = animateDpAsState(targetValue = if (isSelected) 12.dp else 10.dp, label = "")
//        Box(
//            modifier = modifier.padding(2.dp)
//                .size(size.value)
//                .clip(CircleShape)
//                .background(if (isSelected) Color(0xff373737) else Color(0xA8373737))
//        )
//    }
}