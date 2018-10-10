package es.source.code.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.scos.R;

import org.w3c.dom.Text;

import es.source.code.model.Food;
import es.source.code.model.FoodType;

public class FoodDetail extends AppCompatActivity implements View.OnTouchListener{

    //手势检测,在ontouch中调用
    private GestureDetector gestureDetector;
    private LinearLayout linearLayout;
    private int Foodindex;

    private ImageView imageView_foodimg;
    private TextView textView_foodname;
    private TextView textView_foodprice;
    private EditText editText_foodnote;
    private Button button_orderornot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //if (getSupportActionBar() != null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_food_detail);

        gestureDetector=new GestureDetector(new gestureListener());
        linearLayout=(LinearLayout)findViewById(R.id.fooddetail);
        linearLayout.setOnTouchListener(this);
        imageView_foodimg=(ImageView)findViewById(R.id.fooddetail_img);
        textView_foodname=(TextView)findViewById(R.id.fooddetail_foodname);
        textView_foodprice=(TextView)findViewById(R.id.fooddetail_foodprice);
        editText_foodnote=(EditText)findViewById(R.id.fooddetail_foodnoteedit);
        button_orderornot=(Button)findViewById(R.id.fooddetail_orderornot);


        Intent intent=getIntent();
        Foodindex=intent.getIntExtra(FoodView.foodposition,0);
        showFood(Foodindex);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class gestureListener implements GestureDetector.OnGestureListener{
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1.getX() - e2.getX() >20){
                if(Foodindex<FoodView.foods.size()-1) Foodindex++;
            }
            if(e2.getX() - e1.getX() >20){
                if(Foodindex>0) Foodindex--;
            }
            showFood(Foodindex);
            return false;
        }


    }

    void showFood(int foodindex){
        final Food food= FoodView.foods.get(foodindex);
        imageView_foodimg.setImageResource(food.getFood_img());
        textView_foodname.setText("名称:"+food.getFood_name());
        textView_foodprice.setText("价格:"+food.getFood_price());

        if(food.getFood_order_time()>0) button_orderornot.setText("退订!");
        else button_orderornot.setText("点菜!");

        button_orderornot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count=food.getFood_order_time();
                if(food.getFood_order_time()>0){
                    food.setFood_order_time(--count);
                    button_orderornot.setText("点菜!");
                }
                else{
                    food.setFood_order_time(++count);
                    button_orderornot.setText("退订!");
                }
                Toast.makeText(FoodDetail.this, "点菜次数"+food.getFood_order_time(), Toast.LENGTH_SHORT).show();
                //刷新数据
                FoodView.nofityall();
            }
        });


    }
}
