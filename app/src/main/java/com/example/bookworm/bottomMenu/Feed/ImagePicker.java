package com.example.bookworm.bottomMenu.Feed;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import com.example.bookworm.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

public class ImagePicker extends AppCompatActivity {
    private static final String TAG = subActivity_Feed_Create.class.getSimpleName();
    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    public static final String INTENT_ASPECT_RATIO_X = "aspect_ratio_x";
    public static final String INTENT_ASPECT_RATIO_Y = "aspect_ratio_Y";
    public static final String INTENT_LOCK_ASPECT_RATIO = "lock_aspect_ratio";
    public static final String INTENT_IMAGE_COMPRESSION_QUALITY = "compression_quality";
    public static final String INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT = "set_bitmap_max_width_height";
    public static final String INTENT_BITMAP_MAX_WIDTH = "max_width";
    public static final String INTENT_BITMAP_MAX_HEIGHT = "max_height";
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_GALLERY_IMAGE = 1;
    private int ASPECT_RATIO_X = 4, ASPECT_RATIO_Y = 3, bitmapMaxWidth = 1920, bitmapMaxHeight = 1280;
    private int IMAGE_COMPRESSION = 80;
    public static String fileName;

    //activityResult 처리
    //촬영한 사진의 결과물을 처리
    ActivityResultLauncher<Intent> captureResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = getCacheImagePath(fileName); //촬영한 사진이 캐시된 경로의 URI를 가져옴
                        cropImage(imageUri); //URI를 통해 사진 편집
                    } else {
                        setResultCancelled();
                    }
                }
            });
    ActivityResultLauncher<Intent> galleryResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData(); //전달받은 이미지의 경로를 통해 사진을 전달받음
                        cropImage(imageUri); //전달받은 사진의 URI로 사진 편집
                    } else {
                        setResultCancelled();
                    }
                }
            });
    ActivityResultLauncher<Intent> cropResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                {

                    if (result.getResultCode() == RESULT_OK) {
                        handleUCropResult(result.getData()); //잘려진 사진을 처리
                    } else {
                        setResultCancelled();
                    }

                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        processingImage();
    }

    //Methods
    private void processingImage() {
        Intent intent = getIntent();
        ASPECT_RATIO_X = intent.getIntExtra(INTENT_ASPECT_RATIO_X, ASPECT_RATIO_X);
        ASPECT_RATIO_Y = intent.getIntExtra(INTENT_ASPECT_RATIO_Y, ASPECT_RATIO_Y);
        IMAGE_COMPRESSION = intent.getIntExtra(INTENT_IMAGE_COMPRESSION_QUALITY, IMAGE_COMPRESSION);
        bitmapMaxWidth = intent.getIntExtra(INTENT_BITMAP_MAX_WIDTH, bitmapMaxWidth);
        bitmapMaxHeight = intent.getIntExtra(INTENT_BITMAP_MAX_HEIGHT, bitmapMaxHeight);
        int requestCode = intent.getIntExtra(INTENT_IMAGE_PICKER_OPTION, -1);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            takeCameraImage();
        } else {
            chooseImageFromGallery();
        }
    }

    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseGallerySelected();
    }

    private void takeCameraImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg"; //시간을 가져옴으로서 난수 생성
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                captureResult.launch(takePictureIntent);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void chooseImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryResult.launch(pickPhoto);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    //이미지를 어떻게 가져올지 사용자에게 묻는 메소드
    public static void showImagePickerOptions(Context context, PickerOptionListener listener) {
        //Alert Dialog를 통해 사용자와 대화

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo));

        //Alert Dialog에 띄울 선택지 목록 작성
        String[] animals = {context.getString(R.string.lbl_take_camera_picture), context.getString(R.string.lbl_choose_from_gallery)};
        //Alert Dialog에 아이템 세팅
        builder.setItems(animals, (dialog, which) -> {
            switch (which) {
                case 0:
                    listener.onTakeCameraSelected();
                    break;
                case 1:
                    listener.onChooseGallerySelected();
                    break;
            }
        });

        //Dialog를 생성하고 보여줌
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //실제로 이미지를 자르는 메소드
    private void cropImage(Uri sourceUri) {
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), queryName(getContentResolver(), sourceUri)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(IMAGE_COMPRESSION);
        options.withAspectRatio(ASPECT_RATIO_X, ASPECT_RATIO_Y);
        options.withMaxResultSize(bitmapMaxWidth, bitmapMaxHeight);
        options.setToolbarColor(ContextCompat.getColor(this, R.color.maincolor));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.maincolor));
        options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.select)); //컨트롤 위젯(크기와 회전)
        options.setToolbarTitle("\t사진 편집"); //타이틀 제목




        Intent intent = UCrop.of(sourceUri, destinationUri).withOptions(options).getIntent(this);
        cropResult.launch(intent);

    }
    //이미지가 캐시된 경로를 반환하는 메소드
    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(ImagePicker.this, getPackageName() + ".provider", image);
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private void handleUCropResult(Intent data) {
        if (data == null) {
            setResultCancelled();
            return;
        }
        final Uri resultUri = UCrop.getOutput(data);
        setResultOk(resultUri);
    }

    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        intent.putExtra("path", imagePath);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}