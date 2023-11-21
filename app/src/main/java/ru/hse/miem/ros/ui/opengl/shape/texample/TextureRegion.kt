package ru.hse.miem.ros.ui.opengl.shape.texample

class TextureRegion(
    texWidth: Float,
    texHeight: Float,
    x: Float,
    y: Float,
    width: Float,
    height: Float
) {
    //--Members--//
    var u1: Float
    var v1 // Top/Left U,V Coordinates
            : Float
    var u2: Float
    var v2 // Bottom/Right U,V Coordinates
            : Float

    //--Constructor--//
    // D: calculate U,V coordinates from specified texture coordinates
    // A: texWidth, texHeight - the width and height of the texture the region is for
    //    x, y - the top/left (x,y) of the region on the texture (in pixels)
    //    width, height - the width and height of the region on the texture (in pixels)
    init {
        u1 = x / texWidth // Calculate U1
        v1 = y / texHeight // Calculate V1
        u2 = u1 + (width / texWidth) // Calculate U2
        v2 = v1 + (height / texHeight) // Calculate V2
    }
}