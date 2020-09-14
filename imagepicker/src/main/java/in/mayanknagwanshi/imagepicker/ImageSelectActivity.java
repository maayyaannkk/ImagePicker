package in.mayanknagwanshi.imagepicker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import in.mayanknagwanshi.imagepicker.imageCompression.ImageCompression;
import in.mayanknagwanshi.imagepicker.imageCompression.ImageCompressionListener;
import in.mayanknagwanshi.imagepicker.imagePicker.ImagePickerUtil;

public class ImageSelectActivity extends AppCompatActivity {
    private static final int EXTERNAL_PERMISSION_CODE = 1234;
    public static final int SELECT_IMAGE = 121;

    private ProgressBar progressBar;
    private TextView textViewCamera;
    private TextView textViewGallery;
    private TextView textViewCancel;

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

        progressBar = findViewById(R.id.progressBar);
        textViewCamera = findViewById(R.id.textViewCamera);
        textViewGallery = findViewById(R.id.textViewGallery);
        textViewCancel = findViewById(R.id.textViewCancel);

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        textViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    toggleProgress(true);
                    startActivityForResult(ImagePickerUtil.getPickImageChooserIntent(ImageSelectActivity.this, true, false), SELECT_IMAGE);
                } else {
                    requestStoragePermission();
                }
            }
        });
        textViewGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    toggleProgress(true);
                    startActivityForResult(ImagePickerUtil.getPickImageChooserIntent(ImageSelectActivity.this, false, true), SELECT_IMAGE);
                } else {
                    requestStoragePermission();
                }
            }
        });

        if (getIntent() != null) {
            isCompress = getIntent().getBooleanExtra(FLAG_COMPRESS, true);
            isCamera = getIntent().getBooleanExtra(FLAG_CAMERA, true);
            isGallery = getIntent().getBooleanExtra(FLAG_GALLERY, true);
        }

        if (isCamera && isGallery) toggleProgress(false);
        else toggleProgress(true);

        if (checkPermission() && (!isCamera || !isGallery)) {
            //start image picker
            startActivityForResult(ImagePickerUtil.getPickImageChooserIntent(ImageSelectActivity.this, isCompress, isGallery), SELECT_IMAGE);
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
                if ((!isCamera || !isGallery))
                    startActivityForResult(ImagePickerUtil.getPickImageChooserIntent(ImageSelectActivity.this, isCompress, isGallery), SELECT_IMAGE);
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                String filePath = ImagePickerUtil.getImageFilePath(getApplicationContext(), data);
                if (filePath != null && !isCompress) {
                    //return filepath
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_FILE_PATH, filePath);
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }

                new ImageCompression(getApplicationContext(), filePath, new ImageCompressionListener() {
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
                }).execute();
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    private void toggleProgress(boolean showProgress) {
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.GONE);
        textViewCamera.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        textViewGallery.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        textViewCancel.setVisibility(showProgress ? View.GONE : View.VISIBLE);
    }

    //region init
    public static void startImageSelectionForResult(Activity activity, boolean isCamera, boolean isGallery, boolean isCompress, int requestCode) {
        Intent intent = new Intent(activity, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, isCamera);
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, isGallery);
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, isCompress);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startImageSelectionForResult(Fragment fragment, boolean isCamera, boolean isGallery, boolean isCompress, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, isCamera);
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, isGallery);
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, isCompress);
        fragment.startActivityForResult(intent, requestCode);
    }
    //endregion
}
