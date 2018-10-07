package es.source.code.activity.es.mainscreen.code.handleresult;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;

import com.myapp.scos.R;

import es.source.code.activity.LoginOrRegister;

public class MainScreenHandleBackResult implements MainScreenHandleResult{
    @Override
    public void mainscreenhandleresult(Activity mainscreenactivity, Intent intent) {

        Log.d("back", intent.getStringExtra(LoginOrRegister.Backstatus));



    }
}
