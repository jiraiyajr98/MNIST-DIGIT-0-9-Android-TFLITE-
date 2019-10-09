package com.ankan.mnist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rm.freedrawview.FreeDrawView;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;
import com.rm.freedrawview.ResizeBehaviour;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Button calculate;
    private Interpreter tflite;
    FreeDrawView mSignatureView;
    TextView predictedText;

    Button clearButton;
    private final float[][] mResult = new float[1][10];


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        calculate = findViewById(R.id.calculate);
        clearButton = findViewById(R.id.clear_button);
        predictedText = findViewById(R.id.predicted_tv);

        try {
            tflite = new Interpreter(loadModelFile(this.getAssets(),"mnist.tflite"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        mSignatureView = findViewById(R.id.your_id);
        // Setup the View
        mSignatureView.setPaintColor(Color.BLACK);

        //mSignatureView.setPaintWidthDp(6);
        mSignatureView.setPaintAlpha(255);// from 0 to 255
        mSignatureView.setResizeBehaviour(ResizeBehaviour.CROP);// Must be one of ResizeBehaviour
        // values;

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignatureView.clearDraw();
                predictedText.setText("");
            }
        });


        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // This will take a screenshot of the current drawn content of the view
                mSignatureView.getDrawScreenshot(new FreeDrawView.DrawCreatorListener() {
                    @Override
                    public void onDrawCreated(Bitmap draw) {

                        Utils utils = new Utils();
                        tflite.run(
                                utils.classify(toGrayscale(Bitmap.createScaledBitmap(draw, 28, 28, false)))
                                ,
                                mResult);
//                        utils.classify(toGrayscale(Bitmap.createScaledBitmap(draw, 28, 28, false)));


                        Log.d("BYTE-ARRAY", "classify(): result = " + Arrays.toString(mResult[0]));
                        //Toast.makeText(MainActivity.this, "Predicted "+argmax(mResult[0]), Toast.LENGTH_SHORT).show();
                        predictedText.setText(String.valueOf(argmax(mResult[0])));
                    }

                    @Override
                    public void onDrawCreationError() {
                        // Something went wrong creating the bitmap, should never
                        // happen unless the async task has been canceled
                    }
                });

            }
        });

    }

    public static Bitmap toGrayscale(Bitmap srcImage) {

        Bitmap bmpGrayscale = Bitmap.createBitmap(srcImage.getWidth(), srcImage.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bmpGrayscale);
        Paint paint = new Paint();

        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcImage, 0, 0, paint);

        return bmpGrayscale;
    }

    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    private static int argmax(float[] probs) {
        int maxIdx = -1;
        float maxProb = 0.0f;
        for (int i = 0; i < probs.length; i++) {
            if (probs[i] > maxProb) {
                maxProb = probs[i];
                maxIdx = i;
            }
        }
        return maxIdx;
    }
}
