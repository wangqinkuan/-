package es.source.code.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.ArrayList;
import java.util.List;

import es.source.code.adapter.FoodViewPaperAdapter;
import es.source.code.adapter.NotOrderFoodAdapter;
import es.source.code.adapter.OrderFoodAdapter;
import es.source.code.model.Food;
import es.source.code.model.User;

public class FoodOrderView extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LayoutInflater layoutInflater;
    private OrderFoodAdapter orderFoodAdapter;
    private NotOrderFoodAdapter notOrderFoodAdapter;
    private View hasorderview;
    private View hasnotorderview;
    private ProgressDialog progressDialog;

    List<String> Titlelist=new ArrayList<>();
    List<View> Pagelist=new ArrayList<>();

    ListView orderfoodlistView ;
    ListView notorderfoodlistView ;

    Button button_submitorder,button_buysubmit;
    public static TextView textView_ordertotalprice,textView_ordercount,textView_notordertotalprice,textView_notordercount;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //if (getSupportActionBar() != null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_food_order_view);

        Intent intent=getIntent();
        user=(User)intent.getSerializableExtra(LoginOrRegister.User);

        init();
        upodateTotaldata();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("结账");
        progressDialog.setMessage("支付中,请勿关闭......");
        //    设置setCancelable(false); 表示我们不能取消这个弹出框，等支付完成之后再让弹出框消失
        progressDialog.setCancelable(false);
        //    设置ProgressDialog样式为水平的样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }
    void init(){
        //初始化tablayout与viewpager
        tabLayout=(TabLayout)findViewById(R.id.food_orderview_tab);
        viewPager=(ViewPager)findViewById(R.id.food_orderview_pager);
        layoutInflater=LayoutInflater.from(this);
        //viewpager中的两个界面渲染出来
        hasorderview=layoutInflater.inflate(R.layout.food_orderview_hasorder,null);
        hasnotorderview=layoutInflater.inflate(R.layout.food_orderview_hasnotorder,null);
        //加到page列表中
        Pagelist.add(hasorderview);
        Pagelist.add(hasnotorderview);
        //添加title
        Titlelist.add("已下单菜");
        Titlelist.add("未下单菜");

        tabLayout.addTab(tabLayout.newTab().setText(Titlelist.get(0)));//添加选项卡
        tabLayout.addTab(tabLayout.newTab().setText(Titlelist.get(1)));
        //pagelist与titlelist就绪后适配器
        FoodViewPaperAdapter adapter=new FoodViewPaperAdapter(Pagelist,Titlelist);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);

        textView_ordertotalprice=(TextView)hasorderview.findViewById(R.id.food_orderview_totalprice);
        textView_ordercount=(TextView)hasorderview.findViewById(R.id.food_orderview_totalcount);
        textView_notordertotalprice=(TextView)hasnotorderview.findViewById(R.id.food_notorderview_totalprice);
        textView_notordercount=(TextView)hasnotorderview.findViewById(R.id.food_notorderview_totalcount);

        button_submitorder=(Button)hasnotorderview.findViewById(R.id.button_food_notorderview_submit);
        button_buysubmit=(Button)hasorderview.findViewById(R.id.button_food_orderview_submit);
        //提交订单
        button_submitorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Food food:FoodView.foods){
                    if(food.getFood_order_time()>0&&!food.isFood_hasorder()){
                        food.setFood_hasorder(true);
                    }
                }
                //刷新数据
                orderFoodAdapter.notifyDataSetChanged();
                notOrderFoodAdapter.notifyDataSetChanged();
                //更新总量总价
                upodateTotaldata();
            }
        });
        //结账
        button_buysubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getOldUser()){
                    Toast.makeText(FoodOrderView.this, "您好,老用户7折", Toast.LENGTH_SHORT).show();
                    new MyAsyncTask().execute();
                }
            }
        });

        //添加listview的数据源与数据
        orderFoodAdapter = new OrderFoodAdapter(this, R.layout.food_hasorder_item, FoodView.foods);
        notOrderFoodAdapter=new NotOrderFoodAdapter(this,R.layout.food_hasnotorder_item,FoodView.foods);

        orderfoodlistView = (ListView) hasorderview.findViewById(R.id.food_orderview_hasorderlist);
        notorderfoodlistView = (ListView) hasnotorderview.findViewById(R.id.food_orderview_hasnotorderlist);

        orderfoodlistView.setAdapter(orderFoodAdapter);
        notorderfoodlistView.setAdapter(notOrderFoodAdapter);

    }
    //刷新总价与总量
    public static void upodateTotaldata(){
        int ordertotalcount=0,notordertotalcount=0;
        double ordertotalprice=0,notordertotalprice=0;
        for(Food food:FoodView.foods){
            //未下单
            if(!food.isFood_hasorder()&&food.getFood_order_time()>0){
                notordertotalcount+=food.getFood_order_time();
                notordertotalprice+=food.getFood_price();
            }else if(food.isFood_hasorder()){
                ordertotalcount+=food.getFood_order_time();
                ordertotalprice+=food.getFood_price();
            }
        }

        textView_ordertotalprice.setText("总价"+ordertotalprice);
        textView_ordercount.setText("总量"+ordertotalcount);
        textView_notordertotalprice.setText("总价"+notordertotalprice);
        textView_notordercount.setText("总量"+notordertotalcount);
    }


   public class MyAsyncTask extends AsyncTask<Void,Integer,Void>{

       @Override
       protected Void doInBackground(Void... voids) {
           //设置进度条进度
           for(int i=1;i<=4;i++){
               try {
                   Thread.sleep(1000);
                   //设置进度0~100
                   int progress=i*100/4;
                   Log.d("p", "doInBackground: "+progress);
                   publishProgress(progress);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

           }
           Looper.prepare();
           //修改按钮不可点击并toast
           runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   button_buysubmit.setEnabled(false);
                   Toast.makeText(FoodOrderView.this, "结账金额"+textView_ordertotalprice.getText(), Toast.LENGTH_SHORT).show();

               }
           });
           return null;
       }

       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           //显示进度
           progressDialog.show();
       }
        //完毕后执行
       @Override
       protected void onPostExecute(Void aVoid) {
           super.onPostExecute(aVoid);
           progressDialog.setProgress(0);
           progressDialog.dismiss();
       }
        //更新进度
       @Override
       protected void onProgressUpdate(Integer... values) {
           super.onProgressUpdate(values);
           progressDialog.setProgress(values[0]);
       }
   }

}
