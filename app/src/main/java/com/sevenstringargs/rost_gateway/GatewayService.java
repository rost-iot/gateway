package com.sevenstringargs.rost_gateway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;

public class GatewayService extends Service {
    public static final String SERVICE_ID = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_ID = "0000ffe1-0000-1000-8000-00805f9b34fb";

    private String orgId = "8hlv7w";
    private String broker = "tcp://" + orgId + ".messaging.internetofthings.ibmcloud.com:1883";
    private String deviceType = "Rost-gateway";
    private String deviceId = "gateway2";
    private String deviceFullId = "g:" + orgId +":" + deviceType + ":" + deviceId;
    private String username = "use-token-auth";
    private String password = "xb+oz_zoNiof3?m*F_";
    private MqttClient client = null;

    public GatewayService() {
    }

    private MqttConnectOptions createConnectionOptions(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        return options;
    }

    private void connectClient(){
        try {
            client = new MqttClient(broker, deviceFullId, null);
            client.connect(createConnectionOptions());
        } catch(MqttException e){
            e.printStackTrace();
            client = null;
        }
    }

    private void disconnectClient(){
        if (client != null){
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

    @Override
    public void onDestroy(){
        disconnectClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate(){
        Log.i("ssa", "Creating");
        final HashMap<String, String> lookup = new HashMap<>();
        HashMap<String, String> macLookup = new HashMap<>();
        macLookup.put("00:15:83:00:54:F9", "lamp");
        lookup.put("lamp", "00:15:83:00:54:F9");
        final BluetoothConnector connector = new BluetoothConnector(this, lookup, macLookup);
        connectClient();

        try {
            client.subscribe("iot-2/type/+/id/+/cmd/+/fmt/json");
        } catch (MqttException e) {
            Log.i("ssa", e.getReasonCode() + "");
            Log.i("ssa", e.getLocalizedMessage());
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.i("ssa", "Got message");
                Log.i("ssa", s);
                try {
                    String[] idAndCmd= getIdAndCmd(s);

                    Log.i("ssa", idAndCmd[0] + " " + idAndCmd[1]);

                    if (lookup.containsKey(idAndCmd[0])){
                        connector.doCommand(lookup.get(idAndCmd[0]), idAndCmd[1]);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                   Log.i("ssa", "Failed to split id and command");
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.i("ssa", "Delivered");
            }
        });

    }

    public String[] getIdAndCmd(String s){
        String dev = "";
        String cmd = "";
        String[] ss = s.split("id/");
        ss = ss[1].split("/cmd/");
        dev = ss[0];
        ss = ss[1].split("/");
        cmd = ss[0];

        return new String[] {dev, cmd};
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class Wrapper {
        public Data d;

        public Wrapper(String value){
            d = new Data();
            d.data = value;
        }
    }
    private class Data {
        public String data;
    }
}
