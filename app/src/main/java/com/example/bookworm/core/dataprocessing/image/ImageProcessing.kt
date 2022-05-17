package com.example.bookworm.core.dataprocessing.image


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.example.bookworm.R
import com.example.bookworm.bottomMenu.Feed.subActivity_Feed_Create
import com.example.bookworm.core.internet.interfaces.GetDataInterface
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


//이미지를 처리하는 클래스

class ImageProcessing(val context: Context) {
    var startActivityResult: ActivityResultLauncher<Intent>;
    val bitmap: MutableLiveData<Bitmap> = MutableLiveData()
    val bitmapUri: MutableLiveData<Uri> = MutableLiveData()
    val imgData:MutableLiveData<String> = MutableLiveData()
    //라벨은 알럿 다이어그램을 통해 입력을 받고, 선택한 값으로 라벨이 지정됨 => 구현 예정


    init {
        startActivityResult = (context as AppCompatActivity)
            .registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                result.let {
                    val code = it.resultCode
                    if (code == Activity.RESULT_OK) {
                        val uri =
                            result.data!!.getParcelableExtra<Uri>("path")
                        try {
                            // You can update this bitmap to your server
                            uri?.let {
                                if (Build.VERSION.SDK_INT < 28) {
                                    bitmap.value = MediaStore.Images.Media.getBitmap(
                                        context.contentResolver,
                                        uri
                                    )
                                    bitmapUri.value = uri
                                } else {
                                    val source =
                                        ImageDecoder.createSource(context.contentResolver, uri)
                                    bitmapUri.value = uri
                                    bitmap.value = ImageDecoder.decodeBitmap(source)
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    fun initProcess() {
        //권한이 설정되었는지 확인 후, 사진을 불러오거나 찍는다.
        Dexter.withActivity(context as Activity?)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showImagePickerOptions()
                    }
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    private fun showImagePickerOptions() {
        ImagePicker.showImagePickerOptions(context, object : ImagePicker.PickerOptionListener {
            override fun onTakeCameraSelected() {
                launchCameraIntent()
            }

            override fun onChooseGallerySelected() {
                launchGalleryIntent()
            }
        })
    }

    private fun launchCameraIntent() {
        val intent = Intent(context, ImagePicker::class.java)
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_IMAGE_CAPTURE)

        // setting aspect ratio
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1)

        // setting maximum bitmap width and height
        intent.putExtra(ImagePicker.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_WIDTH, 1000)
        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_HEIGHT, 1000)
        startActivityResult.launch(intent)
    }

    private fun launchGalleryIntent() {
        val intent = Intent(context, ImagePicker::class.java)
        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE)

        // setting aspect ratio
        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1)
        startActivityResult.launch(intent)
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.dialog_permission_title))
        builder.setMessage(context.getString(R.string.dialog_permission_message))
        builder.setPositiveButton(
            context.getString(R.string.go_to_settings),
            { dialog: DialogInterface, which: Int ->
                dialog.cancel()
                openSettings()
            })
        builder.setNegativeButton(
            context.getString(R.string.cancel),
            { dialog: DialogInterface, which: Int -> dialog.cancel() })
        builder.show()
    }

    //권한 설정
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", context.packageName, null)
        intent.data = uri
        startActivityResult.launch(intent)
    }

    fun uploadImage(data: Bitmap,fileName:String){
        val filesDir: File = context.getFilesDir()
        val file = File(filesDir, "$fileName") //파일명 설정
        val bos = ByteArrayOutputStream()
        data.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bitmapdata = bos.toByteArray()
        //파일에 바이트배열로 담겨진 비트맵파일을 쓴다.
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("upload", file.name, reqFile)
        val name = RequestBody.create(MediaType.parse("text/plain"), "upload")
        var query: MutableMap<String,Any> = HashMap()
        query["rqbody"] = body
        query["rqname"] = name
        var imgurl = context.getString(R.string.serverUrl) //이미지 서버의 주소
//        var module = Module(context, imgurl, map)
//        module.connect(3)
        CoroutineScope(Dispatchers.IO).launch {
            var retrofit = Retrofit.Builder()
                .baseUrl(imgurl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
            var mainInterface = retrofit.create(GetDataInterface::class.java)
            val name = query.get("rqname") as RequestBody
            val body = query.get("rqbody") as MultipartBody.Part
            val response = mainInterface.postprofileImage(body, name)
            CoroutineScope(Dispatchers.Main).launch {
                if(response!!.isSuccessful){
                    Log.d("경로",response.body().toString());
                    Log.d("업로드","업로드 성공")
                    imgData.value=imgurl + response.body()
                }else{
                    Log.e("업로드","업로드 실패")
                }
            }
        }
    }
}