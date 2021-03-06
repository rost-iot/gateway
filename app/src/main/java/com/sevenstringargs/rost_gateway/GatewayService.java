package com.sevenstringargs.rost_gateway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GatewayService extends Service {
    private BluetoothConnector bluetoothConnector;
    private MqttConnector mqttConnector;

    public GatewayService() {
    }

    private void setupStores(){
        setupCommandStore();
        setupDeviceStore();
        setupBlStateStore();
    }

    private void setupBlStateStore(){
        BlStateStore.clearConnected();
        BlStateStore.clearFound();
    }

    private void setupDeviceStore(){
        DeviceStore.addPairs("00:15:83:00:54:A5", "fan");
        DeviceStore.addPairs("00:15:83:00:54:F9", "lamp");
        DeviceStore.addPairs("00:15:83:00:54:DE", "tap");
    }

    private void setupCommandStore(){
        CommandStore.addPair("lamp-on", "a");
        CommandStore.addPair("lamp-off", "b");
        CommandStore.addPair("tap-small", "a");
        CommandStore.addPair("tap-medium", "b");
        CommandStore.addPair("tap-large", "c");
        CommandStore.addPair("tap-config", "d");
        CommandStore.addPair("fan-low", "a");
        CommandStore.addPair("fan-high", "b");
        CommandStore.addPair("fan-off", "c");
    }

    @Override
    public void onCreate(){
        Log.i("ssa", "Creating");

        setupStores();

        bluetoothConnector= new BluetoothConnector(this);
        mqttConnector = new MqttConnector(this, bluetoothConnector);
    }

    @Override
    public void onDestroy(){
        mqttConnector.disconnectClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
