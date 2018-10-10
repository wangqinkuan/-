package es.source.code.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.List;

import es.source.code.activity.FoodOrderView;
import es.source.code.model.Food;
import es.source.code.model.FoodType;

public class DrinkFoodItemAdpater extends ArrayAdapter<Food> {

    private int resourceid;

    public DrinkFoodItemAdpater(@NonNull Context context, int resource, @NonNull List<Food> objects) {
        super(context, resource, objects);
        resourceid=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Food food=getItem(position);
        //获取相应控件,将其值设置为food中的值
        //获取
        View view;
        final ViewHolder viewHolder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceid,null);
            viewHolder=new ViewHolder();
            viewHolder.foodimage=(ImageView) view.findViewById(R.id.food_img);
            viewHolder.foodname=(TextView)view.findViewById(R.id.food_name);
            viewHolder.foodprice=(TextView)view.findViewById(R.id.food_price);
            viewHolder.foodorderbutton=(Button)view.findViewById(R.id.food_order_button);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        if(food.getFood_type()== FoodType.Drink){
            viewHolder.foodimage.setImageResource(food.getFood_img());
            viewHolder.foodname.setText("菜名:"+food.getFood_name());
            viewHolder.foodprice.setText("价格:"+food.getFood_price());

            final int ordertime=food.getFood_order_time();

            if(ordertime>0) viewHolder.foodorderbutton.setText("退订");
            else viewHolder.foodorderbutton.setText("点菜");

            viewHolder.foodorderbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(food.getFood_order_time()<1){
                        int i=food.getFood_order_time()+1;
                        food.setFood_order_time(i);
                        viewHolder.foodorderbutton.setText("退订");
                    }else{
                        int i=food.getFood_order_time()-1;
                        food.setFood_order_time(i);
                        food.setFood_hasorder(false);
                        viewHolder.foodorderbutton.setText("点菜");
                    }
                    notifyDataSetChanged();

                    Toast.makeText(getContext(), "点菜次数"+food.getFood_order_time(), Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            viewHolder.foodimage.setVisibility(View.GONE);
            viewHolder.foodname.setVisibility(View.GONE);
            viewHolder.foodprice.setVisibility(View.GONE);
            viewHolder.foodorderbutton.setVisibility(View.GONE);
        }


        return view;
    }



    class ViewHolder{
        ImageView foodimage;
        TextView foodname,foodprice;
        Button foodorderbutton;
    }
}
