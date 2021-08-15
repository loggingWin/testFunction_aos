package com.example.testfunction

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.testfunction.databinding.ActivityOcrBinding
import com.googlecode.tesseract.android.TessBaseAPI
import com.pedro.library.AutoPermissions
import com.pedro.library.AutoPermissionsListener
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class OcrActivity : AppCompatActivity(), AutoPermissionsListener {
    private val TAG = "@@TAG@@"

    private val binding: ActivityOcrBinding by lazy { ActivityOcrBinding.inflate(layoutInflater) }
    lateinit var tess: TessBaseAPI //Tesseract API 객체 생성
    var dataPath: String = "" //데이터 경로 변수 선언


    lateinit var mIntent: Intent

    // 이미지 관련
    val PERMISSION: Int = 200
    val CAMERA: Int = 100
    val GALLERY: Int = 101
    val STRING_INTENT_KEY = "300"
    var imagePath = ""

//    // activityResult를 받기 위한 callBack 등록
//    private lateinit var getResultText: ActivityResultLauncher<Intent>

    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // tess-two
        dataPath = "$filesDir/tesseract/" //언어데이터의 경로 미리 지정

        checkFile(File(dataPath + "tessdata/"), "kor") //사용할 언어파일의 이름 지정
        checkFile(File(dataPath + "tessdata/"), "eng")

        val lang: String = "kor+eng"
        tess = TessBaseAPI() //api 준비
        tess.init(dataPath, lang) //해당 사용할 언어데이터로 초기화
        // end: tess-two


//        // activityResult를 받기 위한 callBack 등록
//        getResultText = registerForActivityResult(
//            ActivityResultContracts.StartActivityForResult()
//        ) { result ->
//            if (result.resultCode == RESULT_OK) {
//                val mString = result.data?.getStringExtra(STRING_INTENT_KEY)
//                Log.d("@@TAG@@", "onCreate: $mString")
//            }
//        }


        // end: activityResult - callBack


        // 갤러리 버튼
        binding.btnAlbum.setOnClickListener {

            if (!hasPermission()) {
                AutoPermissions.loadAllPermissions(this, PERMISSION)
            } else {
                mIntent = Intent(Intent.ACTION_PICK)
                mIntent.type = MediaStore.Images.Media.CONTENT_TYPE
                mIntent.type = "image/*"
                startActivityForResult(mIntent, GALLERY)
            }
        }

        // 카메라 버튼
        binding.btnCamera.setOnClickListener {

            Log.d(TAG, "onCreate: 권한 유무 ${hasPermission()}")


            if (!hasPermission()) { // 권한이 없다면

                Log.d(TAG, "onCreate: 권한 없을 때 권한 요청")
                AutoPermissions.loadAllPermissions(this, PERMISSION)
            } else {
                Log.d(TAG, "onCreate: 권한있어서 카메라 인텐트 실행")
                mIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (mIntent.resolveActivity(packageManager) != null) {

                    val imageFile: File = createImageFile()

                    val imageUri: Uri = FileProvider.getUriForFile(
                        applicationContext,
                        "com.example.testfunction.fileprovider",
                        imageFile
                    )
                    mIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)


                    startActivityForResult(mIntent, CAMERA) // final int CAMERA = 100;
                }
            }

        }

        binding.btnImageGrayScale.setOnClickListener {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)

            val filter = ColorMatrixColorFilter(matrix)
            binding.sampleImage.colorFilter = filter
        }


        // OCR 동작 버튼
        binding.ocrStartButton.setOnClickListener {

            processImage(binding.sampleImage.drawable.toBitmap()) //이미지 가공후 텍스트뷰에 띄우기
        }

    }

    private fun createImageFile(): File { // 파일 생성하는 메소드
        // 이미지 파일 생성
        val imageDate = SimpleDateFormat("yyyyMMdd_HHmmss")
        val timeStamp = imageDate.format(Date()) // 파일명 중복을 피하기 위한 "yyyyMMdd_HHmmss"꼴의 timeStamp
        val fileName = "IMAGE_$timeStamp" // 이미지 파일 명
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(fileName, ".jpg", storageDir) // 이미지 파일 생성
        imagePath = file.absolutePath // 파일 절대경로 저장하기
        return file
        /*
        저장한 파일 위치: 내 PC\핸드폰명\Phone\Android\data\패키지명\files\Pictures
         */
    }

    private fun hasPermission(): Boolean { // 카메라 & 앨범 권한 확인
        val cameraPermission =
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val readPermission =
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        Log.d(
            TAG,
            "hasPermission: camera permission: ${cameraPermission == PackageManager.PERMISSION_GRANTED}"
        )
        Log.d(
            TAG,
            "hasPermission: write  permission: ${readPermission == PackageManager.PERMISSION_GRANTED}"
        )
//        var writePerm =
//                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        return ((cameraPermission == PackageManager.PERMISSION_GRANTED)
                &&
                (readPermission == PackageManager.PERMISSION_GRANTED)) // 카메라 권한이 승인된 상태일 경우

//        return ((checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
//                && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AutoPermissions.parsePermissions(
            this, requestCode, permissions as Array<String>, this
        )
    }

    /***
     *  언어 데이터 파일을 기기에 복사하는 기능
     *  @param lang: 언어 데이터 종류
     */
    private fun copyFile(lang: String) {
        try {
            //언어데이  타파일의 위치
            val filePath: String = "$dataPath/tessdata/$lang.traineddata"

            //AssetManager를 사용하기 위한 객체 생성
            val assetManager: AssetManager = assets;

            //byte 스트림을 읽기 쓰기용으로 열기
            val inputStream: InputStream = assetManager.open("tessdata/$lang.traineddata")
            val outStream: OutputStream = FileOutputStream(filePath)


            //위에 적어둔 파일 경로쪽으로 해당 바이트코드 파일을 복사한다.
            val buffer = ByteArray(1024)

            var read: Int = 0
            read = inputStream.read(buffer)
            while (read != -1) {
                outStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
            outStream.flush()
            outStream.close()
            inputStream.close()

        } catch (e: FileNotFoundException) {
            Log.v("오류발생", e.toString())
        } catch (e: IOException) {
            Log.v("오류발생", e.toString())
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) { // 결과가 있을 경우
            var bitmap: Bitmap? = null
            when (requestCode) {
                GALLERY -> {
                    imagePath = data!!.dataString!!
                    Log.d("TAG", "data.getDataString(): $imagePath")
                }

                CAMERA -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 2 // 이미지 축소 정도. 원 크기에서 1/inSampleSize 로 축소됨
                    bitmap = BitmapFactory.decodeFile(imagePath, options)
                }
            }
            Glide.with(this)
//                .load(bitmap)
                .load(imagePath)
                .into(binding.sampleImage)
//            binding.sampleImage.setImageBitmap(bitmap)
        }
    }

//    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) { // 결과가 있을 경우
//            var bitmap: Bitmap? = null
//            when (requestCode) {
//
//                CAMERA -> {
//                    val options = BitmapFactory.Options()
//                    options.inSampleSize = 2 // 이미지 축소 정도. 원 크기에서 1/inSampleSize 로 축소됨
//                    bitmap = BitmapFactory.decodeFile(imagePath, options)
//                }
//            }
//            imageView.setImageBitmap(bitmap)
//        }
//    }

    /***
     *  언어 데이터 파일이 기기에 존재하는지 체크하는 기능
     *  @param dir: 언어 데이터 파일 경로
     *  @param lang: 언어 종류 데이터 파일
     *
     *  -> 파일 없으면 파일 생성
     *  -> 있으면 언어 종류 데이터 파일 복사
     */
    private fun checkFile(dir: File, lang: String) {

        //파일의 존재여부 확인 후 내부로 복사
        if (!dir.exists() && dir.mkdirs()) {
            copyFile(lang)
        }

        if (dir.exists()) {
            val datafilePath: String = "$dataPath/tessdata/$lang.traineddata"
            val dataFile: File = File(datafilePath)
            if (!dataFile.exists()) {
                copyFile(lang)
            }
        }

    }

    /***
     *  이미지에서 텍스트 추출해서 결과뷰에 보여주는 기능
     *  @param bitmap: 이미지 비트맵
     */
    private fun processImage(bitmap: Bitmap) {
        Toast.makeText(applicationContext, "잠시 기다려 주세요", Toast.LENGTH_LONG).show()
        var ocrResult: String? = null;
        tess.setImage(bitmap)
        ocrResult = tess.utF8Text
        if (ocrResult.isNullOrEmpty())
            Toast.makeText(applicationContext, "no results", Toast.LENGTH_LONG).show()
        binding.ocrResultView.text = ocrResult
    }

    override fun onDenied(requestCode: Int, permissions: Array<String>) {
        showToast("onDenied: " + permissions.size)
    }

    override fun onGranted(requestCode: Int, permissions: Array<String>) {
        showToast("onGranted: " + permissions.size)
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }


//    //권한 확인
//    private fun checkPermission() {
//
//        // 1. 위험권한(Camera) 권한 승인상태 가져오기
//        val cameraPermission =
//            ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
//        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
//            // 카메라 권한이 승인된 상태일 경우
//            startProcess()
//
//        } else {
//            // 카메라 권한이 승인되지 않았을 경우
//            requestPermission()
//        }
//    }
//
//    // 2. 권한 요청
//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA)
//    }
//
//
//    // 3. 카메라 기능 실행
//    @SuppressLint("QueryPermissionsNeeded")
//    private fun startProcess() {
//        Toast.makeText(this, "카메라 기능 실행", Toast.LENGTH_SHORT).show()
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (intent.resolveActivity(packageManager) != null) {
//            var imageFile: File? = null
//            try {
//                imageFile = createImageFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            if (imageFile != null) {
//                val imageUri: Uri = FileProvider.getUriForFile(
//                    applicationContext,
//                    "com.example.getimage.fileprovider",
//                    imageFile
//                )
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
//
//
//                startActivityForResult(intent, CAMERA) // final int CAMERA = 100;
//            }
//        }
//    }
}