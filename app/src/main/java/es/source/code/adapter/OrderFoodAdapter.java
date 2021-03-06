package es.source.code.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

import es.source.code.model.Food;

public class OrderFoodAdapter extends ArrayAdapter<Food> {
    private int resourceid;

    public OrderFoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> objects) {
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
        final OrderFoodAdapter.ViewHolder viewHolder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceid,null);
            viewHolder=new OrderFoodAdapter.ViewHolder();
            viewHolder.foodimage=(ImageView)view.findViewById(R.id.hasorderfood_img);
            viewHolder.foodname=(TextView)view.findViewById(R.id.hasorderfood_name);
            viewHolder.foodprice=(TextView)view.findViewById(R.id.hasorderfood_price);
            viewHolder.foodcount=(TextView)view.findViewById(R.id.hasorderfood_count);
            viewHolder.foodnote=(TextView)view.findViewById(R.id.hasorderfood_note);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(OrderFoodAdapter.ViewHolder)view.getTag();
        }
        //未下单,则全部隐藏
        if(!food.isFood_hasorder()){
            viewHolder.foodimage.setVisibility(View.GONE);
            viewHolder.foodname.setVisibility(View.GONE);
            viewHolder.foodprice.setVisibility(View.GONE);
            viewHolder.foodcount.setVisibility(View.GONE);
            viewHolder.foodnote.setVisibility(View.GONE);
        }else{
            //已下单,则先设置可视,因为在一个界面中,先设置了隐藏,即使刷新了数据,还是显示不出来
            viewHolder.foodimage.setVisibility(View.VISIBLE);
            viewHolder.foodname.setVisibility(View.VISIBLE);
            viewHolder.foodprice.setVisibility(View.VISIBLE);
            viewHolder.foodcount.setVisibility(View.VISIBLE);
            viewHolder.foodnote.setVisibility(View.VISIBLE);


            viewHolder.foodimage.setImageResource(food.getFood_img());
            viewHolder.foodname.setText(food.getFood_name());
            viewHolder.foodprice.setText("价格:"+food.getFood_price());
            viewHolder.foodcount.setText("份量:"+food.getFood_order_time());
            viewHolder.foodnote.setText("备注:"+food.getFood_note());
        }


        return view;
    }



    class ViewHolder{
        ImageView foodimage;
        TextView foodname,foodprice,foodcount,foodnote;
    }
}
