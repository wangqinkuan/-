package es.source.code.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import es.source.code.activity.FoodView;
import es.source.code.activity.SCOSHelper;

public class ServerObserverService extends Service{
    private Messenger serviceMessenger = new Messenger(new ServiceHandler());

    private Messenger clientMessenger = null;

    //private static final int RECEIVE_MESSAGE_CODE = 1;

    public static final int SEND_FOOD_INFO = 10;
    //标志线程是否结束
    boolean isexit=false;

    private Thread thread;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        thread=new Thread(new myThread());
        return serviceMessenger.getBinder();
    }

    //判断活动是否在运行状态
    public static boolean isRunningApp(Context context, String packageName) {
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        //判断是否在栈里
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        return isAppRunning;
    }


    private class ServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //收到1,启动线程,开始更新
            if (msg.what == FoodView.startupdate) {
                isexit=false;
                thread.start();
                //通过Message的replyTo获取到客户端自身的Messenger，
                //Service可以通过它向客户端发送消息
                clientMessenger = msg.replyTo;
            }
            //停止更新,线程停止
            else if(msg.what== FoodView.stopupdate){
                if(thread!=null){
                    isexit=true;
                    Log.i("DemoLog", "服务线程停止,线程状态:"+thread.getState());
                }
            }
        }


    }


    class myThread implements Runnable{
        @Override
        public void run() {
            while(isexit==false){
                try {
                    Log.i("DemoLog", "服务线程运行,线程状态:"+thread.getState());
                    if(isRunningApp(getApplicationContext(),"com.myapp.scos")){
                        if (clientMessenger != null) {
                            Message msgToClient = Message.obtain();
                            msgToClient.what = SEND_FOOD_INFO;
                            //可以通过Bundle发送跨进程的信息
                            Bundle bundle = new Bundle();
                            //名称/库存
                            bundle.putString("msg", "熟食"+(int)(Math.random()*10+1));
                            bundle.putInt("count",20);
                            msgToClient.setData(bundle);
                            try {
                                clientMessenger.send(msgToClient);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                Log.e("DemoLog", "MyService向客户端发送信息失败: " + e.getMessage());
                            }
                        }
                    }
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}



