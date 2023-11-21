package ru.hse.miem.ros.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * TODO: Description
 *
 * @author Maxim Kolpakov
 * @version 1.0.0
 * @created on 14.04.21
 */
class CharacterWrapTextView : AppCompatTextView {
    constructor(context: Context) : super((context))
    constructor(context: Context, attrs: AttributeSet?) : super((context), attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        (context), attrs, defStyleAttr
    )

    public override fun setText(text: CharSequence, type: BufferType) {
        super.setText(text, type)
        getWrappedText(text.toString())
    }

    private fun getWrappedText(input: String): String {
        val maxEms: Int = maxLines
        var output: String = ""
        val parts: List<String> =
            listOf(*input.split("/".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
            )
        parts.drop(1)
        var currentLine: String = ""
        var row: Int = 0
        for (part: String in parts) {
            if (currentLine.length + ("/$part").length > maxEms) {
                output += (if (row == 0) "" else "\n") + currentLine
                row++
                currentLine = ""
            } else {
                currentLine += "/$part"
            }
        }
        return output
    }
}