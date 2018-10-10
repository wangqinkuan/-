package es.source.code.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.source.code.activity.es.mainscreen.code.handleresult.MainScreenHandle;
import es.source.code.factory.UserFactory;
import es.source.code.model.User;

public class MainScreen extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    //状态码 登录界面的
    private final int logincode=100;

//    Button button_order;
//    Button button_listorder;
//    Button button_loginorsignin;
//    Button button_help;
    private GridView gridView;
    public SimpleAdapter adapter;
    public List<Map<String,Object>> datalist;


    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_screen);




        /*
        1.准备数据源； 2.新建适配器； 3.GridView加载适配器；4.GridView配置事件监听器
         */
        gridView=(GridView)findViewById(R.id.gridview);
        initdata();
        String[] from={"img","text"};
        int[] to={R.id.mainscreen_gridview_img,R.id.mainscreen_gridview_text};
        adapter=new SimpleAdapter(this,datalist,R.layout.gridview_item,from,to);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);


//        button_order=(Button)findViewById(R.id.Button_order);
//        button_listorder=(Button)findViewById(R.id.Button_listorder);
//        button_loginorsignin=(Button)findViewById(R.id.Button_loginOrSignin);
//        button_help=(Button)findViewById(R.id.Button_help);
//
//        button_loginorsignin.setOnClickListener(this);

        Intent intent=getIntent();

        if(!intent.getStringExtra(SCOSEntry.FromEntry).equals(SCOSEntry.FromEntry)){
//            button_order.setVisibility(View.GONE);
//            button_listorder.setVisibility(View.GONE);

            //从数据源移除0/1项即可不显示
            datalist.remove(0);
            datalist.remove(0);
            adapter.notifyDataSetChanged();
        }


    }

      @Override
       public void onClick(View v) {
//        switch(v.getId()){
//            case R.id.Button_loginOrSignin:
//                startActivityForResult(new Intent(this,LoginOrRegister.class),logincode);
//                break;
//        }
     }


    //取得返回结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //策略模式,处理login界面返回的数据
//        MainScreenHandle m= new MainScreenHandle(resultCode);
//        m.handle(this,data);
        if(resultCode==LoginOrRegister.loginorregister_loginsucuesscode){
            String loginorregisterstatus=data.getStringExtra(LoginOrRegister.LoginStatus);
            if(loginorregisterstatus.equals(LoginOrRegister.LoginSuccess)){
                user=(User) data.getSerializableExtra(LoginOrRegister.User);
                Toast.makeText(this, user.getUsername()+"登录成功", Toast.LENGTH_SHORT).show();
            }
            else if(loginorregisterstatus.equals(LoginOrRegister.RegisterSuccess)){
                user=(User) data.getSerializableExtra(LoginOrRegister.User);
                Toast.makeText(this, user.getUsername()+"注册成功,欢迎成为SCOS新用户", Toast.LENGTH_SHORT).show();
            }else {
                user=null;
            }
            //显示隐藏的导航项
            if(datalist.size()<4){
                Map<String,Object> map1=new HashMap<String ,Object>();
                map1.put("img",R.drawable.order);
                map1.put("text","点菜");

                Map<String,Object> map2=new HashMap<String ,Object>();
                map2.put("img",R.drawable.list);
                map2.put("text","查看订单");


                datalist.add(0,map1);
                datalist.add(1,map2);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initdata(){
        int icon[]={R.drawable.order,R.drawable.list,R.drawable.login,R.drawable.help};
        String name[]={"点菜","查看订单","登录/注册","帮助"};
        datalist=new ArrayList<Map<String,Object>>();
        for(int i=0;i<icon.length;i++){
            Map<String,Object> map=new HashMap<String ,Object>();
            map.put("img",icon[i]);
            map.put("text",name[i]);
            datalist.add(map);
        }
    }

    //gridview按下事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView= (TextView)view.findViewById(R.id.mainscreen_gridview_text);
        Toast.makeText(this, textView.getText().toString(), Toast.LENGTH_SHORT).show();
        String itemtext=textView.getText().toString();
        switch (itemtext){
            case "登录/注册":
                startActivityForResult(new Intent(this,LoginOrRegister.class),logincode);
                break;
            case "点菜":
                Intent intent=new Intent(this,FoodView.class);
                intent.putExtra(LoginOrRegister.User,user);
                startActivity(intent);
                break;
        }
    }
}
