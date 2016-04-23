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

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothConnector extends BluetoothGattCallback {
    public static final UUID SERVICE_ID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID CHAR_ID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private final Context context;
    private BluetoothAdapter.LeScanCallback callback;
    public BluetoothConnector(Context context){
        this.context = context;
        BluetoothHelper.enable(context);

        createCallback();
        TimerTask discoveryTask = createDiscoveryTask();
        Timer t = new Timer();
        t.scheduleAtFixedRate(discoveryTask, 0, 60000);
    }

    public void doCommand(String address, String cmd){
        if (BlStateStore.getConnected(address) != null){
            BluetoothGatt gatt = BlStateStore.getConnected(address);

            BluetoothGattService service = gatt.getService(SERVICE_ID);
            if (service == null){
                Log.i("ssa", "BLE Service not found, try again");
                gatt.discoverServices();
                return;
            }

            BluetoothGattCharacteristic ch = service.getCharacteristic(CHAR_ID);
            if (ch == null){
                Log.i("ssa", "BLE Characteristic not found, try again");
                gatt.discoverServices();
                return;
            }

            String translatedCommand = CommandStore.getTo(cmd);
            if (translatedCommand == null){
                Log.i("ssa", String.format("No command translation for command %s", cmd));
                return;
            }

            String name = DeviceStore.getName(address);
            if (name == null){
                Log.i("ssa", String.format("No name translation for address %s", address));
                return;
            }

            Log.i("ssa", String.format("Performing command: %s(%s) on device: %s(%s)", cmd, translatedCommand, name, address));

            ch.setValue(translatedCommand);
            gatt.writeCharacteristic(ch);
        }
    }

    private void createCallback(){
        callback = new BluetoothAdapter.LeScanCallback() {

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                BlStateStore.addFound(device);
            }
        };
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt device, int status, int newState){
        if (BluetoothGatt.GATT_SUCCESS == status && BluetoothGatt.STATE_CONNECTED == newState) {
            if (BlStateStore.getConnected(device.getDevice().getAddress()) == null){
                BlStateStore.addConnected(device);
                device.discoverServices();
            }
        } else if (BluetoothGatt.STATE_DISCONNECTED == newState) {
            BlStateStore.removeConnected(device);
        }
    }

    private void connectToDevices(){
        Iterator<BluetoothDevice> iter = BlStateStore.allFound().iterator();
        while(iter.hasNext()){
            BluetoothDevice d = iter.next();
            if (DeviceStore.getName(d.getAddress()) != null){
                connectToBluetoothDevice(d);
            }
        }

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

                        Iterator<BluetoothDevice> iter = BlStateStore.allFound().iterator();
                        while(iter.hasNext()){
                            BluetoothDevice d = iter.next();
                            Log.i("ssa", d.getAddress() + " " + d.getName());
                            connectToDevices();
                        }

                        BlStateStore.clearFound();
                    }
                });
            }
        };

        return discoverTask;
    }
}
