package in.mayanknagwanshi.imagepicker.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import in.mayanknagwanshi.imagepicker.R;

public class ImageCropView extends AppCompatImageView {
    private Paint paint;
    private Bitmap bitmapGrid;
    private int maxHeight = 0, maxWidth = 0;
    private Rect rectCropGrid;
    private int sideLengthRect = 0;
    private int downTouchX = 0, downTouchY = 0;
    private int downTouchToMoveX = 0, downTouchToMoveY = 0;
    private int rectLeft = 0, rectTop = 0;
    private boolean isImageSet = false;

    public ImageCropView(@NonNull Context context) {
        this(context, null);
    }

    public ImageCropView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageCropView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmapGrid = BitmapFactory.decodeResource(getResources(), R.drawable.frame);
        paint = new Paint();
        paint.setAlpha(255);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getDrawable() == null) return;
        if (!isImageSet) {
            initCalc();
            isImageSet = true;
        }
        drawCropper(canvas);
    }

    private void drawCropper(Canvas canvas) {
        if (sideLengthRect == 0)
            sideLengthRect = Math.min(maxWidth, maxHeight) / 2;

        if (rectCropGrid == null)
            rectCropGrid = new Rect(rectLeft, rectTop, sideLengthRect, sideLengthRect);

        canvas.drawBitmap(bitmapGrid, null, rectCropGrid, paint);
    }

    private void initCalc() {
        /*int viewHeight = getMeasuredHeight();//height of imageView
        int viewWidth = getMeasuredWidth();//width of imageView

        maxHeight = getDrawable().getIntrinsicHeight();//original height of underlying image
        maxWidth = getDrawable().getIntrinsicWidth();//original width of underlying image

        maxWidth = maxWidth * viewHeight / maxHeight;
        maxHeight = maxHeight * viewWidth / maxWidth;
        if (viewHeight / maxHeight <= viewWidth / maxWidth) {
            viewWidth = maxWidth * viewHeight / maxHeight;//rescaled width of image within ImageView
        } else {
            viewHeight = maxHeight * viewWidth / maxWidth;//rescaled height of image within ImageView
        }*/

        maxHeight = getMeasuredHeight();
        maxWidth = getMeasuredWidth();

        if (getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth() > maxHeight / maxWidth) {
            //image view width greater than bitmap width
            maxWidth = (int) ((maxHeight * 1.0 / getDrawable().getIntrinsicHeight()) * getDrawable().getIntrinsicWidth());
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        isImageSet = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (rectCropGrid == null) return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int eventX = (int) event.getX();
                int eventY = (int) event.getY();
                if (getCornerPaddedRect(rectCropGrid.right, rectCropGrid.bottom).contains(eventX, eventY)) {
                    downTouchToMoveX = eventX;
                    downTouchToMoveY = eventY;
                } else if (rectCropGrid.contains(eventX, eventY)) {
                    downTouchX = eventX;
                    downTouchY = eventY;
                } else {
                    downTouchX = 0;
                    downTouchY = 0;
                    downTouchToMoveX = 0;
                    downTouchToMoveY = 0;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (downTouchToMoveX != 0 && downTouchToMoveY != 0) {
                    int moveX = (int) event.getX();
                    int moveY = (int) event.getY();
                    int displacementX = moveX - downTouchToMoveX;
                    int displacementY = moveY - downTouchToMoveY;
                    int displacement = Math.max(displacementX, displacementY);
                    if (rectLeft + displacement + sideLengthRect <= maxWidth && rectTop + displacement + sideLengthRect <= maxHeight &&
                            sideLengthRect + displacement < Math.min(maxWidth, maxHeight) && sideLengthRect + displacement > Math.min(maxWidth, maxHeight) / 4) {
                        sideLengthRect += displacement;
                        rectCropGrid.set(rectLeft, rectTop, sideLengthRect + rectLeft, sideLengthRect + rectTop);
                    }
                    downTouchToMoveX = (int) event.getX();
                    downTouchToMoveY = (int) event.getY();
                    invalidate();
                }
                if (downTouchX == 0 && downTouchY == 0) break;
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();
                int displacementX = moveX - downTouchX;
                int displacementY = moveY - downTouchY;
                if ((rectLeft + displacementX + sideLengthRect <= maxWidth && rectLeft + displacementX >= 0) && (rectTop + displacementY + sideLengthRect <= maxHeight && rectTop + displacementY >= 0)) {
                    rectTop += displacementY;
                    rectLeft += displacementX;
                    rectCropGrid.set(rectLeft, rectTop, sideLengthRect + rectLeft, sideLengthRect + rectTop);
                    downTouchX = (int) event.getX();
                    downTouchY = (int) event.getY();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                downTouchX = 0;
                downTouchY = 0;
                downTouchToMoveX = 0;
                downTouchToMoveY = 0;
                break;
        }
        return true;
    }

    private Rect getCornerPaddedRect(int x, int y) {
        int paddingRadius = sideLengthRect / 10;
        return new Rect(x - paddingRadius, y - paddingRadius, x + paddingRadius, y + paddingRadius);
    }

    public CroppedCoordinate getCroppedGrid() {
        //scale grid before returning
        double scaleFactor = getDrawable().getIntrinsicWidth() * 1.0 / maxWidth;
        return new CroppedCoordinate((int) (rectCropGrid.left * scaleFactor), (int) (rectCropGrid.top * scaleFactor), (int) (sideLengthRect * scaleFactor));
    }

    public static class CroppedCoordinate {
        int x, y, side;

        public CroppedCoordinate(int x, int y, int side) {
            this.x = x;
            this.y = y;
            this.side = side;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getSide() {
            return side;
        }
    }
}
