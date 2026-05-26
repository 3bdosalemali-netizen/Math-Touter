package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun testMathText() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    ru.noties.jlatexmath.JLatexMathAndroid.init(context)
    val view = android.widget.TextView(context)
    val markwon = io.noties.markwon.Markwon.builder(context)
        .usePlugin(io.noties.markwon.inlineparser.MarkwonInlineParserPlugin.create())
        .usePlugin(io.noties.markwon.ext.latex.JLatexMathPlugin.create(40f) { builder ->
            builder.inlinesEnabled(true)
        })
        .build()
    markwon.setMarkdown(view, "Here is math \$x^2\$")
    println(view.text)
  }
}
