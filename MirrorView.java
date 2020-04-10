package com.cutestudio.mirrorphotoeditor.ui.mirrorView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.cutestudio.mirrorphotoeditor.util.MirrorType;

import static com.cutestudio.mirrorphotoeditor.util.MirrorType.M1;

public class MirrorView extends View {
    private int currentX = 0;
    private int currentY = 0;
    private int downX = 0;
    private int upX = 0;
    private int downY = 0;
    private int upY = 0;
    private MirrorType mirrorType = M1;
    private Bitmap bitmap;
    private Bitmap bitmapFrame;
    private int newStartX;
    private int startX;
    private int newStartX1;
    private int startX1;
    private int newStartX2;
    private int startX2;
    private int newStartX3;
    private int startX3;
    private int newStartX4;
    private int startX4;
    private int newStartY;
    private int startY;
    private Matrix matrixVertical;
    private Matrix matrixHorizontal;
    private Matrix matrixVH;
    public MirrorView(Context context) {
        super(context);
        init();
    }

    public MirrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MirrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getDownY() {
        return downY;
    }

    public void setDownY(int downY) {
        this.downY = downY;
    }

    public int getUpY() {
        return upY;
    }

    public void setUpY(int upY) {
        this.upY = upY;
        this.startY = newStartY;
        this.downY = currentY;
    }

    public int getDownX() {
        return downX;
    }

    public void setDownX(int downX) {
        this.downX = downX;
    }

    public int getUpX() {
        return upX;
    }

    public void setUpX(int upX) {
        this.upX = upX;
        this.startX = newStartX;
        this.startX1 = newStartX1;
        this.startX2 = newStartX2;
        this.startX3 = newStartX3;
        this.startX4 = newStartX4;
        this.downX = currentX;
    }

    public int getCurrentX() {
        return currentX;
    }

    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    private void init() {
        setWillNotDraw(false);
        startX = 0;
        startX1 = 0;
        startX2 = 0;
        startX3 = 0;
        startX4 = 0;
        startY = 0;
        matrixVertical = new Matrix();
        matrixHorizontal = new Matrix();
        matrixVH = new Matrix();
        matrixVH.preScale(-1, -1);
        matrixHorizontal.preScale(-1, 1);
        matrixVertical.preScale(1, -1);
    }

    private Bitmap getBitmapScale(Bitmap bitmap) {
        Bitmap bitmapScale = null;
        int btmGalleryWidth = bitmap.getWidth();
        int btmGalleryHeight = bitmap.getHeight();
        try {
            if (bitmap.getHeight() < getHeight()) {
                bitmapScale = Bitmap.createScaledBitmap(bitmap,
                        (getHeight() * bitmap.getWidth()) / bitmap.getHeight(),
                        getHeight(), true);
            } else if (bitmap.getWidth() < getWidth() / 2) {
                bitmapScale = Bitmap.createScaledBitmap(bitmap,
                        getWidth() / 2,
                        (getWidth() / 2 * bitmap.getHeight()) / bitmap.getWidth(), true);
            } else {
                float scale = Math.min(btmGalleryWidth / (float) (getWidth() / 2),
                        btmGalleryHeight / (float) (getHeight()));
                bitmapScale = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale),
                        (int) (bitmap.getHeight() / scale), true);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmapScale;
    }

    private Bitmap getBitmapScaleQuarter(Bitmap bitmap) {
        Bitmap bitmapScale;
        int btmGalleryWidth = bitmap.getWidth();
        int btmGalleryHeight = bitmap.getHeight();
        if (bitmap.getHeight() < getHeight()) {
            bitmapScale = Bitmap.createScaledBitmap(bitmap,
                    ((getHeight() * bitmap.getWidth()) / bitmap.getHeight()) / 2,
                    getHeight() / 2, true);
        } else if (bitmap.getWidth() < getWidth() / 2) {
            bitmapScale = Bitmap.createScaledBitmap(bitmap, getWidth() / 4,
                    (getWidth() / 4 * bitmap.getHeight()) / bitmap.getWidth(), true);
        } else {
            float scaleWith = (btmGalleryWidth / (float) (getWidth() / 4));
            float scaleHeight = (btmGalleryHeight / (float) (getHeight() / 2));
            float scale;
            if (scaleWith < scaleHeight) {
                scale = scaleWith;
            } else {
                scale = scaleHeight;
            }
            bitmapScale = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale),
                    (int) (bitmap.getHeight() / scale), true);
        }
        return bitmapScale;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            float scale;
            int btmGalleryWidth = bitmap.getWidth();
            int btmGalleryHeight = bitmap.getHeight();
            Bitmap bitmapScale = getBitmapScale(bitmap);
            if (bitmapScale != null) {
                try {
                    switch (mirrorType) {
                        case M1: {
                            Rect desM1L = new Rect(0, 0, getWidth() / 2,
                                    getHeight());
                            Rect desM1R = new Rect(getWidth() / 2, 0, getWidth(),
                                    getHeight());
                            Bitmap bitmapMatrix = Bitmap.createBitmap(bitmapScale, 0, 0,
                                    bitmapScale.getWidth(), bitmapScale.getHeight(),
                                    matrixHorizontal, true);
                            int newEndX = initDrawBitmapTouchX(bitmapScale);
                            canvas.drawBitmap(bitmapScale, new Rect(newStartX, 0, newEndX,
                                    getHeight()), desM1L, null);
                            canvas.drawBitmap(bitmapMatrix, new Rect(
                                    bitmapMatrix.getWidth() - newEndX, 0,
                                    bitmapMatrix.getWidth() - newStartX, getHeight()),
                                    desM1R, null);
                            bitmapMatrix.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M2: {
                            Rect desM2L = new Rect(0, 0, getWidth() / 2, getHeight());
                            Rect desM2R = new Rect(getWidth() / 2, 0, getWidth(), getHeight());
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, 1.0f);
                            Bitmap bitmapMatrix = Bitmap.createBitmap(bitmapScale, 0, 0,
                                    bitmapScale.getWidth(), bitmapScale.getHeight(), matrix, true);
                            int newEndX = initDrawBitmapTouchX(bitmapScale);
                            canvas.drawBitmap(bitmapMatrix, new Rect(newStartX, 0, newEndX,
                                    getHeight()), desM2L, null);
                            canvas.drawBitmap(bitmapScale, new Rect(bitmapMatrix.getWidth() - newEndX,
                                    0, bitmapMatrix.getWidth() - newStartX, getHeight()), desM2R, null);
                            bitmapMatrix.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M3: {
                            Bitmap bitmapTopBot = getBitmapScaleTopBot(bitmap);
                            Rect desM3T = new Rect(0, 0, getWidth(), getHeight() / 2);
                            Rect desM3B = new Rect(0, getHeight() / 2, getWidth(), getHeight());
                            Matrix matrixM3 = new Matrix();
                            matrixM3.preScale(1.0f, -1.0f);
                            Bitmap bitmapFlipM3 = Bitmap.createBitmap(bitmapTopBot, 0, 0,
                                    bitmapTopBot.getWidth(), bitmapTopBot.getHeight(), matrixM3, true);
                            int newEndY = initDrawBitmapTouchY(bitmapTopBot);
                            canvas.drawBitmap(bitmapTopBot, new Rect(0, newStartY, getWidth(),
                                    newEndY), desM3T, null);
                            canvas.drawBitmap(bitmapFlipM3, new Rect(0,
                                    bitmapFlipM3.getHeight() - newEndY, getWidth(),
                                    bitmapFlipM3.getHeight() - newStartY), desM3B, null);
                            bitmapFlipM3.recycle();
                            bitmapTopBot.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M4: {
                            Bitmap bitmapTopBot = getBitmapScaleTopBot(bitmap);
                            Rect desM3T = new Rect(0, 0, getWidth(), getHeight() / 2);
                            Rect desM3B = new Rect(0, getHeight() / 2, getWidth(), getHeight());
                            Matrix matrixM4 = new Matrix();
                            matrixM4.preScale(1.0f, -1.0f);
                            Bitmap bitmapFlipM4 = Bitmap.createBitmap(bitmapTopBot, 0, 0,
                                    bitmapTopBot.getWidth(), bitmapTopBot.getHeight(),
                                    matrixM4, true);
                            int newEndY = initDrawBitmapTouchY(bitmapTopBot);
                            canvas.drawBitmap(bitmapFlipM4, new Rect(0, newStartY, getWidth(),
                                    newEndY), desM3T, null);
                            canvas.drawBitmap(bitmapTopBot, new Rect(0,
                                    bitmapFlipM4.getHeight() - newEndY, getWidth(),
                                    bitmapFlipM4.getHeight() - newStartY), desM3B, null);
                            bitmapFlipM4.recycle();
                            bitmapTopBot.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M5: {
                            Rect desM5L = new Rect(0, 0, getWidth() / 2, getHeight());
                            Rect desM5R = new Rect(getWidth() / 2, 0, getWidth(), getHeight());
                            int delta = downX - currentX;
                            newStartX = startX + delta;
                            int newEndX = newStartX + getWidth() / 2;
                            if (newEndX >= bitmapScale.getWidth()) {
                                newEndX = bitmapScale.getWidth();
                                newStartX = newEndX - getWidth() / 2;
                            } else if (newStartX < 0) {
                                newStartX = 0;
                                newEndX = newStartX + getWidth() / 2;
                            }
                            canvas.drawBitmap(bitmapScale, new Rect(newStartX, 0,
                                    newEndX, getHeight()), desM5L, null);
                            canvas.drawBitmap(bitmapScale, new Rect(newStartX, 0,
                                    newEndX, getHeight()), desM5R, null);
                            bitmapScale.recycle();
                            break;
                        }
                        case M6: {
                            Bitmap bitmapTopBot = getBitmapScaleTopBot(bitmap);
                            Rect desM6T = new Rect(0, 0, getWidth(), getHeight() / 2);
                            Rect desM6B = new Rect(0, getHeight() / 2, getWidth(), getHeight());
                            int delta = downY - currentY;
                            newStartY = startY + delta;
                            int newEndY = newStartY + getHeight() / 2;
                            if (newEndY >= bitmapTopBot.getHeight()) {
                                newEndY = bitmapTopBot.getHeight();
                                newStartY = newEndY - getHeight() / 2;
                            } else if (newStartY < 0) {
                                newStartY = 0;
                                newEndY = newStartY + getHeight() / 2;
                            }
                            canvas.drawBitmap(bitmapTopBot, new Rect(0, newStartY, getWidth(),
                                    newEndY), desM6T, null);
                            canvas.drawBitmap(bitmapTopBot, new Rect(0, newStartY, getWidth(),
                                    newEndY), desM6B, null);
                            bitmapTopBot.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M7: {
                            Rect desM7L = new Rect(0, 0, getWidth() / 2, getHeight());
                            Rect desM7R = new Rect(getWidth() / 2, 0, getWidth(), getHeight());
                            Matrix matrixM7 = new Matrix();
                            matrixM7.preScale(-1.0f, -1.0f);
                            int newEndX = initDrawBitmapTouchX(bitmapScale);
                            Bitmap bitmapMatrixM7 = Bitmap.createBitmap(bitmapScale, 0, 0,
                                    bitmapScale.getWidth(), bitmapScale.getHeight(),
                                    matrixM7, true);
                            canvas.drawBitmap(bitmapScale, new Rect(newStartX, 0, newEndX,
                                    getHeight()), desM7L, null);
                            canvas.drawBitmap(bitmapMatrixM7, new Rect(
                                    bitmapMatrixM7.getWidth() - newEndX,
                                    bitmapMatrixM7.getHeight() - getHeight(),
                                    bitmapMatrixM7.getWidth() - newStartX, bitmapMatrixM7.getHeight()),
                                    desM7R, null);
                            bitmapMatrixM7.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M8: {
                            Bitmap bitmapTopBot = getBitmapScaleTopBot(bitmap);
                            Rect desM8T = new Rect(0, 0, getWidth(), getHeight() / 2);
                            Rect desM8B = new Rect(0, getHeight() / 2, getWidth(), getHeight());
                            Matrix matrixM3 = new Matrix();
                            matrixM3.preScale(-1.0f, -1.0f);
                            Bitmap bitmapFlipM8 = Bitmap.createBitmap(bitmapTopBot, 0, 0,
                                    bitmapTopBot.getWidth(), bitmapTopBot.getHeight(), matrixM3, true);
                            int newEndY = initDrawBitmapTouchY(bitmapTopBot);
                            canvas.drawBitmap(bitmapTopBot, new Rect(0, newStartY, getWidth(),
                                    newEndY), desM8T, null);
                            canvas.drawBitmap(bitmapFlipM8, new Rect(0,
                                    bitmapFlipM8.getHeight() - newEndY, getWidth(),
                                    bitmapFlipM8.getHeight() - newStartY), desM8B, null);
                            bitmapFlipM8.recycle();
                            bitmapTopBot.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M9: {
                            Rect desM9L = new Rect(0, 0, getWidth() / 3, getHeight());
                            Rect desM9C = new Rect(getWidth() / 3, 0, 2 * getWidth() / 3, getHeight());
                            Rect desM9R = new Rect(2 * getWidth() / 3, 0, getWidth(), getHeight());
                            int delta1 = downX - currentX;
                            int delta2 = downX - currentX;
                            int delta3 = downX - currentX;
                            if (downX >= getWidth() / 3) {
                                delta1 = 0;
                            }
                            if (downX > (2 * getWidth()) / 3 || downX <= getWidth() / 3) {
                                delta2 = 0;
                            }
                            if (downX < 2 * getWidth() / 3) {
                                delta3 = 0;
                            }
                            newStartX1 = startX1 + delta1;
                            int newEndX1 = newStartX1 + getWidth() / 3;
                            int tempWL1 = newStartX1 + bitmapScale.getWidth() / 3;
                            int tempWR1 = newEndX1 + bitmapScale.getWidth() / 3;

                            if (tempWR1 > bitmapScale.getWidth()) {
                                tempWR1 = bitmapScale.getWidth();
                                newEndX1 = tempWR1 - bitmapScale.getWidth() / 3;
                                newStartX1 = newEndX1 - getWidth() / 3;
                                tempWL1 = tempWR1 - getWidth() / 3;
                            } else if (tempWL1 < 0) {
                                tempWL1 = 0;
                                tempWR1 = tempWL1 + getWidth() / 3;
                                newStartX1 = -bitmapScale.getWidth() / 3;
                            }
                            newStartX2 = startX2 + delta2;
                            int newEndX2 = newStartX2 + getWidth() / 3;
                            int tempWL2 = newStartX2 + bitmapScale.getWidth() / 3;
                            int tempWR2 = newEndX2 + bitmapScale.getWidth() / 3;
                            if (tempWR2 > bitmapScale.getWidth()) {
                                tempWR2 = bitmapScale.getWidth();
                                tempWL2 = tempWR2 - getWidth() / 3;
                                newEndX2 = tempWR2 - bitmapScale.getWidth() / 3;
                                newStartX2 = newEndX2 - getWidth() / 3;
                            } else if (tempWL2 < 0) {
                                tempWL2 = 0;
                                tempWR2 = tempWL2 + getWidth() / 3;
                                newStartX2 = -bitmapScale.getWidth() / 3;
                            }
                            newStartX3 = startX3 + delta3;
                            int newEndX3 = newStartX3 + getWidth() / 3;
                            int tempWL3 = newStartX3 + bitmapScale.getWidth() / 3;
                            int tempWR3 = newEndX3 + bitmapScale.getWidth() / 3;
                            if (tempWR3 > bitmapScale.getWidth()) {
                                tempWR3 = bitmapScale.getWidth();
                                newEndX3 = tempWR3 - bitmapScale.getWidth() / 3;
                                newStartX3 = newEndX3 - getWidth() / 3;
                                tempWL3 = tempWR3 - getWidth() / 3;
                            } else if (tempWL3 < 0) {
                                tempWL3 = 0;
                                tempWR3 = tempWL3 + getWidth() / 3;
                                newStartX3 = -bitmapScale.getWidth() / 3;
                            }
                            canvas.drawBitmap(bitmapScale, new Rect(tempWL1, 0, tempWR1,
                                    getHeight()), desM9L, null);
                            canvas.drawBitmap(bitmapScale, new Rect(tempWL2, 0, tempWR2,
                                    getHeight()), desM9C, null);
                            canvas.drawBitmap(bitmapScale, new Rect(tempWL3, 0, tempWR3,
                                    getHeight()), desM9R, null);
                            bitmapScale.recycle();
                            break;
                        }
                        case M10: {
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, 1.0f);
                            Bitmap bitmapMatrix = Bitmap.createBitmap(bitmapScale, 0, 0,
                                    bitmapScale.getWidth(), bitmapScale.getHeight(), matrix, true);
                            Rect desM10L = new Rect(0, 0, getWidth() / 4, getHeight());
                            Rect desM10C1 = new Rect(getWidth() / 4, 0,
                                    getWidth() / 2, getHeight());
                            Rect desM10C2 = new Rect(getWidth() / 2, 0,
                                    3 * getWidth() / 4, getHeight());
                            Rect desM10R = new Rect(3 * getWidth() / 4, 0,
                                    getWidth(), getHeight());
                            int delta1 = downX - currentX;
                            int delta2 = downX - currentX;
                            int delta3 = downX - currentX;
                            int delta4 = downX - currentX;
                            if (downX >= getWidth() / 4) {
                                delta1 = 0;
                            }
                            if (downX > getWidth() / 2 || downX <= getWidth() / 4) {
                                delta2 = 0;
                            }
                            if (downX < getWidth() / 2 || downX > 3 * getWidth() / 4) {
                                delta3 = 0;
                            }
                            if (downX < 3 * getWidth() / 4) {
                                delta4 = 0;
                            }
                            newStartX1 = startX1 + delta1;
                            int newEndX1 = newStartX1 + getWidth() / 4;
                            int tempWL1 = newStartX1 + bitmapScale.getWidth() / 4;
                            int tempWR1 = newEndX1 + bitmapScale.getWidth() / 4;
                            if (tempWR1 > bitmapScale.getWidth()) {
                                tempWR1 = bitmapScale.getWidth();
                                tempWL1 = tempWR1 - getWidth() / 4;
                                newEndX1 = tempWR1 - bitmapScale.getWidth() / 4;
                                newStartX1 = newEndX1 - getWidth() / 4;
                            } else if (tempWL1 < 0) {
                                tempWL1 = 0;
                                tempWR1 = tempWL1 + getWidth() / 4;
                                newStartX1 = -bitmapScale.getWidth() / 4;
                            }
                            newStartX2 = startX2 + delta2;
                            int newEndX2 = newStartX2 + getWidth() / 4;
                            int tempWL2 = newStartX2 + bitmapScale.getWidth() / 4;
                            int tempWR2 = newEndX2 + bitmapScale.getWidth() / 4;
                            if (tempWR2 > bitmapScale.getWidth()) {
                                tempWR2 = bitmapScale.getWidth();
                                tempWL2 = tempWR2 - getWidth() / 4;
                                newEndX2 = tempWR2 - bitmapScale.getWidth() / 4;
                                newStartX2 = newEndX2 - getWidth() / 4;
                            } else if (tempWL2 < 0) {
                                tempWL2 = 0;
                                tempWR2 = tempWL2 + getWidth() / 4;
                                newStartX2 = -bitmapScale.getWidth() / 4;
                            }
                            newStartX3 = startX3 + delta3;
                            int newEndX3 = newStartX3 + getWidth() / 4;
                            int tempWL3 = newStartX3 + bitmapScale.getWidth() / 4;
                            int tempWR3 = newEndX3 + bitmapScale.getWidth() / 4;
                            if (tempWR3 > bitmapScale.getWidth()) {
                                tempWR3 = bitmapScale.getWidth();
                                tempWL3 = tempWR3 - getWidth() / 4;
                                newEndX3 = tempWR3 - bitmapScale.getWidth() / 4;
                                newStartX3 = newEndX3 - getWidth() / 4;
                            } else if (tempWL3 < 0) {
                                tempWL3 = 0;
                                tempWR3 = tempWL3 + getWidth() / 4;
                                newStartX3 = -bitmapScale.getWidth() / 4;
                            }
                            newStartX4 = startX4 + delta4;
                            int newEndX4 = newStartX4 + getWidth() / 4;
                            int tempWL4 = newStartX4 + bitmapScale.getWidth() / 4;
                            int tempWR4 = newEndX4 + bitmapScale.getWidth() / 4;
                            if (tempWR4 > bitmapScale.getWidth()) {
                                tempWR4 = bitmapScale.getWidth();
                                tempWL4 = tempWR4 - getWidth() / 4;
                                newEndX4 = tempWR4 - bitmapScale.getWidth() / 4;
                                newStartX4 = newEndX4 - getWidth() / 4;
                            } else if (tempWL4 < 0) {
                                tempWL4 = 0;
                                tempWR4 = tempWL4 + getWidth() / 4;
                                newStartX4 = -bitmapScale.getWidth() / 4;
                            }
                            canvas.drawBitmap(bitmapScale, new Rect(tempWL1, 0, tempWR1,
                                    getHeight()), desM10L, null);
                            canvas.drawBitmap(bitmapMatrix, new Rect(tempWL2, 0, tempWR2,
                                    getHeight()), desM10C1, null);
                            canvas.drawBitmap(bitmapScale, new Rect(tempWL3, 0, tempWR3,
                                    getHeight()), desM10C2, null);
                            canvas.drawBitmap(bitmapMatrix, new Rect(tempWL4, 0, tempWR4,
                                    getHeight()), desM10R, null);
                            bitmapMatrix.recycle();
                            bitmapScale.recycle();
                            break;
                        }
                        case M11: {
                            Bitmap bitmapScaleRoss;
                            Rect des = new Rect(0, 0, getWidth(), getHeight());
                            if (getWidth() > bitmap.getWidth() && getHeight() > bitmap.getHeight()) {
                                int heightRoss = (int) ((getWidth() * bitmap.getHeight()) / (float) bitmap.getWidth());
                                int width = getWidth();
                                if (heightRoss < getHeight()) {
                                    heightRoss = getHeight();
                                    width = (int) (getHeight() * bitmap.getWidth() / (float) (bitmap.getHeight()));
                                }
                                bitmapScaleRoss = Bitmap.createScaledBitmap(bitmap, width, heightRoss, true);
                            } else {
                                float scaleWith = (btmGalleryWidth / (float) (getWidth()));
                                float scaleHeight = (btmGalleryHeight / (float) (getHeight()));
                                if (scaleWith < scaleHeight) {
                                    scale = scaleWith;
                                } else {
                                    scale = scaleHeight;
                                }
                                bitmapScaleRoss = Bitmap.createScaledBitmap(bitmap,
                                        (int) (bitmap.getWidth() / scale),
                                        (int) (bitmap.getHeight() / scale), true);
                            }
                            int delta = downX - currentX;
                            if (downX < downY) {
                                delta = -delta;
                            }
                            newStartX = startX + delta;
                            int newEndX = newStartX + getWidth();
                            if (newEndX > bitmapScaleRoss.getWidth()) {
                                newEndX = bitmapScaleRoss.getWidth();
                                newStartX = bitmapScaleRoss.getWidth() - getWidth();
                            } else if (newStartX < 0) {
                                newStartX = 0;
                                newEndX = getWidth();
                            }
                            Matrix matrix = new Matrix();
                            matrix.preScale(-1.0f, -1.0f);
                            Bitmap bitmapMatrix = Bitmap.createBitmap(bitmapScaleRoss, 0, 0,
                                    bitmapScaleRoss.getWidth(), bitmapScaleRoss.getHeight(), matrix, true);
                            Bitmap bitmapCross = getBitmapCross(bitmapScaleRoss, newStartX, newEndX);
                            canvas.drawBitmap(bitmapMatrix, new Rect(newStartX,
                                    bitmapScaleRoss.getHeight() - getHeight(),
                                    newEndX, bitmapScaleRoss.getHeight()), des, null);
                            canvas.drawBitmap(bitmapCross, new Rect(0, 0, getWidth(),
                                    getHeight()), des, null);
                            bitmapScaleRoss.recycle();
                            bitmapScale.recycle();
                            bitmapCross.recycle();
                            bitmapMatrix.recycle();
                            break;
                        }
                        case M12: {
                            Bitmap bitmapScaleRoss;
                            Rect des = new Rect(0, 0, getWidth(), getHeight());
                            if (getWidth() > bitmap.getWidth() && getHeight() > bitmap.getHeight()) {
                                int heightRoss = (int) ((getWidth() * bitmap.getHeight()) / (float) bitmap.getWidth());
                                int width = getWidth();
                                if (heightRoss < getHeight()) {
                                    heightRoss = getHeight();
                                    width = (int) (getHeight() * bitmap.getWidth() / (float) (bitmap.getHeight()));
                                }
                                bitmapScaleRoss = Bitmap.createScaledBitmap(bitmap, width, heightRoss, true);
                            } else {
                                float scaleWith = (btmGalleryWidth / (float) (getWidth()));
                                float scaleHeight = (btmGalleryHeight / (float) (getHeight()));
                                if (scaleWith < scaleHeight) {
                                    scale = scaleWith;
                                } else {
                                    scale = scaleHeight;
                                }
                                bitmapScaleRoss = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() / scale),
                                        (int) (bitmap.getHeight() / scale), true);
                            }
                            int delta = downX - currentX;
                            newStartX = startX + delta;
                            int newEndX = newStartX + getWidth();
                            if (newEndX > bitmapScaleRoss.getWidth()) {
                                newEndX = bitmapScaleRoss.getWidth();
                                newStartX = bitmapScaleRoss.getWidth() - getWidth();
                            } else if (newStartX < 0) {
                                newStartX = 0;
                                newEndX = getWidth();
                            }
                            int heightBitmapSmall = (int) (getWidth() / 3 * bitmap.getHeight() / (float) (bitmap.getWidth()));
                            int widthBitmapSmall = getWidth() / 3;
                            if (heightBitmapSmall < getHeight() / 3) {
                                heightBitmapSmall = getHeight() / 3;
                                widthBitmapSmall = (int) (getHeight() / 3 * bitmap.getWidth() / (float) (bitmap.getHeight()));
                            }
                            Bitmap bitmapScaleSmall = Bitmap.createScaledBitmap(bitmap, widthBitmapSmall, heightBitmapSmall, true);
                            Bitmap bitmapCircle = getCircleBitmap(bitmapScaleSmall, newStartX);
                            canvas.drawBitmap(bitmapScaleRoss, new Rect(newStartX, 0, newEndX, getHeight()), des, null);
                            canvas.drawBitmap(bitmapCircle, getWidth() / 3, getHeight() / 3, null);
                            bitmapScaleSmall.recycle();
                            bitmapScale.recycle();
                            bitmapCircle.recycle();
                            bitmapScaleRoss.recycle();
                            break;
                        }
                        case M13: {
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixHorizontal, true);
                            Bitmap flipBitmapVertical = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixVertical, true);
                            drawMirrorFromFourBitmap(initBitmapMirrorFromFourBitmap(bitmap,
                                    flipBitmapHorizontal, bitmap, flipBitmapVertical), canvas,
                                    matrixVertical, matrixHorizontal);
                            bitmapScale.recycle();
                            flipBitmapHorizontal.recycle();
                            flipBitmapVertical.recycle();
                            break;
                        }
                        case M14: {
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixHorizontal, true);
                            Bitmap flipBitmapVertical = Bitmap.createBitmap(flipBitmapHorizontal, 0,
                                    0, bitmap.getWidth(), bitmap.getHeight(), matrixVertical, true);
                            drawMirrorFromFourBitmap(initBitmapMirrorFromFourBitmap(flipBitmapHorizontal,
                                    bitmap, flipBitmapHorizontal, flipBitmapVertical), canvas, matrixVertical,
                                    matrixHorizontal);
                            bitmapScale.recycle();
                            flipBitmapHorizontal.recycle();
                            flipBitmapVertical.recycle();
                            break;
                        }
                        case M15: {
                            Bitmap flipBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrixVertical, true);
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(flipBitmap, 0, 0,
                                    flipBitmap.getWidth(), flipBitmap.getHeight(), matrixHorizontal, true);
                            Bitmap flipBitmapVertical = Bitmap.createBitmap(flipBitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixVertical, true);
                            drawMirrorFromFourBitmap(initBitmapMirrorFromFourBitmap(flipBitmap,
                                    flipBitmapHorizontal, flipBitmap, flipBitmapVertical), canvas,
                                    matrixVertical, matrixHorizontal);
                            bitmapScale.recycle();
                            flipBitmap.recycle();
                            flipBitmapHorizontal.recycle();
                            flipBitmapVertical.recycle();
                            break;
                        }
                        case M16: {
                            Bitmap flipBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrixVH, true);
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(flipBitmap, 0, 0,
                                    flipBitmap.getWidth(), flipBitmap.getHeight(), matrixHorizontal, true);
                            Bitmap flipBitmapVertical = Bitmap.createBitmap(flipBitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixVertical, true);
                            drawMirrorFromFourBitmap(initBitmapMirrorFromFourBitmap(flipBitmap,
                                    flipBitmapHorizontal, flipBitmap, flipBitmapVertical), canvas,
                                    matrixVertical, matrixHorizontal);
                            bitmapScale.recycle();
                            flipBitmap.recycle();
                            flipBitmapHorizontal.recycle();
                            flipBitmapVertical.recycle();
                            break;
                        }
                        case M17: {
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixHorizontal, true);
                            Bitmap flipBitmapVertical = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), null, true);
                            if (bitmap.getHeight() > bitmap.getWidth()) {
                                drawMirrorM17M18M19WhenHeightLonger(initBitmapMirrorFromFourBitmap(bitmap,
                                        flipBitmapHorizontal, bitmap, flipBitmapVertical),
                                        initBitmapMirrorFromFourBitmap(bitmap, flipBitmapHorizontal,
                                                bitmap, flipBitmapVertical), canvas);
                            } else {
                                drawMirrorM17WhenHeightShort(initBitmapMirrorFromFourBitmap(bitmap,
                                        flipBitmapHorizontal, bitmap, flipBitmapVertical), canvas,
                                        matrixHorizontal);
                            }
                            bitmapScale.recycle();
                            flipBitmapHorizontal.recycle();
                            flipBitmapVertical.recycle();
                            break;
                        }
                        case M18: {
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap.getWidth(), bitmap.getHeight(), matrixHorizontal, true);
                            Bitmap bitmapScaleM18 = getBitmapScaleQuarter(bitmap);
                            Bitmap bitmapMatrixM18 = Bitmap.createBitmap(bitmapScaleM18, 0, 0,
                                    bitmapScaleM18.getWidth(), bitmapScaleM18.getHeight(), matrixHorizontal, true);
                            if (bitmap.getHeight() > bitmap.getWidth()) {
                                drawMirrorM17M18M19WhenHeightLonger(initBitmapMirrorFromFourBitmap(bitmap,
                                        flipBitmapHorizontal, bitmap, flipBitmapHorizontal),
                                        initBitmapMirrorFromFourBitmap(flipBitmapHorizontal, bitmap,
                                                bitmap, flipBitmapHorizontal), canvas);
                            } else {
                                drawMirrorM18M19WhenHeightShort(canvas, bitmapScaleM18, bitmapMatrixM18,
                                        bitmapMatrixM18, bitmapScaleM18);
                            }
                            bitmapScale.recycle();
                            bitmapMatrixM18.recycle();
                            bitmapScaleM18.recycle();
                            flipBitmapHorizontal.recycle();
                            break;
                        }
                        case M19: {
                            Bitmap flipBitmapHorizontal = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrixHorizontal, true);
                            Bitmap bitmapScaleM19 = getBitmapScaleQuarter(bitmap);
                            Bitmap bitmapMatrixM19 = Bitmap.createBitmap(bitmapScaleM19, 0, 0,
                                    bitmapScaleM19.getWidth(), bitmapScaleM19.getHeight(), matrixHorizontal, true);
                            if (bitmap.getHeight() > bitmap.getWidth()) {
                                drawMirrorM17M18M19WhenHeightLonger(initBitmapMirrorFromFourBitmap(
                                        flipBitmapHorizontal, bitmap, flipBitmapHorizontal, bitmap),
                                        initBitmapMirrorFromFourBitmap(bitmap, flipBitmapHorizontal,
                                                flipBitmapHorizontal, bitmap), canvas);
                            } else {
                                drawMirrorM18M19WhenHeightShort(canvas, bitmapMatrixM19, bitmapScaleM19,
                                        bitmapScaleM19, bitmapMatrixM19);
                            }
                            bitmapScale.recycle();
                            bitmapMatrixM19.recycle();
                            bitmapScaleM19.recycle();
                            flipBitmapHorizontal.recycle();
                            break;
                        }
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
        }
        if (bitmapFrame != null && !bitmapFrame.isRecycled()) {
            canvas.drawBitmap(bitmapFrame, 0, 0, null);
        }
    }

    private Bitmap initBitmapMirrorFromFourBitmap(Bitmap bitmapLHeightLonger,
                                                  Bitmap bitmapRHeightLonger,
                                                  Bitmap bitmapTHeightShort,
                                                  Bitmap bitmapBHeightShort) throws OutOfMemoryError {
        Bitmap bitmapWithFlip;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height > width) {
            bitmapWithFlip = Bitmap.createBitmap(width + width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapWithFlip);
            canvas.drawBitmap(bitmapLHeightLonger, 0, 0, null);
            canvas.drawBitmap(bitmapRHeightLonger, width, 0, null);
            return bitmapWithFlip;
        } else {
            bitmapWithFlip = Bitmap.createBitmap(width, height + height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapWithFlip);
            canvas.drawBitmap(bitmapTHeightShort, 0, 0, null);
            canvas.drawBitmap(bitmapBHeightShort, 0, height, null);
            return bitmapWithFlip;
        }
    }

    private void drawMirrorFromFourBitmap(Bitmap bitmapDrawn, Canvas canvas, Matrix matrixVertical,
                                          Matrix matrixHorizontal) throws OutOfMemoryError {
        if (bitmap.getHeight() > bitmap.getWidth()) {
            Bitmap bitmapTopBot = getBitmapScaleTopBot(bitmapDrawn);
            Rect desM3T = new Rect(0, 0, getWidth(), getHeight() / 2);
            Rect desM3B = new Rect(0, getHeight() / 2, getWidth(), getHeight());
            Bitmap bitmapFlipM3 = Bitmap.createBitmap(bitmapTopBot, 0, 0, bitmapTopBot.getWidth(),
                    bitmapTopBot.getHeight(), matrixVertical, true);
            int newEndY = initDrawBitmapTouchY(bitmapTopBot);
            canvas.drawBitmap(bitmapTopBot, new Rect(0, newStartY, getWidth(), newEndY),
                    desM3T, null);
            canvas.drawBitmap(bitmapFlipM3, new Rect(0, bitmapFlipM3.getHeight() - newEndY,
                    getWidth(), bitmapFlipM3.getHeight() - newStartY), desM3B, null);
            bitmapFlipM3.recycle();
            bitmapTopBot.recycle();
        } else {
            Rect desM1L = new Rect(0, 0, getWidth() / 2, getHeight());
            Rect desM1R = new Rect(getWidth() / 2, 0, getWidth(), getHeight());
            Bitmap bitmapScaleM = getBitmapScale(bitmapDrawn);
            Bitmap bitmapMatrix = Bitmap.createBitmap(bitmapScaleM, 0, 0, bitmapScaleM.getWidth(),
                    bitmapScaleM.getHeight(), matrixHorizontal, true);
            int newEndX = initDrawBitmapTouchX(bitmapScaleM);
            canvas.drawBitmap(bitmapScaleM, new Rect(newStartX, 0, newEndX, getHeight()),
                    desM1L, null);
            canvas.drawBitmap(bitmapMatrix, new Rect(bitmapMatrix.getWidth() - newEndX, 0,
                    bitmapMatrix.getWidth() - newStartX, getHeight()), desM1R, null);
            bitmapMatrix.recycle();
            bitmapScaleM.recycle();
        }
        bitmapDrawn.recycle();
    }

    private void drawMirrorM17M18M19WhenHeightLonger(Bitmap bitmapDrawn1, Bitmap bitmapDrawn2,
                                                     Canvas canvas) throws OutOfMemoryError{
        Bitmap bitmapTopBot = getBitmapScaleTopBot(bitmapDrawn1);
        Rect desT = new Rect(0, 0, getWidth(), getHeight() / 2);
        Rect desB = new Rect(0, getHeight() / 2, getWidth(), getHeight());
        Bitmap bitmapFlip = getBitmapScaleTopBot(bitmapDrawn2);
        int delta = downY - currentY;
        newStartY = startY + delta;
        int newEndY = newStartY + getHeight() / 2;
        if (newEndY >= bitmapTopBot.getHeight()) {
            newEndY = bitmapTopBot.getHeight();
            newStartY = newEndY - getHeight() / 2;
        } else if (newStartY < 0) {
            newStartY = 0;
            newEndY = newStartY + getHeight() / 2;
        }
        canvas.drawBitmap(bitmapTopBot, new Rect(0, newStartY, getWidth(), newEndY),
                desT, null);
        canvas.drawBitmap(bitmapFlip, new Rect(0, newStartY, getWidth(), newEndY),
                desB, null);
        bitmapTopBot.recycle();
        bitmapFlip.recycle();
        bitmapDrawn1.recycle();
        bitmapDrawn2.recycle();
    }

    private void drawMirrorM18M19WhenHeightShort(Canvas canvas, Bitmap bitmap1, Bitmap bitmap2,
                                                 Bitmap bitmap3, Bitmap bitmap4) throws OutOfMemoryError{
        Rect des1 = new Rect(0, 0, getWidth() / 2, getHeight() / 2);
        Rect des2 = new Rect(getWidth() / 2, 0, getWidth(), getHeight() / 2);
        Rect des3 = new Rect(0, getHeight() / 2, getWidth() / 2, getHeight());
        Rect des4 = new Rect(getWidth() / 2, getHeight() / 2, getWidth(), getHeight());
        int newEndX = initDrawBitmapTouchX(bitmap1);
        canvas.drawBitmap(bitmap1, new Rect(newStartX, 0, newEndX, getHeight() / 2),
                des1, null);
        canvas.drawBitmap(bitmap2, new Rect(bitmap1.getWidth() - newEndX, 0,
                bitmap1.getWidth() - newStartX, getHeight() / 2), des2, null);
        canvas.drawBitmap(bitmap3, new Rect(bitmap1.getWidth() - newEndX, 0,
                bitmap1.getWidth() - newStartX, getHeight() / 2), des3, null);
        canvas.drawBitmap(bitmap4, new Rect(newStartX, 0, newEndX, getHeight() / 2),
                des4, null);
    }

    private void drawMirrorM17WhenHeightShort(Bitmap bitmapDrawn1, Canvas canvas,
                                              Matrix matrixHorizontal) throws OutOfMemoryError{
        Rect desM1L = new Rect(0, 0, getWidth() / 2, getHeight());
        Rect desM1R = new Rect(getWidth() / 2, 0, getWidth(), getHeight());
        Bitmap bitmapScaleM = getBitmapScale(bitmapDrawn1);
        Bitmap bitmapMatrix = Bitmap.createBitmap(bitmapScaleM, 0, 0, bitmapScaleM.getWidth(),
                bitmapScaleM.getHeight(), matrixHorizontal, true);
        int newEndX = initDrawBitmapTouchX(bitmapScaleM);
        canvas.drawBitmap(bitmapScaleM, new Rect(newStartX, 0, newEndX, getHeight()),
                desM1L, null);
        canvas.drawBitmap(bitmapMatrix, new Rect(bitmapMatrix.getWidth() - newEndX, 0,
                bitmapMatrix.getWidth() - newStartX, getHeight()), desM1R, null);
        bitmapMatrix.recycle();
        bitmapScaleM.recycle();
        bitmapDrawn1.recycle();
    }

    public void setTypeMirror(MirrorType type) {
        mirrorType = type;
        invalidate();
    }

    public void setTypeFrame(Bitmap bitmap) {
        if (bitmapFrame != null) {
            bitmapFrame.recycle();
        }
        bitmapFrame = bitmap;
        invalidate();
    }

    public void setBitmapFlip(Bitmap bitmapFlip) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        bitmap = Bitmap.createBitmap(bitmapFlip);
        invalidate();
    }

    public void setBitmapItem(Bitmap bitmapItem) {
        if (bitmapItem != null)
            this.bitmap = bitmapItem;
        startX = 50;
        startY = 40;
        startX1 = 10;
        startX2 = 10;
        startX3 = 10;
        startX4 = 10;
    }

    private int initDrawBitmapTouchX(Bitmap bitmapScale) {
        int newEndX;
        int delta = downX - currentX;
        if (downX > getWidth() / 2)
            delta = -delta;
        newStartX = startX + delta;

        newEndX = newStartX + getWidth() / 2;
        if (newEndX >= bitmapScale.getWidth()) {
            newEndX = bitmapScale.getWidth();
            newStartX = newEndX - getWidth() / 2;
        } else if (newStartX < 0) {
            newStartX = 0;
            newEndX = newStartX + getWidth() / 2;
        }
        return newEndX;
    }

    private int initDrawBitmapTouchY(Bitmap bitmapScale) {
        int newEndY;
        int delta = downY - currentY;
        if (downY > getHeight() / 2)
            delta = -delta;

        newStartY = startY + delta;
        newEndY = newStartY + getHeight() / 2;
        if (newEndY >= bitmapScale.getHeight()) {
            newEndY = bitmapScale.getHeight();
            newStartY = newEndY - getHeight() / 2;
        } else if (newStartY < 0) {
            newStartY = 0;
            newEndY = newStartY + getHeight() / 2;
        }
        return newEndY;
    }

    private Bitmap getBitmapScaleTopBot(Bitmap bitmap) throws OutOfMemoryError{
        Bitmap bitmapScale;
        bitmapScale = Bitmap.createScaledBitmap(bitmap, getWidth(),
                (int) ((getWidth() * bitmap.getHeight()) / (float) bitmap.getWidth()), false);
        return bitmapScale;
    }

    private Bitmap getBitmapCross(Bitmap bitmapResource, int newStartX, int newEndX) throws OutOfMemoryError {
        Bitmap bitmapCross = Bitmap.createBitmap(bitmapResource.getWidth(),
                bitmapResource.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCross);
        Paint mPaint = new Paint();
        Point pointA = new Point(0, 0);
        Point pointB = new Point(getWidth(), getHeight());
        Point pointC = new Point(0, getHeight());
        Path path = new Path();
        Rect des = new Rect(0, 0, getWidth(), getHeight());
        path.moveTo(pointA.x, pointA.y);
        path.lineTo(pointB.x, pointB.y);
        path.lineTo(pointC.x, pointC.y);
        path.lineTo(pointA.x, pointA.y);
        canvas.drawARGB(0, 0, 0, 0);
        mPaint.setColor(Color.RED);
        canvas.drawPath(path, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapResource, new Rect(bitmapResource.getWidth() - newEndX,
                0, bitmapResource.getWidth() - newStartX, getHeight()), des, mPaint);
        return bitmapCross;
    }

    private Bitmap getCircleBitmap(Bitmap bitmap, int newStartX) throws OutOfMemoryError{
        float newEndX = (float) ((newStartX / 3) + getWidth() / 3);
        Bitmap output;
        Rect srcRect, dstRect;
        float r;
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();

        if (width > height) {
            output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            r = height / 2;
        } else {
            output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            r = width / 2;
        }
        srcRect = new Rect((int) (float) (newStartX / 3), 0, (int) newEndX,
                getHeight() / 3);
        dstRect = new Rect(0, 0, getWidth() / 3, getHeight() / 3);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint);
        return output;
    }
}
