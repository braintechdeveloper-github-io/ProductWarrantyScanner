package com.johnmelodyme.starlabsproductswarranty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = "Starlabs";
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private TextView SERIAL_NUMBER, hidden;
    private Spinner MENU_DROP;
    private Button SEND;
    private CameraSource cameraSource;
    private String INTENT_DATA = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, MainActivity.class.getName() + " on Starting");
        surfaceView = findViewById(R.id.surfaceView);
        hidden = findViewById(R.id.hiddenresult);
        SERIAL_NUMBER = findViewById(R.id.serialnumber);
//        SERIAL_NUMBER.setText("Serial Number:");
        init(); // -------> start detection:
        Context context = this;
        mediaPlayer = MediaPlayer.create(context, R.raw.beep);
        SEND = findViewById(R.id.todatabase);
        MENU_DROP = findViewById(R.id.menu_drop);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.starlabs_products, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MENU_DROP.setAdapter(adapter);

    }


    private void init() {
        // TODO BARCODE_DETECTOR:
        BarcodeDetector barcodeDetector = new BarcodeDetector
                .Builder(MainActivity.this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        // TODO CAMERA_SOURCE:
        cameraSource = new CameraSource
                .Builder(MainActivity.this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();

        // TODO SURFACE_VIEW:
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                        Log.d(TAG, "surfaceCreated: getholder");
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        Log.d(TAG, "surfaceCreated: REQUEST_CAMERA_PERMISSION");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "surfaceCreated: " + e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // TODO BARCODE_DETECTOR:
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(),
                        "To prevent memory leaks barcode scanner has been stopped",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes;
                barcodes = detections.getDetectedItems();
                // TODO handler
                if (barcodes.size() != 0) {
                    SERIAL_NUMBER.post(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.start();
                            INTENT_DATA = barcodes.valueAt(0).displayValue;
                            SERIAL_NUMBER.setText("Serial No: " + INTENT_DATA);
                            Log.d(TAG, "receiveDetections:  SERIAL NUMBER ==>" + INTENT_DATA);

                            SEND.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MENU_DROP.setOnItemSelectedListener(MainActivity.this);

                                    if (MENU_DROP.getSelectedItem().equals("Smart Sleep Device")){
                                        hidden.setText("Smart Sleep Device");
                                        Log.d(TAG, "onClick: Smart Sleep Device ");
                                    } else if (MENU_DROP.getSelectedItem().equals("Blood Sugar Control")){
                                        hidden.setText("Blood Sugar Control");
                                        Log.d(TAG, "onClick: Blood Sugar Control");
                                    } else if (MENU_DROP.getSelectedItem().equals("Neurosound Technology")){
                                        hidden.setText("Neurosound Technology");
                                        Log.d(TAG, "onClick: Neurosound Technology " );
                                    } else if (MENU_DROP.getSelectedItem().equals("--Please Select a product--")){
                                        disp("Please Select a Product!");
                                    } else {
                                        Log.d(TAG, "=========================================");
                                    }

                                    String SERIALNUMBER, PRODUCT;
                                    SERIALNUMBER = INTENT_DATA;
                                    PRODUCT = hidden.getText().toString().trim(); //

                                }
                            });
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
        cameraSource.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        if (!INTENT_DATA.isEmpty()){
            cameraSource.stop();
        }
    }

    public void disp(String s){
        Toast.makeText(MainActivity.this, s,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String PRODUCT_SELECTED = parent.getItemAtPosition(position).toString();
        hidden.setText(PRODUCT_SELECTED);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}