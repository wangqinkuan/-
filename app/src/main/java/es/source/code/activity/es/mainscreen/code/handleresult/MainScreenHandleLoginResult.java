package es.source.code.activity.es.mainscreen.code.handleresult;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.HashMap;
import java.util.Map;

import es.source.code.activity.LoginOrRegister;
import es.source.code.activity.MainScreen;
import es.source.code.model.User;


public class MainScreenHandleLoginResult implements MainScreenHandleResult{

    @Override
    public void mainscreenhandleresult(Activity mainscreenactivity,Intent intent) {
        Log.d("login", intent.getStringExtra(LoginOrRegister.LoginStatus));
        //查看点菜和查看订单是否为隐藏,隐藏则设置为显示
//        Button button_order=(Button)mainscreenactivity.findViewById(R.id.Button_order);
//        Button button_list=(Button)mainscreenactivity.findViewById(R.id.Button_listorder);
//        if(button_order.getVisibility()==View.GONE) button_order.setVisibility(View.VISIBLE);
//        if(button_list.getVisibility()==View.GONE) button_list.setVisibility(View.VISIBLE);



        MainScreen mainScreen=(MainScreen)mainscreenactivity;

        //检查返回的数据,根据返回值做相应操作
        String loginorregisterstatus=intent.getStringExtra(LoginOrRegister.LoginStatus);
        if(loginorregisterstatus.equals(LoginOrRegister.LoginSuccess)){
            mainScreen.user=(User) intent.getSerializableExtra(LoginOrRegister.User);
            Toast.makeText(mainScreen, mainScreen.user.getUsername()+"登录成功", Toast.LENGTH_SHORT).show();
        }
        else if(loginorregisterstatus.equals(LoginOrRegister.RegisterSuccess)){
            mainScreen.user=(User) intent.getSerializableExtra(LoginOrRegister.User);
            Toast.makeText(mainScreen, mainScreen.user.getUsername()+"注册成功", Toast.LENGTH_SHORT).show();
        }else {
            mainScreen.user=null;
        }





        //如果小于4 说明点菜与订单被隐藏,添加之
        if(mainScreen.datalist.size()<4){
            Map<String,Object> map1=new HashMap<String ,Object>();
            map1.put("img",R.drawable.ic_logo);
            map1.put("text","点菜");

            Map<String,Object> map2=new HashMap<String ,Object>();
            map2.put("img",R.drawable.ic_logo);
            map2.put("text","查看订单");


            mainScreen.datalist.add(0,map1);
            mainScreen.datalist.add(1,map2);
            mainScreen.adapter.notifyDataSetChanged();
        }




    }
}
