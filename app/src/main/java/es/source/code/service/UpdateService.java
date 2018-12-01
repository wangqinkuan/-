package es.source.code.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.myapp.scos.R;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import es.source.code.activity.FoodDetail;
import es.source.code.activity.FoodView;
import es.source.code.activity.MainScreen;
import es.source.code.model.Food;
import es.source.code.model.FoodType;

public class UpdateService extends IntentService{

    public UpdateService() {
        super("UpdateService");
        Log.i("UpdateService", "UpdateService constructor");
    }

    @Override
    public void onCreate() {
        Log.i("UpdateService", "UpdateService onCreate");
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        notifyFoodUpdate();
        getFoodUpdate();
        Log.i("UpdateService", "UpdateService onHandleIntent");
    }

    //通知菜品
    public void notifyFoodUpdate(){
        //通知
        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setTicker("有新的食品更新");
        mBuilder.setContentTitle("新食品").setContentText("食品更新了").setSmallIcon(R.drawable.ic_logo).setSound(soundUri);
        mBuilder.setAutoCancel(true);
        Intent intent=new Intent(this, MainScreen.class);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        manager.notify(1003,notification);

    }
    //得到菜品信息
    public void getFoodUpdate(){
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        StringBuilder stringBuilder = new StringBuilder();

        //String requestURL="http://192.168.3.10:8080/SCOSServer/foodxml";
        String requestURL="http://192.168.3.10:8080/SCOSServer/food";
        Log.i("url", requestURL);
        try {
            //存储返回结果
            String result = null;
            String strRead = null;
            //开始连接
            URL url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            //使用Get方式请求数据
            conn.setRequestMethod("GET");
            conn.connect();
            //输入流获取返回数据
            InputStream is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((strRead = reader.readLine()) != null) {
                stringBuilder.append(strRead);
            }
            result = stringBuilder.toString();
            Log.i("service", "getFoodUpdate: "+result);
            ArrayList<Food> foodupdate= parseFoodJson(result);
            //ArrayList<Food> foodupdate=parseFoodXml(result);
            FoodView.foods.clear();
            for(Food food:foodupdate){
                FoodView.foods.add(food);

            }
            Message message=new Message();
            message.what=7;
            FoodView.updateUI(message);
            //FoodView.nofityalladapter();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    //解析菜json
    public ArrayList<Food> parseFoodJson(String foodjson){
        long startTime=System.currentTimeMillis();
        ArrayList<Food> result=new ArrayList<Food>();
        try {
            JSONArray jsonArray=new JSONArray(foodjson);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("name");
                double price=jsonObject.getDouble("price");
                int left=jsonObject.getInt("left");
                int type=jsonObject.getInt("type");
                Log.i("foodupdate", name+price+left+type);

                Food food=new Food();
                food.setFood_name(name);
                food.setFood_img(R.drawable.order);
                food.setFood_price(price);
                food.setFood_type(type);
                food.setFood_order_time(0);
                food.setFood_note("");
                food.setFood_hasorder(false);
                food.setFood_reserve(left);

                result.add(food);
            }
        }catch (Exception e){
            e.toString();
        }
        long endTime=System.currentTimeMillis();
        Log.d("解析时间", (endTime-startTime)+"ms");
        return result;

    }

    public ArrayList<Food> parseFoodXml(String foodxml){
        long startTime=System.currentTimeMillis();
        ArrayList<Food> result=new ArrayList<Food>();
        // 创建DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过documentBuilder对象 的parser方法加载books。xml文件到当前项目下
            InputStream iStream=new ByteArrayInputStream(foodxml.getBytes());
            Document document=db.parse(iStream);

            //获取所有book节点的集合
            NodeList foodlist = document.getElementsByTagName("food");
            Log.i("xml", "parseFoodXml总结点数: "+foodlist.getLength());
            //遍历每一个book节点
            for(int i= 0; i<foodlist.getLength();i++){
                Node food = foodlist.item(i);
                NodeList chilNod = food.getChildNodes();
                String name="";
                double price=0;
                int type=0;
                int left=0;
                    for(int k = 0;k<chilNod.getLength();k++){
                        Node chil = chilNod.item(k);
                        //区分text 类型node
                        if(chilNod.item(k).getNodeType() == Node.ELEMENT_NODE){
                            if(chil.getNodeName().equals("Name")) name=chilNod.item(k).getTextContent();
                            if(chil.getNodeName().equals("price")) price=Double.parseDouble(chilNod.item(k).getTextContent());
                            if(chil.getNodeName().equals("left")) left=Integer.parseInt(chilNod.item(k).getTextContent());
                            if(chil.getNodeName().equals("type")) type=Integer.parseInt(chilNod.item(k).getTextContent());
                        }
                    }
                Food newfood=new Food();
                newfood.setFood_name(name);
                newfood.setFood_img(R.drawable.order);
                newfood.setFood_price(price);
                newfood.setFood_type(type);
                newfood.setFood_order_time(0);
                newfood.setFood_note("");
                newfood.setFood_hasorder(false);
                newfood.setFood_reserve(left);

                result.add(newfood);
                }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long endTime=System.currentTimeMillis();
        Log.i("解析时间", (endTime-startTime)+"ms");
        return result;
    }
}
