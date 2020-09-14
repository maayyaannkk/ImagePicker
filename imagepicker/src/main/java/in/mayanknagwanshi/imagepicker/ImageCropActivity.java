package in.mayanknagwanshi.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import in.mayanknagwanshi.imagepicker.view.ImageCropView;

import static in.mayanknagwanshi.imagepicker.ImageSelectActivity.RESULT_FILE_PATH;

public class ImageCropActivity extends AppCompatActivity {
    private ImageCropView imageCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        imageCropView = findViewById(R.id.imageCropView);
        Button buttonContinue = findViewById(R.id.buttonContinue);

        if (getIntent() != null && getIntent().getStringExtra(EXTRA_FILE_PATH) != null) {
            Bitmap selectedImage = BitmapFactory.decodeFile(getIntent().getStringExtra(EXTRA_FILE_PATH));
            imageCropView.setImageBitmap(selectedImage);
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(RESULT_FILE_PATH, getCroppedPath(getIntent().getStringExtra(EXTRA_FILE_PATH), imageCropView.getCroppedGrid()));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    private String getCroppedPath(String filePath, ImageCropView.CroppedCoordinate croppedCoordinate) {
        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
        Bitmap scaledBitmap = Bitmap.createBitmap(selectedImage, croppedCoordinate.getX(), croppedCoordinate.getY(), croppedCoordinate.getSide(), croppedCoordinate.getSide());
        try {
            FileOutputStream out = new FileOutputStream(filePath);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    public static final String EXTRA_FILE_PATH = "extra_file_path";

    public static void startActivity(Activity activity, String filePath) {
        Intent intent = new Intent(activity, ImageCropActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, filePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        activity.startActivity(intent);
        activity.finish();
    }
}