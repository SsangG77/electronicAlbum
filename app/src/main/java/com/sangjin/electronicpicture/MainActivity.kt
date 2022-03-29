package com.sangjin.electronicpicture

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat


//권한이 있는지 없는지에 따라 권한 요청을 한다.
//권한을 요청한 결과가 승인이면 사진을 선택하는 창을 띄우고 결과가 취소이면 토스트 메세지를 띄운다. override fun onRequestPermissionsResult()
//창을 띄우기 위한 메소드를 생성한다.
//띄운 창에서 사진들을 선택하면 그 정보들을 다시 현재 액티비티로 가져와서 뷰들에 사진들을 띄운다.
//그리고 가져온 사진들을 다른 액티비티를 생성해서 그 액티비티로 정보들을 넘긴다.


class MainActivity : AppCompatActivity() {

    private val addPhotoButton by lazy {
        findViewById<Button>(R.id.addButton)
    }
    private val runPhotoButton by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val ImageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.firstfirst))
            add(findViewById(R.id.firstsecond))
            add(findViewById(R.id.firstthird))
            add(findViewById(R.id.secondfirst))
            add(findViewById(R.id.secondsecond))
            add(findViewById(R.id.secondthird))
        }
    }
    private val photoList = mutableListOf<Uri>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPhotoButton()
        initRunPhoto()
    }

    private fun initPhotoButton() {
        addPhotoButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    findGetPhoto()
                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPopup()
                }
                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
        }
    }

    private fun initRunPhoto() {
        runPhotoButton.setOnClickListener {
            val intent = Intent(this, photoFrame::class.java)
            val size = photoList.size
            photoList.forEachIndexed { index, uri -> //
                intent.putExtra("photo$index", uri.toString())
            }
            intent.putExtra("photoListSize", size)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findGetPhoto()
                } else {
                    Toast.makeText(this, "사진을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {}
        }
    }

    private fun findGetPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            2000 -> { //
                //사진선택창에서 선택한 사진을 이미지뷰에 띄워줘야함.
                val seletedImage: Uri? = data?.data

                if (photoList.size == 6) {
                    Toast.makeText(this, "사진을 더이상 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                if (seletedImage != null) {
                    photoList.add(seletedImage)
                    ImageViewList[photoList.size - 1].setImageURI(seletedImage)
                } else {
                    Toast.makeText(this, "사진을 추가하지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPopup() {
        AlertDialog.Builder(this)
            .setTitle("확인")
            .setMessage("사진 선택을 위해 권한이 필요합니다.")
            .setPositiveButton("확인") {_, _ ->
                requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
            .setNegativeButton("취소") {_, _ -> }
            .create()
            .show()
    }
}