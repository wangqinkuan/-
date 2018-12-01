package es.source.code.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.myapp.scos.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import es.source.code.factory.UserFactory;
import es.source.code.model.User;

public class LoginOrRegister extends AppCompatActivity implements View.OnClickListener{
    SharedPreferences sp;

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

    private Handler handler=new Handler(){
      public void handleMessage(Message msg){
          switch (msg.what){
              //登录失败
              case 0:
                  Toast.makeText(LoginOrRegister.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                  break;
                  //登录成功
              case 1:
                  Toast.makeText(LoginOrRegister.this, "登录成功", Toast.LENGTH_SHORT).show();

                  User user= UserFactory.createuser(editText_username.getText().toString(),editText_password.getText().toString(),true);
                  startMain(LoginStatus,LoginSuccess,loginorregister_loginsucuesscode,user);
                  break;
                  //注册失败
              case 3:
                  Toast.makeText(LoginOrRegister.this, "用户名以及存在", Toast.LENGTH_SHORT).show();
                  break;
              case 4:
                  Toast.makeText(LoginOrRegister.this, "注册成功", Toast.LENGTH_SHORT).show();

                  User newuser= UserFactory.createuser(editText_username.getText().toString(),editText_password.getText().toString(),true);
                  startMain(LoginStatus,RegisterSuccess,loginorregister_loginsucuesscode,newuser);
                  break;
          }
      }
    };

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt("loginState",0);
        editor.commit();
        startMain(Backstatus,Return,loginorregister_backcode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_or_register);


        //初始化sharedpreferences对象
        sp = getSharedPreferences("User", MODE_PRIVATE);


        button_login=(Button)findViewById(R.id.Button_login);
        button_register=(Button)findViewById(R.id.Button_signin);
        button_back=(Button)findViewById(R.id.Button_back);
        progressBar=(ProgressBar)findViewById(R.id.ProgressBar_loginorregister);
        editText_username=(EditText)findViewById(R.id.EditText_username);
        editText_password=(EditText)findViewById(R.id.EditText_password);


        button_login.setOnClickListener(this);
        button_register.setOnClickListener(this);
        button_back.setOnClickListener(this);


        //读取sharedpreferences,如果存在数据,则隐藏注册按钮
        String u=sp.getString("username","");
        String p=sp.getString("password","");
        Log.d("pre", "addUserdataToSharedPreferences: "+u+p);
        if(!u.equals("")&&!p.equals("")){
            editText_username.setText(u);
            editText_password.setText(p);
            button_register.setVisibility(View.GONE);

        }


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
                    addUserdataToSharedPreferences(username,password,1);
                    //startMain(LoginStatus,LoginSuccess,loginorregister_loginsucuesscode,user);
                }else{
                    editText_username.setError(getString(R.string.loginOrRegister_error));
                    editText_password.setError(getString(R.string.loginOrRegister_error));
                }
                sendLoginRequest(username,password);
                //Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
                showProgressBar();
                break;
            case R.id.Button_signin:
                if(validateUserNameorPassword(username,reg)&&validateUserNameorPassword(password,reg)){
                    Log.d("signin", "signin success");
                    User user= UserFactory.createuser(username,password,false);
                    addUserdataToSharedPreferences(username,password,2);

                    //startMain(LoginStatus,RegisterSuccess,loginorregister_loginsucuesscode,user);
                }else{
                    editText_username.setError(getString(R.string.loginOrRegister_error));
                    editText_password.setError(getString(R.string.loginOrRegister_error));
                }
                sendRegisterRequest(username,password);
                break;
            case R.id.Button_back:
                SharedPreferences.Editor editor=sp.edit();
                editor.putInt("loginState",0);
                editor.commit();

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
    //在sharedPreferences中添加用户名密码
    public void addUserdataToSharedPreferences(String username,String password,int state){

        SharedPreferences.Editor editor=sp.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.putInt("loginState",state);
        editor.commit();


        String u=sp.getString("username","null");
        String p=sp.getString("password","null");
        Log.d("pre", "addUserdataToSharedPreferences: "+u+p);
    }

    //提交servlet验证
    public void sendLoginRequest(final String username, final String pasasword){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                StringBuilder stringBuilder = new StringBuilder();

                String requestURL="http://192.168.3.10:8080/SCOSServer/login"+"?"+"username="+username+"&"+"password="+pasasword;
                Log.i("url", requestURL);
                try {
                    //存储返回结果
                    String result = null;
                    String strRead = null;
                    //开始连接
                    URL url = new URL(requestURL);
                    conn = (HttpURLConnection) url.openConnection();
                    //使用Get方式请求数据
                    conn.setRequestMethod("GET");
                    conn.connect();
                    //输入流获取返回数据
                    InputStream is = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    while ((strRead = reader.readLine()) != null) {
                        stringBuilder.append(strRead);
                    }
                    result = stringBuilder.toString();

                    parseLoginJSON(result);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    //注册servlet
    public void sendRegisterRequest(final String username, final String pasasword){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                BufferedReader reader = null;
                StringBuilder stringBuilder = new StringBuilder();

                String requestURL="http://192.168.3.10:8080/SCOSServer/register"+"?"+"username="+username+"&"+"password="+pasasword;
                Log.i("url", requestURL);
                try {
                    //存储返回结果
                    String result = null;
                    String strRead = null;
                    //开始连接
                    URL url = new URL(requestURL);
                    conn = (HttpURLConnection) url.openConnection();
                    //使用Get方式请求数据
                    conn.setRequestMethod("GET");
                    conn.connect();
                    //输入流获取返回数据
                    InputStream is = conn.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    while ((strRead = reader.readLine()) != null) {
                        stringBuilder.append(strRead);
                    }
                    result = stringBuilder.toString();

                    parseLoginJSON(result);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    //解析返回的登录/zhuce结果json,1代表登录成功,0代表失败
    private void parseLoginJSON(String jsonData){
        try{
            Log.i("resultjson", jsonData);
            JSONObject jsonObject=new JSONObject(jsonData);
            int result=jsonObject.getInt("RESULTCODE");
            Message message=new Message();
            message.what=result;
            handler.sendMessage(message);
        }catch (Exception e){

        }
    }
}

