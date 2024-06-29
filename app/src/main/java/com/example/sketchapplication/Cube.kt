package com.example.sketchapplication

import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube {
    private val vertexShaderCode =
        """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec4 aColor;
        varying vec4 vColor;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vColor = aColor;
        }
        """.trimIndent()

    private val fragmentShaderCode =
        """
        precision mediump float;
        varying vec4 vColor;
        void main() {
            gl_FragColor = vColor;
        }
        """.trimIndent()

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val drawListBuffer: ShortBuffer

    private val program: Int
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var mvpMatrixHandle: Int = 0

    private val cubeCoords = floatArrayOf(
        // Front face
        -0.5f, 0.5f, 0.5f, // top left
        -0.5f, -0.5f, 0.5f, // bottom left
        0.5f, -0.5f, 0.5f, // bottom right
        0.5f, 0.5f, 0.5f, // top right

        // Back face
        -0.5f, 0.5f, -0.5f, // top left
        -0.5f, -0.5f, -0.5f, // bottom left
        0.5f, -0.5f, -0.5f, // bottom right
        0.5f, 0.5f, -0.5f // top right
    )

    private val colors = floatArrayOf(
        // Front face (red)
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,
        1.0f, 0.0f, 0.0f, 1.0f,

        // Back face (green)
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f,
        0.0f, 1.0f, 0.0f, 1.0f
    )

    private val drawOrder = shortArrayOf(
        0, 1, 2, 0, 2, 3, // Front face
        4, 5, 6, 4, 6, 7, // Back face
        0, 4, 7, 0, 7, 3, // Top face
        1, 5, 6, 1, 6, 2, // Bottom face
        0, 1, 5, 0, 5, 4, // Left face
        3, 2, 6, 3, 6, 7  // Right face
    )

    init {
        // Initialize vertex buffer
        val bb = ByteBuffer.allocateDirect(cubeCoords.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer()
        vertexBuffer.put(cubeCoords)
        vertexBuffer.position(0)

        // Initialize color buffer
        val cb = ByteBuffer.allocateDirect(colors.size * 4)
        cb.order(ByteOrder.nativeOrder())
        colorBuffer = cb.asFloatBuffer()
        colorBuffer.put(colors)
        colorBuffer.position(0)

        // Initialize draw list buffer
        val dlb = ByteBuffer.allocateDirect(drawOrder.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        drawListBuffer = dlb.asShortBuffer()
        drawListBuffer.put(drawOrder)
        drawListBuffer.position(0)

        // Compile shaders and link program
        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // Get handle to vertex shader's vPosition and link with vertexBuffer
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(
            positionHandle, 3,
            GLES20.GL_FLOAT, false,
            12, vertexBuffer
        )

        // Get handle to fragment shader's vColor and link with colorBuffer
        colorHandle = GLES20.glGetAttribLocation(program, "aColor")
        GLES20.glEnableVertexAttribArray(colorHandle)
        GLES20.glVertexAttribPointer(
            colorHandle, 4,
            GLES20.GL_FLOAT, false,
            16, colorBuffer
        )

        // Get handle to shape's transformation matrix
        mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw the cube
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, drawOrder.size,
            GLES20.GL_UNSIGNED_SHORT, drawListBuffer
        )

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}







