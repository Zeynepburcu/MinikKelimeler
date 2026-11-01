package com.minikkelimeler.app

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** An emoji picture from res/drawable-nodpi. */
@Composable
fun Emoji(@DrawableRes image: Int, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(image),
        contentDescription = null,
        modifier = modifier
    )
}

/** A round white button showing an emoji. */
@Composable
fun RoundButton(
    @DrawableRes image: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick)
    ) {
        Emoji(image, Modifier.size(size * 0.62f))
    }
}

/** Star counter shown in the corner. */
@Composable
fun StarCount(stars: Int, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Emoji(R.drawable.ui_star, Modifier.size(32.dp))
        Spacer(Modifier.width(6.dp))
        Text("$stars", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
    }
}

/** Top row: back button on the left, star counter on the right. */
@Composable
fun TopBar(onBack: () -> Unit, stars: Int? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        RoundButton(R.drawable.ui_back, onBack, size = 56.dp)
        if (stars != null) StarCount(stars)
    }
}

/** A simple flag for the language buttons. */
@Composable
fun Flag(lang: Lang, modifier: Modifier = Modifier) {
    Canvas(modifier.clip(RoundedCornerShape(8.dp))) {
        val w = size.width
        val h = size.height
        when (lang) {
            Lang.DE -> {
                drawRect(Color(0xFF000000), Offset(0f, 0f), Size(w, h / 3))
                drawRect(Color(0xFFDD0000), Offset(0f, h / 3), Size(w, h / 3))
                drawRect(Color(0xFFFFCE00), Offset(0f, 2 * h / 3), Size(w, h / 3))
            }

            Lang.EN -> {
                val blue = Color(0xFF012169)
                val red = Color(0xFFC8102E)
                drawRect(blue)
                drawLine(Color.White, Offset(0f, 0f), Offset(w, h), strokeWidth = h * 0.2f)
                drawLine(Color.White, Offset(w, 0f), Offset(0f, h), strokeWidth = h * 0.2f)
                drawLine(red, Offset(0f, 0f), Offset(w, h), strokeWidth = h * 0.07f)
                drawLine(red, Offset(w, 0f), Offset(0f, h), strokeWidth = h * 0.07f)
                drawRect(Color.White, Offset(w / 2 - h * 0.17f, 0f), Size(h * 0.34f, h))
                drawRect(Color.White, Offset(0f, h / 2 - h * 0.17f), Size(w, h * 0.34f))
                drawRect(red, Offset(w / 2 - h * 0.1f, 0f), Size(h * 0.2f, h))
                drawRect(red, Offset(0f, h / 2 - h * 0.1f), Size(w, h * 0.2f))
            }
        }
    }
}

/** A large rounded card with a picture and a caption. */
@Composable
fun PictureCard(
    @DrawableRes image: Int,
    caption: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 96.dp,
    badge: String? = null
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(color)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Emoji(image, Modifier.size(imageSize))
        Spacer(Modifier.height(8.dp))
        Text(caption, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF3E2723))
        if (badge != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                badge,
                fontSize = 14.sp,
                color = Color(0xFF6D4C41),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
