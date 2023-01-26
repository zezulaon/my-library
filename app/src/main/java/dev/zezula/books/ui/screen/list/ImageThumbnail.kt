package dev.zezula.books.ui.screen.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.zezula.books.R

@Composable
@OptIn(ExperimentalGlideComposeApi::class)
internal fun ImageThumbnail(
    modifier: Modifier = Modifier,
    bookThumbnailUri: String? = null,
) {
    val iconShape = RoundedCornerShape(12.dp)
    Box {
        Box(
            modifier = modifier
                .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = iconShape),
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 12.dp, start = 4.dp)
                    .fillMaxWidth(fraction = .5f),
                contentScale = ContentScale.Crop,
                painter = painterResource(id = R.drawable.ic_bookmark),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.surface),
                contentDescription = null,
            )
        }
        bookThumbnailUri?.let {
            GlideImage(
                modifier = modifier
                    .clip(iconShape)
                    .border(4.dp, MaterialTheme.colorScheme.secondaryContainer, iconShape)
                    .padding(4.dp),
                contentScale = ContentScale.Crop,
                model = it,
                contentDescription = null,
            )
        }
    }
}
