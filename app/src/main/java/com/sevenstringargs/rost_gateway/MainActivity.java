package com.sevenstringargs.rost_gateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        String ssid = "axel ibm";
        String pass = "ibmibmibm";
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"" + ssid + "\"";
        wc.preSharedKey= "\"" + pass + "\"";
        WifiManager manager = (WifiManager) getSystemService(WIFI_SERVICE);
        int id = manager.addNetwork(wc);
        manager.enableNetwork(id, true);
*/
        startService(new Intent(this, GatewayService.class));

    }
}
