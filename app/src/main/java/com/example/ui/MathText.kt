package com.example.ui

import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import android.view.View

@Composable
fun MathText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color.Black,
    textSizeSP: Float = 16f
) {
    val layoutDirection = LocalLayoutDirection.current
    AndroidView(
        modifier = modifier,
        factory = { context ->
            try {
                ru.noties.jlatexmath.JLatexMathAndroid.init(context.applicationContext)
            } catch (e: Exception) {
                // Ignore if it doesn't exist
            }
            TextView(context).apply {
                textSize = textSizeSP
                setTextColor(textColor.toArgb())
                textDirection = if (layoutDirection == LayoutDirection.Rtl) View.TEXT_DIRECTION_RTL else View.TEXT_DIRECTION_LTR
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { textView ->
            val markwon = Markwon.builder(textView.context)
                .usePlugin(io.noties.markwon.inlineparser.MarkwonInlineParserPlugin.create())
                .usePlugin(JLatexMathPlugin.create(textView.textSize) { builder ->
                    builder.inlinesEnabled(true)
                })
                .build()
            markwon.setMarkdown(textView, text)
        }
    )
}
