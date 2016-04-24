package com.sevenstringargs.rost_gateway;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("ssa", "Starting activity");

        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);

/*
        String ssid = getString(R.string.wifiName);
        String pass = getString(R.string.wifiPass);
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid + "\"";
        wc.preSharedKey= "\"" + pass + "\"";
        int id = manager.addNetwork(wc);
        manager.enableNetwork(id, true);
        manager.reconnect();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

*/
        startService(new Intent(this, GatewayService.class));
    }
}
