package com.sangjin.electronicpicture

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlin.concurrent.timer


class photoFrame : AppCompatActivity() {

    val firstImageView: ImageView by lazy {
        findViewById(R.id.firstImageView)
    }

    val nextImageView: ImageView by lazy {
        findViewById(R.id.nextImageView)
    }

    val photoList = mutableListOf<Uri>()
    var currentPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_frame)

        autoChange()
    }

    private fun autoChange() {
        //가져온 사진을 리스트에 넣고 리스트의 순서대로 화면에 애니메이션효과로 나타나게 한다.
        val size = intent.getIntExtra("photoListSize", 0) //
        for (i in 0..size) { //
           intent.getStringExtra("photo$i")?.let {
               photoList.add(Uri.parse(it))
           }
        }
        timer(period = 5000) {
            runOnUiThread {
                var current = currentPosition
                val next = if(photoList.size <= currentPosition + 1) 0 else currentPosition + 1
                firstImageView.setImageURI(photoList[current])
                nextImageView.setImageURI(photoList[next])
                nextImageView.alpha = 0f
                nextImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next
            }
        }
    }
}