package com.sevenstringargs.rost_gateway;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import java.util.Collection;
import java.util.HashMap;

public class BlStateStore {
    private static HashMap<String, BluetoothGatt> connected = new HashMap<>();
    private static HashMap<String, BluetoothDevice> found = new HashMap<>();

    public synchronized static void clearConnected(){
        connected.clear();
    }

    public synchronized static void clearFound(){
        found.clear();
    }

    public synchronized static void addConnected(BluetoothGatt device){
        if (device == null){
            return;
        }

        String address = device.getDevice().getAddress();
        if (connected.containsKey(address)){
            return;
        }

        connected.put(address, device);
    }

    public synchronized static void addFound(BluetoothDevice device){
        if (device == null){
            return;
        }

        if (found.containsKey(device.getAddress())){
            return;
        }

        found.put(device.getAddress(), device);
    }

    public synchronized static void removeFound(String address){
        if (address == null){
            return;
        }

        if (!found.containsKey(address)){
           return;
        }

        found.remove(address);
    }

    public synchronized static void removeConnected(BluetoothGatt device){
        if (device == null){
            return;
        }

        removeConnected(device.getDevice().getAddress());
    }

    public synchronized static void removeConnected(String address){
        if (address == null){
            return;
        }

        if (!connected.containsKey(address)){
            return;
        }
    }

    public synchronized static BluetoothGatt getConnected(String address){
        if (address == null){
            return null;
        }

        return connected.get(address);
    }

    public synchronized static BluetoothDevice getFound(String address){
        if (address == null){
            return null;
        }

        return found.get(address);
    }

    public synchronized static Collection<BluetoothDevice> allFound(){
        return found.values();
    }
}
