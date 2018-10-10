package es.source.code.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.ArrayList;
import java.util.List;

import es.source.code.adapter.FoodItemAdapter;
import es.source.code.adapter.FoodViewPaperAdapter;
import es.source.code.adapter.NotOrderFoodAdapter;
import es.source.code.adapter.OrderFoodAdapter;
import es.source.code.factory.FoodFactory;
import es.source.code.factory.HotFoodFactory;
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
        button_submitorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Food food:FoodView.foods){
                    if(food.getFood_order_time()>0&&!food.isFood_hasorder()){
                        food.setFood_hasorder(true);
                    }
                }
                orderFoodAdapter.notifyDataSetChanged();
                notOrderFoodAdapter.notifyDataSetChanged();
                upodateTotaldata();
            }
        });

        button_buysubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.getOldUser()){
                    Toast.makeText(FoodOrderView.this, "您好,老用户7折", Toast.LENGTH_SHORT).show();
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

}
