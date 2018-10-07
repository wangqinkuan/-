package es.source.code.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class FoodOrderView extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private LayoutInflater layoutInflater;

    private View hasorderview;
    private View hasnotorderview;

    List<String> Titlelist=new ArrayList<>();
    List<View> Pagelist=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //if (getSupportActionBar() != null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_food_order_view);

        init();
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

        initFooditem(hasorderview,R.id.food_orderview_hasorderlist,FoodView.foods,true);
        initFooditem(hasnotorderview,R.id.food_orderview_hasnotorderlist,FoodView.foods,false);

    }
    //指定pageview 以及list的id 还有foods数据 boolean order用于标识是否是已点页面
    void initFooditem(View foodview,int foodlist,List<Food> foods,boolean order){
        ArrayAdapter<Food> adapter;
        if(order) adapter = new OrderFoodAdapter(this, R.layout.food_hasorder_item, foods);
        else  adapter=new NotOrderFoodAdapter(this,R.layout.food_hasnotorder_item,foods);
        ListView foodlistView = (ListView) foodview.findViewById(foodlist);
        foodlistView.setAdapter(adapter);
    }

}
