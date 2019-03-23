package com.example.user.wifistrengthdetector;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView strength,percent;
    private Button check,store;
    private ProgressBar percentage;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        strength=(TextView)findViewById(R.id.tv_strength);
        percent=(TextView)findViewById(R.id.tv_percent);
        check=(Button)findViewById(R.id.btn_check);
        store=(Button)findViewById(R.id.btn_store);
        percentage=(ProgressBar)findViewById(R.id.pgb_strength);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStrength();
            }
        });
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                store();
            }
        });
        handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                checkStrength();
                store();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);
    }

    void checkStrength() {
        int progressStatus = 0;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int rssi = wifiManager.getConnectionInfo().getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 100);
        String ssid = wifiManager.getConnectionInfo().getSSID();
        String MacAddr = wifiManager.getConnectionInfo().getMacAddress();
        strength.setText("\t\tSSID : " + ssid + "\n\n\t\tMAC Address : " + MacAddr + "\n\n\t\tRSSI : " + rssi + " dbm \n\n\t\tPercentage : " + level + " %");
        progressStatus = level;
        percentage.setProgress(progressStatus);
        percent.setText(level+" %");

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(MainActivity.this, "Wifi disabled\nPlease enable wifi to check the wifi strength!", Toast.LENGTH_SHORT).show();
        }

    }
    void store(){
        File file = new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "WiFiStrengthDetector.txt");
        System.out.println(file.toString());
        Date currentTime = Calendar.getInstance().getTime();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        try {
            OutputStreamWriter file_writer = new OutputStreamWriter(new FileOutputStream(file, true));
            file_writer.write("############\n" + currentTime + "\n" + strength.getText().toString() + "\n");
            Toast.makeText(MainActivity.this, "File Updated", Toast.LENGTH_SHORT).show();
            file_writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
