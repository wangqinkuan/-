package es.source.code.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.ArrayList;
import java.util.List;

import es.source.code.adapter.ColdFoodItemAdapter;
import es.source.code.adapter.DrinkFoodItemAdpater;
import es.source.code.adapter.FoodItemAdapter;
import es.source.code.adapter.FoodViewPaperAdapter;
import es.source.code.adapter.SeaFoodItemAdapter;
import es.source.code.factory.ColdFoodFactory;
import es.source.code.factory.DrinkFactory;
import es.source.code.factory.FoodFactory;
import es.source.code.factory.HotFoodFactory;
import es.source.code.factory.SeaFoodFactory;
import es.source.code.model.Food;
import es.source.code.model.User;

public class FoodView extends AppCompatActivity {
    public static FoodItemAdapter foodItemAdapters;
    public static ColdFoodItemAdapter coldFoodItemAdapter;
    public static SeaFoodItemAdapter seaFoodItemAdapter;
    public static DrinkFoodItemAdpater drinkFoodItemAdpaters;

    public static List<Food> foods;
    public static final String foodposition="foodpostion";
    public static FoodViewPaperAdapter mAdapter;
    //Tab与Viewpager以及填充器
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;

    private View view_coldfood;
    private View view_hotfood;
    private View view_seafood;
    private View view_drink;
    //保存Tab与Viewpager中的数据
    private List<String> mTitleList = new ArrayList<>();
    private List<View> mViewList = new ArrayList<>();

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //if (getSupportActionBar() != null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_food_view);

        //获取用户信息
        Intent intent=getIntent();
        user=(User)intent.getSerializableExtra(LoginOrRegister.User);


        init();
    }

    //导航
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foodview,menu);
        return true;
    }
    //导航点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.foodview_actionbar_hasorder:
                Intent intent=new Intent(this,FoodOrderView.class);
                intent.putExtra(LoginOrRegister.User,user);
                startActivity(intent);
                break;
            case R.id.foodview_actionbar_showorderlist:
                Toast.makeText(this, "看订单", Toast.LENGTH_SHORT).show();
                break;
            case R.id.foodview_actionbar_callforservice:
                Toast.makeText(this, "呼叫服务", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void init() {
        //初始化food数据
        foods = new ArrayList<Food>();
        FoodFactory hotfoodFactory = new HotFoodFactory();
        FoodFactory seafoodFactory = new SeaFoodFactory();
        FoodFactory coldfoodFactory = new ColdFoodFactory();
        FoodFactory drinkfoodFactory = new DrinkFactory();
        for(int i=0;i<2;i++){
            Food hotfood=hotfoodFactory.CreateFood("熟食"+i, i, R.drawable.order);
            Food seafood=seafoodFactory.CreateFood("海鲜"+i, i, R.drawable.order);
            Food coldfood=coldfoodFactory.CreateFood("凉菜"+i, i, R.drawable.order);
            Food drinkfood=drinkfoodFactory.CreateFood("酒水"+i, i, R.drawable.order);

            foods.add(seafood);
            foods.add(coldfood);
            foods.add(drinkfood);
            foods.add(hotfood);
        }



        foodItemAdapters= new FoodItemAdapter(this, R.layout.food_item, foods);
        coldFoodItemAdapter=new ColdFoodItemAdapter(this, R.layout.food_item, foods);
        drinkFoodItemAdpaters=new DrinkFoodItemAdpater(this, R.layout.food_item, foods);
        seaFoodItemAdapter=new SeaFoodItemAdapter(this, R.layout.food_item, foods);

        mViewPager = findViewById(R.id.food_view_pager);
        mTabLayout = findViewById(R.id.food_view_tab);
        mInflater = LayoutInflater.from(this);

        view_hotfood = mInflater.inflate(R.layout.food_view_hotfood, null);
        view_coldfood = mInflater.inflate(R.layout.food_view_coldfood, null);
        view_seafood = mInflater.inflate(R.layout.food_view_seafood, null);
        view_drink = mInflater.inflate(R.layout.food_view_drink, null);

        mViewList.add(view_hotfood);
        mViewList.add(view_coldfood);
        mViewList.add(view_seafood);
        mViewList.add(view_drink);

        mTitleList.add("熟食");
        mTitleList.add("凉菜");
        mTitleList.add("海鲜");
        mTitleList.add("酒水");

        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(3)));
        mAdapter = new FoodViewPaperAdapter(mViewList, mTitleList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);  //将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器


        initFooditem(foodItemAdapters,view_hotfood,R.id.food_listview_hotfood,foods);
        initFooditem(coldFoodItemAdapter,view_coldfood,R.id.food_listview_coldfood,foods);
        initFooditem(seaFoodItemAdapter,view_seafood,R.id.food_listview_seafood,foods);
        initFooditem(drinkFoodItemAdpaters,view_drink,R.id.food_listview_drink,foods);

    }

    //初始化食品数据
    void initFooditem(FoodItemAdapter foodItemAdapter,View foodview,int foodlist,List<Food> foods){
        ListView foodlistView = (ListView) foodview.findViewById(foodlist);
        foodlistView.setAdapter(foodItemAdapter);
        //item被点击后向fooddetail传递其position数据,fooddetail根据position显示对应的食品
        foodlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FoodView.this,FoodDetail.class);
                intent.putExtra(foodposition,position);
                startActivity(intent);
            }
        });
    }

    void initFooditem(ColdFoodItemAdapter foodItemAdapter,View foodview,int foodlist,List<Food> foods){
        ListView foodlistView = (ListView) foodview.findViewById(foodlist);
        foodlistView.setAdapter(foodItemAdapter);
        //item被点击后向fooddetail传递其position数据,fooddetail根据position显示对应的食品
        foodlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FoodView.this,FoodDetail.class);
                intent.putExtra(foodposition,position);
                startActivity(intent);
            }
        });
    }
    void initFooditem(SeaFoodItemAdapter foodItemAdapter,View foodview,int foodlist,List<Food> foods){
        ListView foodlistView = (ListView) foodview.findViewById(foodlist);
        foodlistView.setAdapter(foodItemAdapter);
        //item被点击后向fooddetail传递其position数据,fooddetail根据position显示对应的食品
        foodlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FoodView.this,FoodDetail.class);
                intent.putExtra(foodposition,position);
                startActivity(intent);
            }
        });
    }
    void initFooditem(DrinkFoodItemAdpater foodItemAdapter,View foodview,int foodlist,List<Food> foods){
        ListView foodlistView = (ListView) foodview.findViewById(foodlist);
        foodlistView.setAdapter(foodItemAdapter);
        //item被点击后向fooddetail传递其position数据,fooddetail根据position显示对应的食品
        foodlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(FoodView.this,FoodDetail.class);
                intent.putExtra(foodposition,position);
                startActivity(intent);
            }
        });
    }

    public static void nofityall(){
        foodItemAdapters.notifyDataSetChanged();
        coldFoodItemAdapter.notifyDataSetChanged();
        seaFoodItemAdapter.notifyDataSetChanged();
        drinkFoodItemAdpaters.notifyDataSetChanged();
    }

}
