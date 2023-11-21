package ru.hse.miem.ros.ui.opengl.shape.texample

import javax.microedition.khronos.opengles.GL10

class SpriteBatch(//--Members--//
    var gl // GL Instance
    : GL10, // Maximum Sprites Allowed in Buffer
    private var maxSprites: Int
) {
    var vertices // Vertices Instance Used for Rendering
            : Vertices = Vertices(
                gl,
                maxSprites * VERTICES_PER_SPRITE,
                maxSprites * INDICES_PER_SPRITE,
                false,
                true,
                false
            )
    private var vertexBuffer // Vertex Buffer
            : FloatArray = FloatArray(maxSprites * VERTICES_PER_SPRITE * VERTEX_SIZE)
    private var bufferIndex // Vertex Buffer Start Index
            : Int = 0
    private var numSprites // Number of Sprites Currently in Buffer
            : Int = 0

    //--Constructor--//
    // D: prepare the sprite batcher for specified maximum number of sprites
    // A: gl - the gl instance to use for rendering
    //    maxSprites - the maximum allowed sprites per batch
    init {
        // Save GL Instance
        // Create Vertex Buffer
        // Create Rendering Vertices
        // Reset Buffer Index
        // Save Maximum Sprites
        // Clear Sprite Counter
        val indices: ShortArray =
            ShortArray(maxSprites * INDICES_PER_SPRITE) // Create Temp Index Buffer
        val len: Int = indices.size // Get Index Buffer Length
        var j: Short = 0 // Counter
        var i: Int = 0
        while (i < len) {
            // FOR Each Index Set (Per Sprite)
            indices[i + 0] = (j + 0).toShort() // Calculate Index 0
            indices[i + 1] = (j + 1).toShort() // Calculate Index 1
            indices[i + 2] = (j + 2).toShort() // Calculate Index 2
            indices[i + 3] = (j + 2).toShort() // Calculate Index 3
            indices[i + 4] = (j + 3).toShort() // Calculate Index 4
            indices[i + 5] = (j + 0).toShort() // Calculate Index 5
            i += INDICES_PER_SPRITE
            j = (j + VERTICES_PER_SPRITE).toShort()
        }
        vertices.setIndices(indices, 0, len) // Set Index Buffer for Rendering
    }

    //--Begin Batch--//
    // D: signal the start of a batch. set the texture and clear buffer
    //    NOTE: the overloaded (non-texture) version assumes that the texture is already bound!
    // R: [none]
    fun beginBatch() {
        numSprites = 0 // Empty Sprite Counter
        bufferIndex = 0 // Reset Buffer Index (Empty)
    }

    //--End Batch--//
    // D: signal the end of a batch. render the batched sprites
    // A: [none]
    // R: [none]
    fun endBatch() {
        if (numSprites > 0) {                        // IF Any Sprites to Render
            vertices.setVertices(vertexBuffer, 0, bufferIndex) // Set Vertices from Buffer
            vertices.bind() // Bind Vertices
            vertices.draw(
                GL10.GL_TRIANGLES,
                0,
                numSprites * INDICES_PER_SPRITE
            ) // Render Batched Sprites
            vertices.unbind() // Unbind Vertices
        }
    }

    //--Draw Sprite to Batch--//
    // D: batch specified sprite to batch. adds vertices for sprite to vertex buffer
    //    NOTE: MUST be called after beginBatch(), and before endBatch()!
    //    NOTE: if the batch overflows, this will render the current batch, restart it,
    //          and then batch this sprite.
    // A: x, y - the x,y position of the sprite (center)
    //    width, height - the width and height of the sprite
    //    region - the texture region to use for sprite
    // R: [none]
    fun drawSprite(x: Float, y: Float, width: Float, height: Float, region: TextureRegion?) {
        if (numSprites == maxSprites) {              // IF Sprite Buffer is Full
            endBatch() // End Batch
            // NOTE: leave current texture bound!!
            numSprites = 0 // Empty Sprite Counter
            bufferIndex = 0 // Reset Buffer Index (Empty)
        }
        val halfWidth: Float = width / 2.0f // Calculate Half Width
        val halfHeight: Float = height / 2.0f // Calculate Half Height
        val x1: Float = x - halfWidth // Calculate Left X
        val y1: Float = y - halfHeight // Calculate Bottom Y
        val x2: Float = x + halfWidth // Calculate Right X
        val y2: Float = y + halfHeight // Calculate Top Y
        vertexBuffer[bufferIndex++] = x1 // Add X for Vertex 0
        vertexBuffer[bufferIndex++] = y1 // Add Y for Vertex 0
        vertexBuffer[bufferIndex++] = region!!.u1 // Add U for Vertex 0
        vertexBuffer[bufferIndex++] = region.v2 // Add V for Vertex 0
        vertexBuffer[bufferIndex++] = x2 // Add X for Vertex 1
        vertexBuffer[bufferIndex++] = y1 // Add Y for Vertex 1
        vertexBuffer[bufferIndex++] = region.u2 // Add U for Vertex 1
        vertexBuffer[bufferIndex++] = region.v2 // Add V for Vertex 1
        vertexBuffer[bufferIndex++] = x2 // Add X for Vertex 2
        vertexBuffer[bufferIndex++] = y2 // Add Y for Vertex 2
        vertexBuffer[bufferIndex++] = region.u2 // Add U for Vertex 2
        vertexBuffer[bufferIndex++] = region.v1 // Add V for Vertex 2
        vertexBuffer[bufferIndex++] = x1 // Add X for Vertex 3
        vertexBuffer[bufferIndex++] = y2 // Add Y for Vertex 3
        vertexBuffer[bufferIndex++] = region.u1 // Add U for Vertex 3
        vertexBuffer[bufferIndex++] = region.v1 // Add V for Vertex 3
        numSprites++ // Increment Sprite Count
    }

    companion object {
        //--Constants--//
        val VERTEX_SIZE: Int = 4 // Vertex Size (in Components) ie. (X,Y,U,V)
        val VERTICES_PER_SPRITE: Int = 4 // Vertices Per Sprite
        val INDICES_PER_SPRITE: Int = 6 // Indices Per Sprite
    }
}