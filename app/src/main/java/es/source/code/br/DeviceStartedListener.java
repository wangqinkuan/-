package es.source.code.br;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import es.source.code.service.UpdateService;

public class DeviceStartedListener extends BroadcastReceiver {

    public DeviceStartedListener(){
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //开启通知更新
        Log.i("DeviceStartedListener", "onReceive: 开机启动");
        Intent startupdateservice=new Intent(context.getApplicationContext(),UpdateService.class);
        context.startService(startupdateservice);
    }
}
