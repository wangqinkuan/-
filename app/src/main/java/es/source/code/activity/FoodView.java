package es.source.code.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import es.source.code.service.ServerObserverService;
import es.source.code.service.UpdateService;

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

    private static View view_coldfood;
    private static View view_hotfood;
    private static View view_seafood;
    private static View view_drink;
    //保存Tab与Viewpager中的数据
    private List<String> mTitleList = new ArrayList<>();
    private List<View> mViewList = new ArrayList<>();

    private User user;

    private boolean isBound = false;
    //是否更新
    boolean update=true;
    public static final int startupdate=1;
    public static final int stopupdate=0;
    //用于启动MyService的Intent对应的action
    private final String SERVICE_ACTION = "com.ispring2.action.MYSERVICE";


    private static Handler mUIHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==7){
                ListView foodlistView = (ListView) view_hotfood.findViewById(R.id.food_listview_hotfood);
                ListView coldlistView = (ListView) view_coldfood.findViewById(R.id.food_listview_coldfood);
                ListView sealistView = (ListView) view_seafood.findViewById(R.id.food_listview_seafood);
                ListView drinklistView = (ListView) view_drink.findViewById(R.id.food_listview_drink);
                foodlistView.setAdapter(foodItemAdapters);
                coldlistView.setAdapter(coldFoodItemAdapter);
                sealistView.setAdapter(seaFoodItemAdapter);
                drinklistView.setAdapter(drinkFoodItemAdpaters);
                nofityalladapter();
            }
        }
    };


    //serviceMessenger表示的是Service端的Messenger，其内部指向了MyService的ServiceHandler实例
    //可以用serviceMessenger向MyService发送消息
    private Messenger serviceMessenger = null;

    //clientMessenger是客户端自身的Messenger，内部指向了ClientHandler的实例
    //MyService可以通过Message的replyTo得到clientMessenger，从而MyService可以向客户端发送消息，
    //并由ClientHandler接收并处理来自于Service的消息
    private Messenger clientMessenger = new Messenger(new ClientHandler());

    //客户端用ClientHandler接收并处理来自于Service的消息
    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ServerObserverService.SEND_FOOD_INFO) {
                    Bundle data = msg.getData();
                    if (data != null) {
//                        String str = data.getString("msg");
//                        int count = data.getInt("count");
//                        //更新
//                        for (Food food : foods) {
//                            if (food.getFood_name().equals(str)) {
//                                food.setFood_reserve(count);
//                            }
//                        }
//                        synchronized (Thread.currentThread()){
//                            FoodView.nofityalladapter();
//                        }
//
//                        Log.i("DemoLog", "客户端收到新的食品信息: " + str + "存量" + count);
                    }
                }
            }
        }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            //客户端与Service建立连接
            //我们可以通过从Service的onBind方法中返回的IBinder初始化一个指向Service端的Messenger
            serviceMessenger = new Messenger(binder);
            Message msg = Message.obtain();
            msg.what = startupdate;
            //此处跨进程Message通信不能将msg.obj设置为non-Parcelable的对象，应该使用Bundle
            //需要将Message的replyTo设置为客户端的clientMessenger，
            //以便Service可以通过它向客户端发送消息
            msg.replyTo = clientMessenger;
            try {
                Log.i("DemoLog", "客户端向service发送信息");
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.i("DemoLog", "客户端向service发送消息失败: " + e.getMessage());
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //客户端与Service失去连接
            serviceMessenger = null;
            isBound = false;
            Log.i("DemoLog", "客户端 onServiceDisconnected");
        }
    };

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
        //是否实时更新

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

                //开始实时更新,绑定服务,发送message.what=1给服务
            case R.id.foodview_actionbar_startupdate:
                if(update) {
                    //绑定更新服务
                    Intent bindintent = new Intent(this, ServerObserverService.class);
                    bindService(bindintent, conn, BIND_AUTO_CREATE);
                    //开启通知更新
                    Intent startupdateservice=new Intent(this, UpdateService.class);
                    startService(startupdateservice);
                    //发送消息,1表示开始更新
                    if (serviceMessenger != null) {
                        Message msg = Message.obtain();
                        msg.what = startupdate;
                        try {
                            serviceMessenger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    item.setTitle("停止实时更新");
                    update=false;
                }else{
                    //发送消息 0表示停止更新
                    if (serviceMessenger!=null){
                        Message msg = Message.obtain();
                        msg.what = stopupdate;
                        try {
                            serviceMessenger.send(msg);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    update=true;
                    item.setTitle("开始实时更新");
                }
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
        for(int i=1;i<10;i++){
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
    //通知四种类型的服务刷新数据
    public static void nofityalladapter(){
        foodItemAdapters.notifyDataSetChanged();
        coldFoodItemAdapter.notifyDataSetChanged();
        seaFoodItemAdapter.notifyDataSetChanged();
        drinkFoodItemAdpaters.notifyDataSetChanged();
    }

    public static void updateUI(Message message) {
        mUIHandler.sendMessage(message);
    }

}
