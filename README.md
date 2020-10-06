# ImagePicker ![Downloads](https://jitpack.io/v/maayyaannkk/ImagePicker/month.svg)
Android library to choose image from gallery or camera with option to compress result image.

# Download [![](https://jitpack.io/v/maayyaannkk/ImagePicker.svg)](https://jitpack.io/#maayyaannkk/ImagePicker) [![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-Image%20Picker%20and%20compression-green.svg?style=flat )]( https://android-arsenal.com/details/1/7055 )

Add this to your project's `build.gradle`

```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

And add this to your module's `build.gradle` 

```groovy
dependencies {
	implementation 'com.github.maayyaannkk:ImagePicker:x.y.z'
}
```

change `x.y.z` to version in [![](https://jitpack.io/v/maayyaannkk/ImagePicker.svg)](https://jitpack.io/#maayyaannkk/ImagePicker)

## Usage

For full example, please refer to `app` module

No need to request for write external storage permission, library will do that.
### Crop with 1:1 aspect ratio
<img src="https://github.com/maayyaannkk/ImagePicker/blob/master/sample.gif" width="300" />

### Start image picker activity

The simplest way to start is setup options and start the activity. Set the FLAG_CROP to crop resulting image in 1:1 aspect ratio
```java
Intent intent = new Intent(this, ImageSelectActivity.class);
intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, false);//default is true
intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
intent.putExtra(ImageSelectActivity.FLAG_GALLERY, true);//default is true
intent.putExtra(ImageSelectActivity.FLAG_CROP, isCrop);//default is false
startActivityForResult(intent, 1213);
```
Receive result
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(selectedImage);
        }
    }
```
## Corner cases
throws IllegalStateException if:  
  -chooseFromCamera and chooseFromGallery both are false
