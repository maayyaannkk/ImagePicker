package in.mayanknagwanshi.imagepicker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import in.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener;
import in.mayanknagwanshi.imagepicker.imagePicker.ImagePicker;

public class ImageSelectActivity extends AppCompatActivity {
    private static final int EXTERNAL_PERMISSION_CODE = 1234;

    private ImagePicker imagePicker;

    private boolean isCompress = true, isCamera = true, isGallery = true;
    public static final String FLAG_COMPRESS = "flag_compress";
    public static final String FLAG_CAMERA = "flag_camera";
    public static final String FLAG_GALLERY = "flag_gallery";

    public static final String RESULT_FILE_PATH = "result_file_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_image_select);

        if (getIntent() != null) {
            isCompress = getIntent().getBooleanExtra(FLAG_COMPRESS, true);
            isCamera = getIntent().getBooleanExtra(FLAG_CAMERA, true);
            isGallery = getIntent().getBooleanExtra(FLAG_GALLERY, true);
        }

        imagePicker = new ImagePicker();
        if (checkPermission()) {
            //start image picker
            imagePicker.withActivity(this).chooseFromGallery(isGallery).chooseFromCamera(isCamera).withCompression(isCompress).start();
        } else {
            //ask permission
            requestStoragePermission();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return currentAPIVersion < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_PERMISSION_CODE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.withActivity(this).chooseFromGallery(isGallery).chooseFromCamera(isCamera).withCompression(isCompress).start();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                imagePicker.addOnCompressListener(new ImageCompressionListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompressed(String filePath) {
                        if (filePath != null && isCompress) {
                            //return filepath
                            Intent intent = new Intent();
                            intent.putExtra(RESULT_FILE_PATH, filePath);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
                String filePath = imagePicker.getImageFilePath(data);
                if (filePath != null && !isCompress) {
                    //return filepath
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_FILE_PATH, filePath);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
