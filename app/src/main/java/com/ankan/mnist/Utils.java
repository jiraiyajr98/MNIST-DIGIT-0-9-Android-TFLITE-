package com.ankan.mnist;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Utils {

    private final ByteBuffer mImageData;
    private static final int BATCH_SIZE = 1;
    public static final int IMG_HEIGHT = 28;
    public static final int IMG_WIDTH = 28;
    private static final int NUM_CHANNEL = 1;
    private static final int NUM_CLASSES = 10;
    private final int[] mImagePixels = new int[IMG_HEIGHT * IMG_WIDTH];
    private final float[][] mResult = new float[1][NUM_CLASSES];


     Utils() {

        this.mImageData = ByteBuffer.allocateDirect(
                4 * BATCH_SIZE * IMG_HEIGHT * IMG_WIDTH * NUM_CHANNEL);

         this.mImageData.order(ByteOrder.nativeOrder());
    }

     ByteBuffer classify(Bitmap bitmap) {

        preprocess(bitmap);
        return mImageData;

//        Log.d("BYTE-ARRAY","Original ByteBuffer:  "
//                + Arrays.toString(mImageData.array()));
//
//        Log.d("BYTE-ARRAY","Size "+mImageData.flip().remaining());
//
//        for(int i = 0 ; i < 784; i++)
//            Log.d("BYTE-ARRAY", String.valueOf(mImageData.get(i)));

    }


//    private void convertBitmapToByteBuffer(Bitmap bitmap) {
//        if (mImageData == null) {
//            return;
//        }
//        mImageData.rewind();
//
//        bitmap.getPixels(mImagePixels, 0, bitmap.getWidth(), 0, 0,
//                bitmap.getWidth(), bitmap.getHeight());
//
//        int pixel = 0;
//        for (int i = 0; i < IMG_WIDTH; ++i) {
//            for (int j = 0; j < IMG_HEIGHT; ++j) {
//                int value = mImagePixels[pixel++];
//                mImageData.putFloat(convertPixel(value));
//            }
//        }
//    }

//    private static float convertPixel(int color) {
//        return (255 - (((color >> 16) & 0xFF) * 0.299f
//                + ((color >> 8) & 0xFF) * 0.587f
//                + (color & 0xFF) * 0.114f)) / 255.0f;
//    }

    private  void preprocess(Bitmap bitmap) {
        int[] pixels = new int[IMG_HEIGHT * IMG_HEIGHT];

        // Load bitmap pixels into the temporary pixels variable
        bitmap.getPixels(pixels, 0, IMG_HEIGHT, 0, 0, IMG_WIDTH, IMG_HEIGHT);

        for (int pixel : pixels) {
            // Set 0 for white and 255 for black pixels
            int channel = pixel & 0xff;
            mImageData.putFloat(0xff - channel);
        }
    }


}
