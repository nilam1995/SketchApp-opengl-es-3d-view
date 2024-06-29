package com.example.sketchapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.example.sketchapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isRectangleMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFreehand.setOnClickListener {
            binding.drawingView.toggleRectangleMode(false)
            binding.drawingView.toggleEraserMode(false)
            binding.drawingView.changeColor(Color.BLACK)
            binding.drawingView.visibility = View.VISIBLE
            binding.my3DView.visibility = View.GONE
        }

        binding.btnRectangle.setOnClickListener {
            binding.drawingView.toggleRectangleMode(true)
            binding.drawingView.toggleEraserMode(false)
            binding.drawingView.visibility = View.VISIBLE
            binding.my3DView.visibility = View.GONE
        }

        binding.btnEraser.setOnClickListener {
            binding.drawingView.toggleRectangleMode(false)
            binding.drawingView.toggleEraserMode(true)
            binding.drawingView.updateEraserSize(100f)

        }

        binding.btn3DView.setOnClickListener {
            binding.drawingView.visibility = View.GONE
            binding.my3DView.visibility = View.VISIBLE
//            binding.my3DView.requestRender()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isRectangleMode) {
            // Implement logic for drawing rectangles
            // ...
        }
        return super.onTouchEvent(event)
    }
}