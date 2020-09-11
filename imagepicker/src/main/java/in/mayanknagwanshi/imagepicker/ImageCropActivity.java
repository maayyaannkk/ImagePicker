package in.mayanknagwanshi.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import in.mayanknagwanshi.imagepicker.view.ImageCropView;

public class ImageCropActivity extends AppCompatActivity {
    private ImageCropView imageCropView;
    private Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        imageCropView = findViewById(R.id.imageCropView);
        buttonContinue = findViewById(R.id.buttonContinue);
        if (getIntent() != null && getIntent().getStringExtra(EXTRA_FILE_PATH) != null) {
            Bitmap selectedImage = BitmapFactory.decodeFile(getIntent().getStringExtra(EXTRA_FILE_PATH));
            imageCropView.setImageBitmap(selectedImage);
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageCropView.setImageBitmap(imageCropView.getCroppedBitmap());
                }
            });
        }
    }

    public static final String EXTRA_FILE_PATH = "extra_file_path";

    public static void startActivity(Activity activity, String filePath) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        activity.startActivity(intent);
        activity.finish();
    }
}