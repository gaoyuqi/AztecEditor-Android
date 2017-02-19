/*
 * Copyright (C) 2016 Automattic
 * Copyright (C) 2015 Matthew Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wordpress.aztec.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.text.Spanned
import android.text.TextUtils
import android.text.style.LineBackgroundSpan
import android.text.style.LineHeightSpan
import android.text.style.QuoteSpan
import android.text.style.UpdateLayout
import org.wordpress.aztec.formatting.BlockFormatter


class AztecQuoteSpan : QuoteSpan, LineBackgroundSpan, AztecBlockSpan, LineHeightSpan, UpdateLayout {

    val rect = Rect()

    private val TAG: String = "blockquote"

    private var verticalPadding: Int = 0
    private var quoteBackground: Int = 0
    private var quoteColor: Int = 0
    private var quoteMargin: Int = 0
    private var quotePadding: Int = 0
    private var quoteWidth: Int = 0
    private var quoteBackgroundAlpha: Float = 0.0f

    override var attributes: String = ""


    constructor(attributes: String = "") : super() {
        this.attributes = attributes
    }

    constructor(quoteStyle: BlockFormatter.QuoteStyle, attributes: String = "") : this(attributes) {
        setStyle(quoteStyle)
    }

    fun setStyle(quoteStyle: BlockFormatter.QuoteStyle) {
        this.verticalPadding = quoteStyle.verticalPadding
        this.quoteBackground = quoteStyle.quoteBackground
        this.quoteColor = quoteStyle.quoteColor
        this.quoteMargin = quoteStyle.quoteMargin
        this.quoteWidth = quoteStyle.quoteWidth
        this.quotePadding = quoteStyle.quotePadding
        this.quoteBackgroundAlpha = quoteStyle.quoteBackgroundAlpha
    }

    override fun chooseHeight(text: CharSequence, start: Int, end: Int, spanstartv: Int, v: Int, fm: Paint.FontMetricsInt) {
        val spanned = text as Spanned
        val spanStart = spanned.getSpanStart(this)
        val spanEnd = spanned.getSpanEnd(this)

        if (start === spanStart || start < spanStart) {
            fm.ascent -= verticalPadding
            fm.top -= verticalPadding
        }
        if (end === spanEnd || spanEnd < end) {
            fm.descent += verticalPadding
            fm.bottom += verticalPadding
        }
    }

    override fun getStartTag(): String {
        if (TextUtils.isEmpty(attributes)) {
            return TAG
        }
        return TAG + attributes
    }

    override fun getEndTag(): String {
        return TAG
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return quoteMargin + quoteWidth + quotePadding
    }

    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                   top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int,
                                   first: Boolean, layout: Layout) {
        val style = p.style
        val color = p.color

        p.style = Paint.Style.FILL
        p.color = quoteColor
        c.drawRect(x.toFloat() + quoteMargin, top.toFloat(), (x + quoteMargin + dir * quoteWidth).toFloat(), bottom.toFloat(), p)

        p.style = style
        p.color = color
    }

    override fun drawBackground(c: Canvas, p: Paint, left: Int, right: Int,
                                top: Int, baseline: Int, bottom: Int,
                                text: CharSequence?, start: Int, end: Int,
                                lnum: Int) {
        val alpha: Int = (quoteBackgroundAlpha * 255).toInt()

        val paintColor = p.color
        p.color = Color.argb(alpha, Color.red(quoteBackground), Color.green(quoteBackground), Color.blue(quoteBackground))
        rect.set(left + quoteMargin, top, right, bottom)

        c.drawRect(rect, p)
        p.color = paintColor
    }
}
