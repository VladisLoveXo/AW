package app.awake.ui.trees

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.awake.ui.theme.AwakeTheme


@Composable
fun TreePackTab(
    packId: String,
    preview: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: (String)->Unit
) {
    var bitmapState = BitmapFactory.decodeStream(
        LocalContext.current.assets.open("tree_packs/${preview}")
    )

    if (bitmapState != null) {
        Image(
            bitmap = bitmapState.asImageBitmap(),
            contentDescription = "tree_tab_preview",
            contentScale = ContentScale.Fit,
            modifier = modifier
                .width(80.dp)
                .height(80.dp)
                .alpha(if (isSelected) 1f else 0.2f)
                .clickable {
                    onSelect(packId)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview34() {
    AwakeTheme {
        TreePackTab(
            packId = "",
            preview = "One/preview.png",
            isSelected = true,
        ) {}
    }
}