package com.selvaganesh7378.viduthalai.screens.onboarding

import android.R.attr.scaleX
import android.R.attr.scaleY
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.selvaganesh7378.viduthalai.model.OnboardingPage
import kotlin.math.absoluteValue

@Composable
fun OnBoardingScreen(
    pages: List<OnboardingPage>,
    onAgree : () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { pages.size }

    // Define gradient backgrounds for each page
    val gradients = listOf(
        Brush.verticalGradient(listOf(Color(0xFF6A11CB), Color(0xFF2575FC))),
        Brush.verticalGradient(listOf(Color(0xFF00B4DB), Color(0xFF0083B0))),
        Brush.verticalGradient(listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))),
        Brush.verticalGradient(listOf(Color(0xFF43CEA2), Color(0xFF185A9D)))

    )

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (
                    (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    ).absoluteValue

            val onboardingPage = pages[page]

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradients[page])
                    .navigationBarsPadding()
                    .statusBarsPadding()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = lerp(0.5f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                            val scale = lerp(0.85f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                            scaleX = scale
                            scaleY = scale
                        },
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        // Centered content
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = onboardingPage.imageRes),
                                contentDescription = onboardingPage.title,
                                modifier = Modifier
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(bottom = 24.dp)
                            )
                            Text(
                                text = onboardingPage.title,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = onboardingPage.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // "Agree" button at bottom-end
                        if (page == pages.lastIndex) {
                            Button(
                                onClick = onAgree,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                            ) {
                                Text("Agree")
                            }
                        }
                    }


                }
            }
        }

        // Page Indicator
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.White else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp)
                )
            }
        }
    }
}



@Composable
fun PageContent(pageCount: Int, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "page count: $pageCount"
        )
    }
}

@Preview
@Composable
fun PreviewOnBoardingScreen(modifier: Modifier = Modifier) {
//    OnBoardingScreen()
}