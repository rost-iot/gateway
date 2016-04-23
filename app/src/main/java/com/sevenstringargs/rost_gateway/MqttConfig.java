package com.sevenstringargs.rost_gateway;

import java.util.HashMap;

public class MqttConfig extends HashMap<String, String> {
    public static final String ORG_ID = "ORG_ID";
    public static final String BROKER_URL = "BROKER_URL";
    public static final String FULL_BROKER_URL = "FULL_BROKER_URL";
    public static final String DEVICE_TYPE = "DEVICE_TYPE";
    public static final String DEVICE_ID = "DEVICE_ID";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";

    public MqttConfig(){
        put(ORG_ID, "8hlv7w");
        put(BROKER_URL, String.format("tcp://%s.messaging.internetofthings.ibmcloud.com", get(ORG_ID)));
        put(FULL_BROKER_URL, String.format("%s:%s", get(BROKER_URL), "1883"));
        put(DEVICE_TYPE, "Rost-gateway");
        put(DEVICE_ID, "gateway2");
        put(CLIENT_ID, String.format("g:%s:%s:%s", get(ORG_ID), get(DEVICE_TYPE), get(DEVICE_ID)));
        put(USERNAME, "use-token-auth");
        put(PASSWORD, "xb+oz_zoNiof3?m*F_");
    }
}
