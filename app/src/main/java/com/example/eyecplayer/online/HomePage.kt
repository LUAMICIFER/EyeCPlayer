package com.example.eyecplayer.online

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.eyecplayer.R

@Composable
fun homePage(){
    Box(Modifier.fillMaxSize()){
        Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Image(
                painter = painterResource(id = R.drawable.baseline_play_arrow_24),
                contentDescription = "Icon"
            )
            IconButton(
                onClick = { TODO() },Modifier.size(18.dp)
            ) {Image(painter = painterResource(id = R.drawable.baseline_play_arrow_24), contentDescription = "search Icon") }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun homePagepreview() {
    homePage()
}