# ImagePicker 
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

### Start image picker activity

The simplest way to start is create 'ImagePicker' class object

```java
ImagePicker imagePicker = new ImagePicker();
```
Setup options and start the activity
```java
imagePicker.withActivity(this) //calling from activity
.withFragment(this) //calling from fragment
.chooseFromGallery(false) //default is true
.chooseFromCamera(false) //default is true
.withCompression(false) //default is true
.start();
```
Receive result
```java
    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            //Add compression listener if withCompression is set to true
            imagePicker.addOnCompressListener(new ImageCompressionListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompressed(String filePath) {//filePath of the compressed image
                    //convert to bitmap easily
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                }
            });
        }
        //call the method 'getImageFilePath(Intent data)' even if compression is set to false
        String filePath = imagePicker.getImageFilePath(data);
        if (filePath != null) {//filePath will return null if compression is set to true
            Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
```
## Corner cases
throws IllegalStateException if:

  -activity and fragment both are null
  
  -chooseFromCamera and chooseFromGallery both are false
  
  -write external file permission not found i.e request write external permission before calling 'start()' method
