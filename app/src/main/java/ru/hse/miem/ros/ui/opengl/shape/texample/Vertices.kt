package ru.hse.miem.ros.ui.opengl.shape.texample

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10

class Vertices @JvmOverloads constructor(//--Members--//
    // NOTE: all members are constant, and initialized in constructor!
    val gl // GL Instance
    : GL10, maxVertices: Int, maxIndices: Int, // Use Color in Vertices
    private val hasColor: Boolean, // Use Texture Coords in Vertices
    private val hasTexCoords: Boolean, // Use Normals in Vertices
    private val hasNormals: Boolean, use3D: Boolean = false
) {
    private val positionCnt // Number of Position Components (2=2D, 3=3D)
            : Int
    private val vertexStride // Vertex Stride (Element Size of a Single Vertex)
            : Int
    private val vertexSize // Bytesize of a Single Vertex
            : Int
    val vertices // Vertex Buffer
            : IntBuffer
    var indices: ShortBuffer? = null // Index Buffer
    private val tmpBuffer // Temp Buffer for Vertex Conversion
            : IntArray
    private var numVertices // Number of Vertices in Buffer
            : Int
    private var numIndices // Number of Indices in Buffer
            : Int

    //--Constructor--//
    // D: create the vertices/indices as specified (for 2d/3d)
    // A: gl - the gl instance to use
    //    maxVertices - maximum vertices allowed in buffer
    //    maxIndices - maximum indices allowed in buffer
    //    hasColor - use color values in vertices
    //    hasTexCoords - use texture coordinates in vertices
    //    hasNormals - use normals in vertices
    //    use3D - (false, default) use 2d positions (ie. x/y only)
    //            (true) use 3d positions (ie. x/y/z)
    init {
        // Save GL Instance
        // Save Color Flag
        // Save Texture Coords Flag
        // Save Normals Flag
        positionCnt =
            if (use3D) POSITION_CNT_3D else POSITION_CNT_2D // Set Position Component Count
        vertexStride =
            positionCnt + (if (hasColor) COLOR_CNT else 0) + (if (hasTexCoords) TEXCOORD_CNT else 0) + (if (hasNormals) NORMAL_CNT else 0) // Calculate Vertex Stride
        vertexSize = vertexStride * 4 // Calculate Vertex Byte Size
        var buffer: ByteBuffer =
            ByteBuffer.allocateDirect(maxVertices * vertexSize) // Allocate Buffer for Vertices (Max)
        buffer.order(ByteOrder.nativeOrder()) // Set Native Byte Order
        vertices = buffer.asIntBuffer() // Save Vertex Buffer
        if (maxIndices > 0) {                        // IF Indices Required
            buffer =
                ByteBuffer.allocateDirect(maxIndices * INDEX_SIZE) // Allocate Buffer for Indices (MAX)
            buffer.order(ByteOrder.nativeOrder()) // Set Native Byte Order
            this.indices = buffer.asShortBuffer() // Save Index Buffer
        } else  // ELSE Indices Not Required
            indices = null // No Index Buffer
        numVertices = 0 // Zero Vertices in Buffer
        numIndices = 0 // Zero Indices in Buffer
        tmpBuffer = IntArray(maxVertices * vertexSize / 4) // Create Temp Buffer
    }

    //--Set Vertices--//
    // D: set the specified vertices in the vertex buffer
    //    NOTE: optimized to use integer buffer!
    // A: vertices - array of vertices (floats) to set
    //    offset - offset to first vertex in array
    //    length - number of floats in the vertex array (total)
    //             for easy setting use: vtx_cnt * (this.vertexSize / 4)
    // R: [none]
    fun setVertices(vertices: FloatArray, offset: Int, length: Int) {
        this.vertices.clear() // Remove Existing Vertices
        val last: Int = offset + length // Calculate Last Element
        var i: Int = offset
        var j: Int = 0
        while (i < last) {
            // FOR Each Specified Vertex
            tmpBuffer[j] = java.lang.Float.floatToRawIntBits(vertices[i]) // Set Vertex as Raw Integer Bits in Buffer
            i++
            j++
        }
        this.vertices.put(tmpBuffer, 0, length) // Set New Vertices
        this.vertices.flip() // Flip Vertex Buffer
        numVertices = length / vertexStride // Save Number of Vertices
        //this.numVertices = length / ( this.vertexSize / 4 );  // Save Number of Vertices
    }

    //--Set Indices--//
    // D: set the specified indices in the index buffer
    // A: indices - array of indices (shorts) to set
    //    offset - offset to first index in array
    //    length - number of indices in array (from offset)
    // R: [none]
    fun setIndices(indices: ShortArray?, offset: Int, length: Int) {
        this.indices?.clear() // Clear Existing Indices
        this.indices?.put(indices, offset, length) // Set New Indices
        this.indices?.flip() // Flip Index Buffer
        numIndices = length // Save Number of Indices
    }

    //--Bind--//
    // D: perform all required binding/state changes before rendering batches.
    //    USAGE: call once before calling draw() multiple times for this buffer.
    // A: [none]
    // R: [none]
    fun bind() {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) // Enable Position in Vertices
        vertices.position(0) // Set Vertex Buffer to Position
        gl.glVertexPointer(positionCnt, GL10.GL_FLOAT, vertexSize, vertices) // Set Vertex Pointer
        if (hasColor) {                              // IF Vertices Have Color
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY) // Enable Color in Vertices
            vertices.position(positionCnt) // Set Vertex Buffer to Color
            gl.glColorPointer(COLOR_CNT, GL10.GL_FLOAT, vertexSize, vertices) // Set Color Pointer
        }
        if (hasTexCoords) {                          // IF Vertices Have Texture Coords
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) // Enable Texture Coords in Vertices
            vertices.position(positionCnt + (if (hasColor) COLOR_CNT else 0)) // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
            gl.glTexCoordPointer(
                TEXCOORD_CNT,
                GL10.GL_FLOAT,
                vertexSize,
                vertices
            ) // Set Texture Coords Pointer
        }
        if (hasNormals) {
            gl.glEnableClientState(GL10.GL_NORMAL_ARRAY) // Enable Normals in Vertices
            vertices.position(positionCnt + (if (hasColor) COLOR_CNT else 0) + (if (hasTexCoords) TEXCOORD_CNT else 0)) // Set Vertex Buffer to Normals (NOTE: position based on whether color/texcoords is also specified)
            gl.glNormalPointer(GL10.GL_FLOAT, vertexSize, vertices) // Set Normals Pointer
        }
    }

    //--Draw--//
    // D: draw the currently bound vertices in the vertex/index buffers
    //    USAGE: can only be called after calling bind() for this buffer.
    // A: primitiveType - the type of primitive to draw
    //    offset - the offset in the vertex/index buffer to start at
    //    numVertices - the number of vertices (indices) to draw
    // R: [none]
    fun draw(primitiveType: Int, offset: Int, numVertices: Int) {
        if (indices != null) {                       // IF Indices Exist
            indices?.position(offset) // Set Index Buffer to Specified Offset
            gl.glDrawElements(
                primitiveType,
                numVertices,
                GL10.GL_UNSIGNED_SHORT,
                indices
            ) // Draw Indexed
        } else {                                         // ELSE No Indices Exist
            gl.glDrawArrays(primitiveType, offset, numVertices) // Draw Direct (Array)
        }
    }

    //--Unbind--//
    // D: clear binding states when done rendering batches.
    //    USAGE: call once before calling draw() multiple times for this buffer.
    // A: [none]
    // R: [none]
    fun unbind() {
        if (hasColor) // IF Vertices Have Color
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY) // Clear Color State
        if (hasTexCoords) // IF Vertices Have Texture Coords
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY) // Clear Texture Coords State
        if (hasNormals) // IF Vertices Have Normals
            gl.glDisableClientState(GL10.GL_NORMAL_ARRAY) // Clear Normals State
    }

    //--Draw Full--//
    // D: draw the vertices in the vertex/index buffers
    //    NOTE: unoptimized version! use bind()/draw()/unbind() for batches
    // A: primitiveType - the type of primitive to draw
    //    offset - the offset in the vertex/index buffer to start at
    //    numVertices - the number of vertices (indices) to draw
    // R: [none]
    fun drawFull(primitiveType: Int, offset: Int, numVertices: Int) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY) // Enable Position in Vertices
        vertices.position(0) // Set Vertex Buffer to Position
        gl.glVertexPointer(positionCnt, GL10.GL_FLOAT, vertexSize, vertices) // Set Vertex Pointer
        if (hasColor) {                              // IF Vertices Have Color
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY) // Enable Color in Vertices
            vertices.position(positionCnt) // Set Vertex Buffer to Color
            gl.glColorPointer(COLOR_CNT, GL10.GL_FLOAT, vertexSize, vertices) // Set Color Pointer
        }
        if (hasTexCoords) {                          // IF Vertices Have Texture Coords
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY) // Enable Texture Coords in Vertices
            vertices.position(positionCnt + (if (hasColor) COLOR_CNT else 0)) // Set Vertex Buffer to Texture Coords (NOTE: position based on whether color is also specified)
            gl.glTexCoordPointer(
                TEXCOORD_CNT,
                GL10.GL_FLOAT,
                vertexSize,
                vertices
            ) // Set Texture Coords Pointer
        }
        if (indices != null) {                       // IF Indices Exist
            indices?.position(offset) // Set Index Buffer to Specified Offset
            gl.glDrawElements(
                primitiveType,
                numVertices,
                GL10.GL_UNSIGNED_SHORT,
                indices
            ) // Draw Indexed
        } else {                                         // ELSE No Indices Exist
            gl.glDrawArrays(primitiveType, offset, numVertices) // Draw Direct (Array)
        }
        if (hasTexCoords) // IF Vertices Have Texture Coords
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY) // Clear Texture Coords State
        if (hasColor) // IF Vertices Have Color
            gl.glDisableClientState(GL10.GL_COLOR_ARRAY) // Clear Color State
    }

    //--Set Vertex Elements--//
    // D: use these methods to alter the values (position, color, textcoords, normals) for vertices
    //    WARNING: these do NOT validate any values, ensure that the index AND specified
    //             elements EXIST before using!!
    // A: x, y, z - the x,y,z position to set in buffer
    //    r, g, b, a - the r,g,b,a color to set in buffer
    //    u, v - the u,v texture coords to set in buffer
    //    nx, ny, nz - the x,y,z normal to set in buffer
    // R: [none]
    fun setVtxPosition(vtxIdx: Int, x: Float, y: Float) {
        val index: Int = vtxIdx * vertexStride // Calculate Actual Index
        vertices.put(index + 0, java.lang.Float.floatToRawIntBits(x)) // Set X
        vertices.put(index + 1, java.lang.Float.floatToRawIntBits(y)) // Set Y
    }

    fun setVtxPosition(vtxIdx: Int, x: Float, y: Float, z: Float) {
        val index: Int = vtxIdx * vertexStride // Calculate Actual Index
        vertices.put(index + 0, java.lang.Float.floatToRawIntBits(x)) // Set X
        vertices.put(index + 1, java.lang.Float.floatToRawIntBits(y)) // Set Y
        vertices.put(index + 2, java.lang.Float.floatToRawIntBits(z)) // Set Z
    }

    fun setVtxColor(vtxIdx: Int, r: Float, g: Float, b: Float, a: Float) {
        val index: Int = (vtxIdx * vertexStride) + positionCnt // Calculate Actual Index
        vertices.put(index + 0, java.lang.Float.floatToRawIntBits(r)) // Set Red
        vertices.put(index + 1, java.lang.Float.floatToRawIntBits(g)) // Set Green
        vertices.put(index + 2, java.lang.Float.floatToRawIntBits(b)) // Set Blue
        vertices.put(index + 3, java.lang.Float.floatToRawIntBits(a)) // Set Alpha
    }

    fun setVtxColor(vtxIdx: Int, r: Float, g: Float, b: Float) {
        val index: Int = (vtxIdx * vertexStride) + positionCnt // Calculate Actual Index
        vertices.put(index + 0, java.lang.Float.floatToRawIntBits(r)) // Set Red
        vertices.put(index + 1, java.lang.Float.floatToRawIntBits(g)) // Set Green
        vertices.put(index + 2, java.lang.Float.floatToRawIntBits(b)) // Set Blue
    }

    fun setVtxColor(vtxIdx: Int, a: Float) {
        val index: Int = (vtxIdx * vertexStride) + positionCnt // Calculate Actual Index
        vertices.put(index + 3, java.lang.Float.floatToRawIntBits(a)) // Set Alpha
    }

    fun setVtxTexCoords(vtxIdx: Int, u: Float, v: Float) {
        val index: Int =
            (vtxIdx * vertexStride) + positionCnt + (if (hasColor) COLOR_CNT else 0) // Calculate Actual Index
        vertices.put(index + 0, java.lang.Float.floatToRawIntBits(u)) // Set U
        vertices.put(index + 1, java.lang.Float.floatToRawIntBits(v)) // Set V
    }

    fun setVtxNormal(vtxIdx: Int, x: Float, y: Float, z: Float) {
        val index: Int =
            (vtxIdx * vertexStride) + positionCnt + (if (hasColor) COLOR_CNT else 0) + (if (hasTexCoords) TEXCOORD_CNT else 0) // Calculate Actual Index
        vertices.put(index + 0, java.lang.Float.floatToRawIntBits(x)) // Set X
        vertices.put(index + 1, java.lang.Float.floatToRawIntBits(y)) // Set Y
        vertices.put(index + 2, java.lang.Float.floatToRawIntBits(z)) // Set Z
    }

    companion object {
        //--Constants--//
        val POSITION_CNT_2D: Int = 2 // Number of Components in Vertex Position for 2D
        val POSITION_CNT_3D: Int = 3 // Number of Components in Vertex Position for 3D
        val COLOR_CNT: Int = 4 // Number of Components in Vertex Color
        val TEXCOORD_CNT: Int = 2 // Number of Components in Vertex Texture Coords
        val NORMAL_CNT: Int = 3 // Number of Components in Vertex Normal
        val INDEX_SIZE: Int = java.lang.Short.SIZE / 8 // Index Byte Size (Short.SIZE = bits)
    }
}