package com.ajmorales.contactos

import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DetalleFotoActivity : AppCompatActivity() {

    private var miToolbar: Toolbar? = null

    companion object {
        var matrix = Matrix()
        var scale = 1f
        var SGD: ScaleGestureDetector? = null
        var imDetalleFoto: ImageView? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_foto)
        miToolbar = findViewById(R.id.toolbar_detalle_foto)
        miToolbar!!.setTitle("Detalle foto")
        setSupportActionBar(miToolbar)
        var miFoto = MainActivity.getImagen(intent.getIntExtra("miIndice2", 0))
        imDetalleFoto = findViewById(R.id.imDetalleFoto)
        imDetalleFoto!!.setImageBitmap(miFoto)
        SGD = ScaleGestureDetector(this, ScaleListener())
    }

    private class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scale = scale * detector.scaleFactor
            scale = Math.max(0.1f, Math.min(scale, 5f))
            matrix.setScale(scale, scale)
            imDetalleFoto?.setImageMatrix(matrix)
            return true
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        SGD!!.onTouchEvent(event)
        return true
    }
}
