package com.sevenstringargs.rost_gateway;

import java.util.HashMap;

public class DeviceStore {
    private static HashMap<String, String> byAddress = new HashMap<>();
    private static HashMap<String, String> byName = new HashMap<>();

    public synchronized static void clearPair(){
        clearByAddress();
        clearByName();
    }

    public synchronized static void clearByAddress(){
        byAddress.clear();
    }

    public synchronized static void clearByName(){
        byName.clear();
    }
    public synchronized static void addPairs(String address, String name){
        if (!byAddress.containsKey(address) && !byName.containsKey(name) && !address.equals(name)){
            byAddress.put(address, name);
            byName.put(name, address);
        }
    }

    public synchronized static String getName(String address){
        return byAddress.get(address);
    }

    public synchronized static String getAddress(String name){
        return byName.get(name);
    }
}
