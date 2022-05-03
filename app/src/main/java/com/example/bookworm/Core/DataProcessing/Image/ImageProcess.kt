//package com.example.bookworm.Core.DataProcessing.Image
//
//import android.Manifest
//import android.R
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Context
//import android.content.DialogInterface
//import android.content.Intent
//import android.graphics.drawable.Drawable
//import android.net.Uri
//import android.provider.MediaStore
//import android.provider.Settings
//import android.util.Log
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.ActivityResultCallback
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
//import androidx.core.content.ContextCompat
//import com.bumptech.glide.Glide
//import com.bumptech.glide.request.target.Target
//import com.example.bookworm.BottomMenu.Feed.ImagePicker
//import com.example.bookworm.BottomMenu.Feed.ImagePicker.PickerOptionListener
//import com.karumi.dexter.Dexter
//import com.karumi.dexter.MultiplePermissionsReport
//import com.karumi.dexter.PermissionToken
//import com.karumi.dexter.listener.PermissionRequest
//import com.karumi.dexter.listener.multi.MultiplePermissionsListener
//import java.io.IOException
//
//class ImageProcess(val context:Context){
//
//    //라벨은 알럿 다이어그램을 통해 입력을 받고, 선택한 값으로 라벨이 지정됨 => 구현 예정
//    var startActivityResult: ActivityResultLauncher<Intent> =
//        registerForActivityResult<Intent, ActivityResult>(
//            StartActivityForResult(),
//            ActivityResultCallback { result: ActivityResult ->
//                val code = result.resultCode
//                if (code == Activity.RESULT_OK) {
//                    val uri =
//                        result.data!!.getParcelableExtra<Uri>("path")
//                    try {
//                        // You can update this bitmap to your server
//                        uploaded = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri)
//
//                        // loading profile image from local cache
//                        loadImage(uri.toString())
//                    } catch (e: IOException) {
//                        e.printStackTrace()
//                    }
//                }
//            })
//
//
//    fun checkPermission(){
//        Dexter.withActivity(context as Activity?)
//            .withPermissions(
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//            .withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    if (report.areAllPermissionsGranted()) {
//                        showImagePickerOptions()
//                    }
//                    if (report.isAnyPermissionPermanentlyDenied) {
//                        showSettingsDialog()
//                    }
//                }
//
//                override fun onPermissionRationaleShouldBeShown(
//                    permissions: List<PermissionRequest>,
//                    token: PermissionToken
//                ) {
//                    token.continuePermissionRequest()
//                }
//            }).check()
//    }
//
//
//    private fun loadImage(url: String) {
//        Log.d("이미지 캐싱 ", "Image cache path: $url")
//        Glide.with(this).load(url)
//            .into<Target<Drawable>>(binding.ivpicture)
//        binding.ivpicture.setColorFilter(ContextCompat.getColor(this, R.color.transparent))
//    }
//
//    private fun showImagePickerOptions() {
//        ImagePicker.showImagePickerOptions(this, object : PickerOptionListener {
//            override fun onTakeCameraSelected() {
//                launchCameraIntent()
//            }
//
//            override fun onChooseGallerySelected() {
//                launchGalleryIntent()
//            }
//        })
//    }
//
//    private fun launchCameraIntent() {
//        val intent = Intent(this@subActivity_Feed_Create, ImagePicker::class.java)
//        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_IMAGE_CAPTURE)
//
//        // setting aspect ratio
//        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
//        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
//        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1)
//
//        // setting maximum bitmap width and height
//        intent.putExtra(ImagePicker.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true)
//        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_WIDTH, 1000)
//        intent.putExtra(ImagePicker.INTENT_BITMAP_MAX_HEIGHT, 1000)
//        startActivityResult.launch(intent)
//    }
//
//    private fun launchGalleryIntent() {
//        val intent = Intent(this@subActivity_Feed_Create, ImagePicker::class.java)
//        intent.putExtra(ImagePicker.INTENT_IMAGE_PICKER_OPTION, ImagePicker.REQUEST_GALLERY_IMAGE)
//
//        // setting aspect ratio
//        intent.putExtra(ImagePicker.INTENT_LOCK_ASPECT_RATIO, true)
//        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_X, 1) // 16x9, 1x1, 3:4, 3:2
//        intent.putExtra(ImagePicker.INTENT_ASPECT_RATIO_Y, 1)
//        startActivityResult.launch(intent)
//    }
//
//    private fun showSettingsDialog() {
//        val builder = AlertDialog.Builder(this@subActivity_Feed_Create)
//        builder.setTitle(getString(R.string.dialog_permission_title))
//        builder.setMessage(getString(R.string.dialog_permission_message))
//        builder.setPositiveButton(
//            getString(R.string.go_to_settings),
//            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
//                dialog.cancel()
//                openSettings()
//            })
//        builder.setNegativeButton(getString(R.string.cancel),
//            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.cancel() })
//        builder.show()
//    }
//
//    //권한 설정
//    private fun openSettings() {
//        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//        val uri = Uri.fromParts("package", getPackageName(), null)
//        intent.data = uri
//        startActivityResult.launch(intent)
//    }
//}