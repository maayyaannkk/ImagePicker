package in.mayanknagwanshi.imagepicker.imagePicker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import in.mayanknagwanshi.imagepicker.provider.ImageSelectionProvider;

public class ImagePickerUtil {
    static String filePath;

    public static Intent getPickImageChooserIntent(Context context, boolean isCamera, boolean isGallery) {
        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri(context);

        List<Intent> allIntents = new ArrayList<>();
        //PackageManager packageManager = context.getPackageManager();

        if (isCamera) {
            // collect all camera intents
            Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            if (outputFileUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(captureIntent);
        }

        if (isGallery) {
            // collect all gallery intents
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
            allIntents.add(galleryIntent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent() != null && intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private static Uri getCaptureImageOutputUri(Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalFilesDir("");
        if (getImage != null) {
            //outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
            String fileName = "IMG_" + System.currentTimeMillis() + ".png";
            filePath = new File(getImage.getPath(), fileName).getPath();
            outputFileUri = ImageSelectionProvider.getUriForFile(context,
                    context.getPackageName() + ".image-selection-provider",
                    new File(getImage.getPath(), fileName));
        }
        return outputFileUri;
    }

    public static String getImageFilePath(Context context, Intent data) {
        return getPickImageResultFilePath(context, data);
    }

    private static String getPickImageResultFilePath(Context context, Intent data) {
        boolean isCamera = data == null || data.getData() == null;
        //Log.e("data", +"");
        /*if (data != null) {
            isCamera = false;
            String action = data.getAction();
            isCamera = action != null && action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        } else {
            isCamera = true;
        }*/

        //Log.e("isCamera", isCamera ? "true" : "false");
        if (isCamera) return filePath;
        else return getRealPathFromURI(context, data.getData());
        //return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) {
        /*String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = activity != null ?
                activity.getContentResolver().query(contentUri, proj, null, null, null) : fragment.getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);*/

        OutputStream out;
        File file = new File(getFilename(context));

        try {
            if (file.createNewFile()) {
                InputStream iStream = context.getContentResolver().openInputStream(contentUri);
                byte[] inputData = getBytes(iStream);
                out = new FileOutputStream(file);
                out.write(inputData);
                out.close();
                return file.getAbsolutePath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private static String getFilename(Context context) {
        File mediaStorageDir = new File(context.getExternalFilesDir(""), "uncompressed");

        //File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Compressed");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String mImageName = "IMG_" + String.valueOf(System.currentTimeMillis()) + ".png";
        return mediaStorageDir.getAbsolutePath() + "/" + mImageName;

    }
}
