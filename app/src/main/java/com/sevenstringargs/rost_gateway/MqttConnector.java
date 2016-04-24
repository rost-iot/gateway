package com.sevenstringargs.rost_gateway;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttConnector {
    private BluetoothConnector bluetoothConnector;
    private MqttClient client = null;
    private MqttConfig config;

    public MqttConnector(Context context, BluetoothConnector bluetoothConnector) {
        config = new MqttConfig(context);
        this.bluetoothConnector = bluetoothConnector;

        if (!setup()){
           Log.i("ssa", "Setup went wrong, stuff might not work");
        }
    }

    private boolean setup() {
        if (!connectClient()) {
            Log.i("ssa", "Failed to connect mqtt client");
            return false;
        }

        if (!setupCallback()){
            Log.i("ssa", "Failed to setup callback");
            return false;
        }

        if (!setupSub()){
            return false;
        }

        return true;
    }

    private boolean setupSub() {
        try {
            client.subscribe("iot-2/type/+/id/+/cmd/+/fmt/json");
        } catch (MqttException e) {
            Log.i("ssa", e.getReasonCode() + "");
            Log.i("ssa", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean setupCallback() {
        try {
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Log.i("ssa", s);

                    try {
                        String[] idAndCmd = getIdAndCmd(s);
                        if (DeviceStore.getAddress(idAndCmd[0]) != null) {
                            if (bluetoothConnector != null) {
                                bluetoothConnector.doCommand(DeviceStore.getAddress(idAndCmd[0]), idAndCmd[1]);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("ssa", "Failed to split id and command");
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    Log.i("ssa", "Delivered");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    private String[] getIdAndCmd(String s) {
        String dev = "";
        String cmd = "";
        String[] ss = s.split("id/");
        ss = ss[1].split("/cmd/");
        dev = ss[0];
        ss = ss[1].split("/");
        cmd = ss[0];

        return new String[]{dev, cmd};
    }

    private MqttConnectOptions createConnectionOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(config.get(MqttConfig.USERNAME));
        options.setPassword(config.get(MqttConfig.PASSWORD).toCharArray());

        return options;
    }

    public void disconnectClient() {
        if (client != null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }

            try {
                client.close();
            } catch (MqttException e) {
                e.printStackTrace();
            } finally {
                client = null;
            }
        }
    }

    private boolean connectClient() {
        try {
            Log.i("ssa", config.get(MqttConfig.FULL_BROKER_URL));
            client = new MqttClient(config.get(MqttConfig.FULL_BROKER_URL), config.get(MqttConfig.CLIENT_ID), null);
            client.connect(createConnectionOptions());
        } catch (MqttException e) {
            e.printStackTrace();
            client = null;
            return false;
        }

        return true;
    }

}
