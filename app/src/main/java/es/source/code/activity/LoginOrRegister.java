package es.source.code.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.myapp.scos.R;

import java.util.regex.Pattern;

import es.source.code.factory.UserFactory;
import es.source.code.model.User;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener{
    public static final int loginorregister_backcode=101;
    public static final int loginorregister_loginsucuesscode=102;

    //intent中 putExtra传递的值
    public static final String LoginStatus="LoginStatus";
    public static final String LoginSuccess="LoginSuccess";
    public static final String RegisterSuccess="RegisterSuccess";
    public static final String Backstatus="Backstatus";
    public static final String Return="Return";
    public static final String User="user";

    private Button button_login;
    private Button button_register;
    private Button button_back;
    private ProgressBar progressBar;
    private EditText editText_username;
    private EditText editText_password;

    @Override
    public void onBackPressed() {
        startMain(Backstatus,Return,loginorregister_backcode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_or_register);






        button_login=(Button)findViewById(R.id.Button_login);
        button_register=(Button)findViewById(R.id.Button_signin);
        button_back=(Button)findViewById(R.id.Button_back);
        progressBar=(ProgressBar)findViewById(R.id.ProgressBar_loginorregister);
        editText_username=(EditText)findViewById(R.id.EditText_username);
        editText_password=(EditText)findViewById(R.id.EditText_password);


        button_login.setOnClickListener(this);
        button_register.setOnClickListener(this);
        button_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final String reg="[a-zA-Z0-9]+";
        String username=editText_username.getText().toString();
        String password=editText_password.getText().toString();
        switch (v.getId()){
            case R.id.Button_login:
                //通过正则验证 则跳转
                if(validateUserNameorPassword(username,reg)&&validateUserNameorPassword(password,reg)){
                    User user= UserFactory.createuser(username,password,true);
                    startMain(LoginStatus,LoginSuccess,loginorregister_loginsucuesscode,user);
                }else{
                    editText_username.setError(getString(R.string.loginOrRegister_error));
                    editText_password.setError(getString(R.string.loginOrRegister_error));
                }

                showProgressBar();
                break;
            case R.id.Button_signin:
                if(validateUserNameorPassword(username,reg)&&validateUserNameorPassword(password,reg)){
                    Log.d("signin", "signin success");
                    User user= UserFactory.createuser(username,password,false);
                    startMain(LoginStatus,RegisterSuccess,loginorregister_loginsucuesscode,user);
                }else{
                    editText_username.setError(getString(R.string.loginOrRegister_error));
                    editText_password.setError(getString(R.string.loginOrRegister_error));
                }
                break;
            case R.id.Button_back:
                startMain(Backstatus,Return,loginorregister_backcode);
                break;

        }
    }

    public void showProgressBar(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });

                try {
                    Log.d("thread", "run: sleep 2000");
                    Thread.sleep(2000);
                    Log.d("thread", "run: wake up");
                }catch (Exception e){
                    System.out.print(e.toString());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        }).start();
    }
    //用正则验证账号密码
    public boolean validateUserNameorPassword(String string,String reg){
        Pattern p=Pattern.compile(reg);
        if(p.matcher(string).matches()) return true;
        return false;
    }
    //打开main并且传值
    public void startMain(String intentkey,String intentstring,int resultcode){
        Intent intent=new Intent(LoginOrRegister.this,MainScreen.class);
        intent.putExtra(intentkey,intentstring);
        setResult(resultcode,intent);
        finish();
    }
    //打开main传递值,带有对象
    public void startMain(String intentkey,String intentstring,int resultcode,User user){
        Intent intent=new Intent(LoginOrRegister.this,MainScreen.class);
        intent.putExtra(intentkey,intentstring);
        //通过序列化传值,注意这里的User是字符串
        intent.putExtra(User,user);
        setResult(resultcode,intent);
        finish();
    }
}
