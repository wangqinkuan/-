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
import es.source.code.activity.FoodView;
import es.source.code.model.Food;

public class NotOrderFoodAdapter extends ArrayAdapter<Food>{
    private int resourceid;

    public NotOrderFoodAdapter(@NonNull Context context, int resource, @NonNull List<Food> objects) {
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
        final NotOrderFoodAdapter.ViewHolder viewHolder;
        if (convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceid,null);
            viewHolder=new NotOrderFoodAdapter.ViewHolder();
            viewHolder.foodimage=(ImageView)view.findViewById(R.id.hasnotorderfood_img);
            viewHolder.foodname=(TextView)view.findViewById(R.id.hasnotorderfood_name);
            viewHolder.foodprice=(TextView)view.findViewById(R.id.hasnotorderfood_price);
            viewHolder.foodcount=(TextView)view.findViewById(R.id.hasnotorderfood_count);
            viewHolder.foodnote=(TextView)view.findViewById(R.id.hasnotorderfood_note);
            viewHolder.button_disorder=(Button)view.findViewById(R.id.button_hasnotorderfood_disorder);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder=(NotOrderFoodAdapter.ViewHolder)view.getTag();
        }
        //设置,点菜次数大于0且未下单则在此处显示
        if(food.getFood_order_time()>0&&!food.isFood_hasorder()){
            viewHolder.foodimage.setImageResource(food.getFood_img());
            viewHolder.foodname.setText("菜名"+food.getFood_name());
            viewHolder.foodprice.setText("价格"+food.getFood_price());
            viewHolder.foodcount.setText("份量"+food.getFood_order_time());
            viewHolder.foodnote.setText("备注"+food.getFood_note());
            //退订按钮
            viewHolder.button_disorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    food.setFood_order_time(food.getFood_order_time()-1);
                    FoodView.nofityall();
                    notifyDataSetChanged();
                    FoodOrderView.upodateTotaldata();
                    Toast.makeText(getContext(), "退订", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            viewHolder.foodimage.setVisibility(View.GONE);
            viewHolder.foodname.setVisibility(View.GONE);
            viewHolder.foodprice.setVisibility(View.GONE);
            viewHolder.foodcount.setVisibility(View.GONE);
            viewHolder.foodnote.setVisibility(View.GONE);
            viewHolder.button_disorder.setVisibility(View.GONE);
        }




        return view;
    }



    class ViewHolder{
        ImageView foodimage;
        TextView foodname,foodprice,foodcount,foodnote;
        Button button_disorder;
    }
}
