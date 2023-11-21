package ru.hse.miem.ros.ui.opengl.shape.texample

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.opengl.GLUtils
import android.util.Log
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.ceil

// This is a OpenGL ES 1.0 dynamic font rendering system. It loads actual font
// files, generates a font map (texture) from them, and allows rendering of
// text strings.
//
// NOTE: the rendering portions of this class uses a sprite batcher in order
// provide decent speed rendering. Also, rendering assumes a BOTTOM-LEFT
// origin, and the (x,y) positions are relative to that, as well as the
// bottom-left of the string to render.
class GLText(//--Members--//
    var gl // GL10 Instance
    : GL10, // Asset Manager
    private var assets: AssetManager
) {
    private val charWidths // Width of Each Character (Actual; Pixels)
            : FloatArray = FloatArray(CHAR_CNT)
    private var batch // Batch Renderer
            : SpriteBatch = SpriteBatch(gl, CHAR_BATCH_SIZE)
    private var fontPadX: Int
    private var fontPadY // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)
            : Int
    private var fontHeight // Font Height (Actual; Pixels)
            : Float
    private var fontAscent // Font Ascent (Above Baseline; Pixels)
            : Float
    private var fontDescent // Font Descent (Below Baseline; Pixels)
            : Float
    private var textureId // Font Texture ID [NOTE: Public for Testing Purposes Only!]
            : Int
    private var textureSize // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]
            : Int
    private var textureRgn: TextureRegion? = null // Full Texture Region
    private var charWidthMax // Character Width (Maximum; Pixels)
            : Float
    private var charHeight // Character Height (Maximum; Pixels)
            : Float
    private var charRgn // Region of Each Character (Texture Coordinates)
            : Array<TextureRegion?> = arrayOfNulls(CHAR_CNT)
    private var cellWidth: Int
    private var cellHeight // Character Cell Width/Height
            : Int
    private var rowCnt: Int
    private var colCnt // Number of Rows/Columns
            : Int// Return X Scale

    //--Get Scale--//
    // D: get the current scaling used for the font
    // A: [none]
    // R: the x/y scale currently used for scale
    private var scaleX: Float

    // Return Y Scale
    private var scaleY // Font Scale (X,Y Axis)
            : Float

    //--Set Space--//
    // D: set the spacing (unscaled; ie. pixel size) to use for the font
    // A: space - space for x axis spacing
    // R: [none]
    //--Get Space--//
    // D: get the current spacing used for the font
    // A: [none]
    // R: the x/y space currently used for scale
    private var space // Additional (X,Y Axis) Spacing (Unscaled)
            : Float

    //--Constructor--//
    // D: save GL instance + asset manager, create arrays, and initialize the members
    // A: gl - OpenGL ES 10 Instance
    init {
        // Save the GL10 Instance
        // Save the Asset Manager Instance
        // Create Sprite Batch (with Defined Size)
        // Create the Array of Character Widths
        // Create the Array of Character Regions

        // initialize remaining members
        fontPadX = 0
        fontPadY = 0
        fontHeight = 0.0f
        fontAscent = 0.0f
        fontDescent = 0.0f
        textureId = -1
        textureSize = 0
        charWidthMax = 0f
        charHeight = 0f
        cellWidth = 0
        cellHeight = 0
        rowCnt = 0
        colCnt = 0
        scaleX = 1.0f // Default Scale = 1 (Unscaled)
        scaleY = 1.0f // Default Scale = 1 (Unscaled)
        space = 0.0f
    }

    fun load(file: String?, size: Int, padX: Int, padY: Int): Boolean {
        val tf: Typeface =
            Typeface.createFromAsset(assets, file) // Create the Typeface from Font File
        return load(tf, size, padX, padY)
    }

    //--Load Font--//
    // description
    //    this will load the specified font file, create a texture for the defined
    //    character range, and setup all required values used to render with it.
    // arguments:
    //    file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
    //    size - Requested pixel size of font (height)
    //    padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.
    fun load(tf: Typeface?, size: Int, padX: Int, padY: Int): Boolean {

        // setup requested values
        fontPadX = padX // Set Requested X Axis Padding
        fontPadY = padY // Set Requested Y Axis Padding

        // setup paint instance for drawing
        val paint: Paint = Paint() // Create Android Paint Instance
        paint.isAntiAlias = true // Enable Anti Alias
        paint.textSize = size.toFloat() // Set Text Size
        paint.color = -0x1 // Set ARGB (White, Opaque)
        paint.setTypeface(tf) // Set Typeface

        // get font metrics
        val fm: Paint.FontMetrics = paint.fontMetrics // Get Font Metrics
        fontHeight = ceil((abs(fm.bottom.toDouble()) + abs(fm.top.toDouble())).toDouble())
            .toFloat() // Calculate Font Height
        fontAscent = ceil(abs(fm.ascent.toDouble())).toFloat() // Save Font Ascent
        fontDescent = ceil(abs(fm.descent.toDouble())).toFloat() // Save Font Descent

        // determine the width of each character (including unknown character)
        // also determine the maximum character width
        val s: CharArray = CharArray(2) // Create Character Array
        charHeight = 0f
        charWidthMax = charHeight // Reset Character Width/Height Maximums
        val w: FloatArray = FloatArray(2) // Working Width Value
        var cnt: Int = 0 // Array Counter
        for (c in CHAR_START..CHAR_END) {  // FOR Each Character
            s[0] = c.toChar() // Set Character
            paint.getTextWidths(s, 0, 1, w) // Get Character Bounds
            charWidths[cnt] = w[0] // Get Width
            if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
                charWidthMax = charWidths[cnt] // Save New Max Width
            cnt++ // Advance Array Counter
        }
        s[0] = CHAR_NONE.toChar() // Set Unknown Character
        paint.getTextWidths(s, 0, 1, w) // Get Character Bounds
        charWidths[cnt] = w[0] // Get Width
        if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
            charWidthMax = charWidths[cnt] // Save New Max Width
        cnt++ // Advance Array Counter

        // set character height to font height
        charHeight = fontHeight // Set Character Height

        // find the maximum size, validate, and setup cell sizes
        cellWidth = charWidthMax.toInt() + (2 * fontPadX) // Set Cell Width
        cellHeight = charHeight.toInt() + (2 * fontPadY) // Set Cell Height
        val maxSize: Int =
            if (cellWidth > cellHeight) cellWidth else cellHeight // Save Max Size (Width/Height)
        if (maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX) // IF Maximum Size Outside Valid Bounds
            return false // Return Error

        // set texture size based on max font size (width or height)
        // NOTE: these values are fixed, based on the defined characters. when
        // changing start/end characters (CHAR_START/CHAR_END) this will need adjustment too!
        textureSize = if (maxSize <= 24) // IF Max Size is 18 or Less
            256 // Set 256 Texture Size
        else if (maxSize <= 40) // ELSE IF Max Size is 40 or Less
            512 // Set 512 Texture Size
        else if (maxSize <= 80) // ELSE IF Max Size is 80 or Less
            1024 // Set 1024 Texture Size
        else  // ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
            2048 // Set 2048 Texture Size

        // create an empty bitmap (alpha only)
        val bitmap: Bitmap =
            Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8) // Create Bitmap
        val canvas: Canvas = Canvas(bitmap) // Create Canvas for Rendering to Bitmap
        bitmap.eraseColor(0x00000000) // Set Transparent Background (ARGB)

        // calculate rows/columns
        // NOTE: while not required for anything, these may be useful to have :)
        colCnt = textureSize / cellWidth // Calculate Number of Columns
        rowCnt = ceil((CHAR_CNT.toFloat() / colCnt.toFloat()).toDouble())
            .toInt() // Calculate Number of Rows

        // render each of the characters to the canvas (ie. build the font map)
        var x: Float = fontPadX.toFloat() // Set Start Position (X)
        var y: Float = (cellHeight - 1) - fontDescent - fontPadY // Set Start Position (Y)
        for (c in CHAR_START..CHAR_END) {  // FOR Each Character
            s[0] = c.toChar() // Set Character to Draw
            canvas.drawText(s, 0, 1, x, y, paint) // Draw Character
            x += cellWidth.toFloat() // Move to Next Character
            if ((x + cellWidth - fontPadX) > textureSize) {  // IF End of Line Reached
                x = fontPadX.toFloat() // Set X for New Row
                y += cellHeight.toFloat() // Move Down a Row
            }
        }
        s[0] = CHAR_NONE.toChar() // Set Character to Use for NONE
        canvas.drawText(s, 0, 1, x, y, paint) // Draw Character

        // generate a new texture
        val textureIds: IntArray = IntArray(1) // Array to Get Texture Id
        gl.glGenTextures(1, textureIds, 0) // Generate New Texture
        Log.i("text handle", "" + textureIds.get(0))
        textureId = textureIds.get(0) // Save Texture Id

        // setup filters for texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId) // Bind Texture
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MIN_FILTER,
            GL10.GL_NEAREST.toFloat()
        ) // Set Minification Filter
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MAG_FILTER,
            GL10.GL_LINEAR.toFloat()
        ) // Set Magnification Filter
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_S,
            GL10.GL_CLAMP_TO_EDGE.toFloat()
        ) // Set U Wrapping
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_CLAMP_TO_EDGE.toFloat()
        ) // Set V Wrapping

        // load the generated bitmap onto the texture
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0) // Load Bitmap to Texture
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0) // Unbind Texture

        // release the bitmap
        bitmap.recycle() // Release the Bitmap

        // setup the array of character texture regions
        x = 0f // Initialize X
        y = 0f // Initialize Y
        for (c in 0 until CHAR_CNT) {         // FOR Each Character (On Texture)
            charRgn[c] = TextureRegion(
                textureSize.toFloat(),
                textureSize.toFloat(),
                x,
                y,
                (cellWidth - 1).toFloat(),
                (cellHeight - 1).toFloat()
            ) // Create Region for Character
            x += cellWidth.toFloat() // Move to Next Char (Cell)
            if (x + cellWidth > textureSize) {
                x = 0f // Reset X Position to Start
                y += cellHeight.toFloat() // Move to Next Row (Cell)
            }
        }

        // create full texture region
        textureRgn = TextureRegion(
            textureSize.toFloat(),
            textureSize.toFloat(),
            0f,
            0f,
            textureSize.toFloat(),
            textureSize.toFloat()
        ) // Create Full Texture Region

        // return success
        return true // Return Success
    }

//    fun begin(alpha: Float) {
//        begin(1.0f, 1.0f, 1.0f, alpha) // Begin with White (Explicit Alpha)
//    }

    //--Begin/End Text Drawing--//
    // D: call these methods before/after (respectively all draw() calls using a text instance
    //    NOTE: color is set on a per-batch basis, and fonts should be 8-bit alpha only!!!
    // A: red, green, blue - RGB values for font (default = 1.0)
    //    alpha - optional alpha value for font (default = 1.0)
    // R: [none]
    @JvmOverloads
    fun begin(red: Float = 1.0f, green: Float = 1.0f, blue: Float = 1.0f, alpha: Float = 1.0f) {
        gl.glColor4f(red, green, blue, alpha) // Set Color+Alpha
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId) // Bind the Texture
        batch.beginBatch() // Begin Batch
    }

    fun end() {
        batch.endBatch() // End Batch
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0) // Bind the Texture
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f) // Restore Default Color/Alpha
    }

    //--Draw Text--//
    // D: draw text at the specified x,y position
    // A: text - the string to draw
    //    x, y - the x,y position to draw text at (bottom left of text; including descent)
    // R: [none]
    fun draw(text: String, x: Float, y: Float) {
        var x: Float = x
        var y: Float = y
        val chrHeight: Float = cellHeight * scaleY // Calculate Scaled Character Height
        val chrWidth: Float = cellWidth * scaleX // Calculate Scaled Character Width
        val len: Int = text.length // Get String Length
        x += (chrWidth / 2.0f) - (fontPadX * scaleX) // Adjust Start X
        y += (chrHeight / 2.0f) - (fontPadY * scaleY) // Adjust Start Y
        for (i in 0 until len) {              // FOR Each Character in String
            var c: Int =
                text[i].code - CHAR_START // Calculate Character Index (Offset by First Char in Font)
            if (c < 0 || c >= CHAR_CNT) // IF Character Not In Font
                c = CHAR_UNKNOWN // Set to Unknown Character Index
            batch.drawSprite(x, y, chrWidth, chrHeight, charRgn.get(c)) // Draw the Character
            x += (charWidths[c] + space) * scaleX // Advance X Position by Scaled Character Width
        }
    }

    //--Draw Text Centered--//
    // D: draw text CENTERED at the specified x,y position
    // A: text - the string to draw
    //    x, y - the x,y position to draw text at (bottom left of text)
    // R: the total width of the text that was drawn
    fun drawC(text: String, x: Float, y: Float): Float {
        val len: Float = getLength(text) // Get Text Length
        draw(text, x - (len / 2.0f), y - (getCharHeight() / 2.0f)) // Draw Text Centered
        return len // Return Length
    }

    fun drawCX(text: String, x: Float, y: Float): Float {
        val len: Float = getLength(text) // Get Text Length
        draw(text, x - (len / 2.0f), y) // Draw Text Centered (X-Axis Only)
        return len // Return Length
    }

    fun drawCY(text: String, x: Float, y: Float) {
        draw(text, x, y - (getCharHeight() / 2.0f)) // Draw Text Centered (Y-Axis Only)
    }

    //--Set Scale--//
    // D: set the scaling to use for the font
    // A: scale - uniform scale for both x and y axis scaling
    //    sx, sy - separate x and y axis scaling factors
    // R: [none]
    fun setScale(scale: Float) {
        scaleY = scale
        scaleX = scaleY // Set Uniform Scale
    }

    fun setScale(sx: Float, sy: Float) {
        scaleX = sx // Set X Scale
        scaleY = sy // Set Y Scale
    }

    //--Get Length of a String--//
    // D: return the length of the specified string if rendered using current settings
    // A: text - the string to get length for
    // R: the length of the specified string (pixels)
    fun getLength(text: String): Float {
        var len = 0.0f // Working Length
        val strLen: Int = text.length // Get String Length (Characters)
        for (i in 0 until strLen) {           // For Each Character in String (Except Last
            val c: Int =
                text[i].code - CHAR_START // Calculate Character Index (Offset by First Char in Font)
            len += (charWidths[c] * scaleX) // Add Scaled Character Width to Total Length
        }
        len += (if (strLen > 1) ((strLen - 1) * space) * scaleX else 0f) // Add Space Length
        return len // Return Total Length
    }

    //--Get Width/Height of Character--//
    // D: return the scaled width/height of a character, or max character width
    //    NOTE: since all characters are the same height, no character index is required!
    //    NOTE: excludes spacing!!
    // A: chr - the character to get width for
    // R: the requested character size (scaled)
    fun getCharWidth(chr: Char): Float {
        val c: Int =
            chr.code - CHAR_START // Calculate Character Index (Offset by First Char in Font)
        return (charWidths[c] * scaleX) // Return Scaled Character Width
    }

    fun getCharWidthMax(): Float {
        return (charWidthMax * scaleX) // Return Scaled Max Character Width
    }

    private fun getCharHeight(): Float {
        return (charHeight * scaleY) // Return Scaled Character Height
    }

    val ascent: Float
        //--Get Font Metrics--//
        get() {
            return (fontAscent * scaleY) // Return Font Ascent
        }
    val descent: Float
        get() {
            return (fontDescent * scaleY) // Return Font Descent
        }
    val height: Float
        get() {
            return (fontHeight * scaleY) // Return Font Height (Actual)
        }

    companion object {
        //--Constants--//
        val CHAR_START: Int = 32 // First Character (ASCII Code)
        val CHAR_END: Int = 126 // Last Character (ASCII Code)
        val CHAR_CNT: Int =
            (((CHAR_END - CHAR_START) + 1) + 1) // Character Count (Including Character to use for Unknown)
        val CHAR_UNKNOWN: Int = (CHAR_CNT - 1) // Index of the Unknown Character
        val CHAR_NONE: Int = 32 // Character to Use for Unknown (ASCII Code)
        val FONT_SIZE_MIN: Int = 6 // Minumum Font Size (Pixels)
        val FONT_SIZE_MAX: Int = 180 // Maximum Font Size (Pixels)
        val CHAR_BATCH_SIZE: Int = 100 // Number of Characters to Render Per Batch
    }
}