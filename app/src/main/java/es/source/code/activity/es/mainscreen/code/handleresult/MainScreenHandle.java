package es.source.code.activity.es.mainscreen.code.handleresult;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;
import java.util.Map;

import es.source.code.activity.LoginOrRegister;
import es.source.code.activity.MainScreen;

public class MainScreenHandle {
    MainScreenHandleResult mainScreenHandleResult;
    public MainScreenHandle(int statuscode){
        if(statuscode==LoginOrRegister.loginorregister_backcode) mainScreenHandleResult=new MainScreenHandleBackResult();
        else if(statuscode==LoginOrRegister.loginorregister_loginsucuesscode) mainScreenHandleResult=new MainScreenHandleLoginResult();
    }
    public void handle(Activity mainscreenactivity, Intent intent){
        mainScreenHandleResult.mainscreenhandleresult(mainscreenactivity,intent);
    }
}
