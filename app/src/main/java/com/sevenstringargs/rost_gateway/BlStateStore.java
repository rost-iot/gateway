package com.sevenstringargs.rost_gateway;

import android.bluetooth.BluetoothGatt;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class BlStateStore {
    private static HashMap<String, BluetoothGatt> connected = new HashMap<>();
    private static Set<String> found = new TreeSet<>();

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

    public synchronized static void addFound(String address){
        if (address == null){
            return;
        }

        if (found.contains(address)){
            return;
        }

        found.add(address);
    }

    public synchronized static void removeFound(String address){
        if (address == null){
            return;
        }

        if (!found.contains(address)){
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
}
//00:15:83:00:54:A5
