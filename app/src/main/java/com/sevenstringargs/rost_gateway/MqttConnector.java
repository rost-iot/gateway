package com.sevenstringargs.rost_gateway;

import android.content.Context;

public class MqttConnector {
    private Context context;
    private BluetoothConnector bluetoothConnector;

    public MqttConnector(Context context, BluetoothConnector bluetoothConnector){
        this.context = context;
        this.bluetoothConnector = bluetoothConnector;
    }
}
