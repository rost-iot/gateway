package com.sevenstringargs.rost_gateway;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import com.sevenstringargs.bluetoothhelper.BluetoothHelper;
import com.sevenstringargs.bluetoothhelper.OnEndCallback;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothConnector extends BluetoothGattCallback {
    public static final String SERVICE_ID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_ID = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private final Context context;
    private HashMap<String, String> cmdLookup;
    private HashMap<String, BluetoothDevice> foundDevices;
    private HashMap<String, BluetoothGatt> connected;
    private HashMap<String, String> macLookup;
    private HashMap<String, String> deviceLookup;
    private BluetoothAdapter.LeScanCallback callback;
    public BluetoothConnector(Context context, HashMap<String, String> deviceLookup, HashMap<String, String> macLookup){
        this.macLookup = macLookup;
        cmdLookup = new HashMap<>();
        cmdLookup.put("on", "a");
        cmdLookup.put("off", "b");
        cmdLookup.put("small", "a");
        cmdLookup.put("medium", "b");
        cmdLookup.put("large", "c");
        this.context = context;
        connected = new HashMap<>();
        this.deviceLookup = deviceLookup;
        foundDevices = new HashMap<>();
        BluetoothHelper.enable(context);

        createCallback();
        TimerTask discoveryTask = createDiscoveryTask();
        Timer t = new Timer();
        t.scheduleAtFixedRate(discoveryTask, 0, 60000);
    }

    public void doCommand(String address, String cmd){
        if (connected.containsKey(address)){
            BluetoothGatt gatt = connected.get(address);
            BluetoothGattService service = gatt.getService(UUID.fromString(SERVICE_ID));
            if (service == null){
                Log.i("ssa", "No Serivce!!!");
                return;
            }

            BluetoothGattCharacteristic ch = service.getCharacteristic(UUID.fromString(CHAR_ID));

            Log.i("ssa", "Command " + cmd);
            Log.i("ssa", "Address " + address);

            String c = cmdLookup.get(cmd);
            ch.setValue(c);
            gatt.writeCharacteristic(ch);
        }
    }

    private void createCallback(){
        callback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!foundDevices.containsKey(device.getAddress())){
                    foundDevices.put(device.getAddress(), device);
                }
            }
        };
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt device, int status, int newState){
        if (BluetoothGatt.GATT_SUCCESS == status && BluetoothGatt.STATE_CONNECTED == newState) {
            if (!connected.containsKey(device.getDevice().getAddress())){
                connected.put(device.getDevice().getAddress(), device);
                device.discoverServices();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        doCommand("00:15:83:00:54:F9", "off");
                    }
                }, 5000);
            }

        } else if (BluetoothGatt.STATE_DISCONNECTED == newState) {
            connected.remove(device.getDevice().getAddress());
        }
    }

    private void connectToDevices(){
        for (BluetoothDevice d : foundDevices.values()){
            if (macLookup.containsKey(d.getAddress())){
                connectToBluetoothDevice(d);
            }
        }
        connected.clear();
    }

    private void connectToBluetoothDevice(BluetoothDevice d){
        Log.i("ssa", d.getAddress() + " Connecting to this");
        d.connectGatt(context, true, this);
    }

    private TimerTask createDiscoveryTask(){
        TimerTask discoverTask = new TimerTask() {
            @Override
            public void run() {
                BluetoothHelper.discoverBle(context, 4000, callback, new OnEndCallback() {
                    @Override
                    public void done() {
                        Log.i("ssa", "Found");
                        for (BluetoothDevice d : foundDevices.values()){
                            Log.i("ssa", d.getAddress() + " " + d.getName());
                            connectToDevices();
                        }
                    }
                });
            }
        };

        return discoverTask;
    }
}
