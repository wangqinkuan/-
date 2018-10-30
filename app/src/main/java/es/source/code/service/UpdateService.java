package es.source.code.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.myapp.scos.R;

import es.source.code.activity.FoodDetail;
import es.source.code.activity.FoodView;
import es.source.code.activity.MainScreen;
import es.source.code.model.Food;

public class UpdateService extends IntentService{

    public UpdateService() {
        super("UpdateService");
        Log.i("UpdateService", "UpdateService constructor");
    }

    @Override
    public void onCreate() {
        Log.i("UpdateService", "UpdateService onCreate");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notifyFoodUpdate();
        Log.i("UpdateService", "UpdateService onHandleIntent");
    }

    //通知菜品
    public void notifyFoodUpdate(){
        //通知
        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setTicker("有新的食品更新");
        mBuilder.setContentTitle("新食品").setContentText("食品更新了").setSmallIcon(R.drawable.ic_logo);
        mBuilder.setAutoCancel(true);

        Intent intent=new Intent(this, FoodDetail.class);
        intent.putExtra(FoodView.foodposition,0);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification=mBuilder.build();
        manager.notify(1003,notification);

    }

}
