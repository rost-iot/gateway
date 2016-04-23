package com.sevenstringargs.rost_gateway;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GatewayService extends Service {
    public GatewayService() {
    }

    @Override
    public void onCreate(){

        Log.i("ssa", "Creating");
    }

    @Override
    public void onDestroy(){
        Log.i("ssa", "Destroying");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
