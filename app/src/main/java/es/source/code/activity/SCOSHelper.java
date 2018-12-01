package es.source.code.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.scos.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import es.source.code.service.ServerObserverService;

public class SCOSHelper extends AppCompatActivity implements AdapterView.OnItemClickListener{

    GridView gridView;
    List<Map<String,Object>> datalist;
    SimpleAdapter adapter;


   Handler handler=new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //收到6,邮件发送成功
                case 6:
                    Toast.makeText(SCOSHelper.this, "邮件发送成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_scoshelper);

        gridView=(GridView)findViewById(R.id.scoshelper_gridview);
        //初始化数据
        initdata();
        String[] from={"img","text"};
        int[] to={R.id.scoshepler_gridview_img,R.id.scoshepler_gridview_text};
        adapter=new SimpleAdapter(this,datalist,R.layout.gridview_helper,from,to);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(this);


        //绑定服务
//        Intent bindintent=new Intent(this,ServerObserverService.class);
//        bindService(bindintent,conn,BIND_AUTO_CREATE);


        //发短信权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE}, 1);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView=(TextView)view.findViewById(R.id.scoshepler_gridview_text);
        String itemtext=textView.getText().toString();
        switch (itemtext){
            case "用户使用协议":
                break;
            case "人工电话帮助":
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:5555"));
                startActivity(intent);
                break;
            case "短信帮助":

                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage("18260163912",null,"测试",null,null);

                break;
            case "邮件帮助":
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendMail();
                            //发送完邮件发送6
                            Message message=new Message();
                            message.what=6;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
        }
    }

    private void initdata(){
        int icons[]={R.drawable.list,R.drawable.list,R.drawable.list,R.drawable.list,R.drawable.list};
        String name[]={"用户使用协议","关于系统","人工电话帮助","短信帮助","邮件帮助"};
        datalist=new ArrayList<Map<String,Object>>();
        for(int i=0;i<icons.length;i++){
            Map<String,Object> map=new HashMap<String,Object>();
            map.put("img",icons[i]);
            map.put("text",name[i]);
            datalist.add(map);
        }
    }

    private void sendMail() throws Exception{
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props;          // 参数配置
        props = new Properties();
        props.setProperty("mail.smtp.ssl.enable", "true");
        props.setProperty("mail.transport.protocol", "smtp");  // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.qq.com");   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");      // 需要请求认证
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log
        session.setDebug(true);
        // 3. 创建一封邮件
        MimeMessage message = createMimeMessage(session, "wangqinkuan@qq.com", "wangqinkuan@qq.com");//我这里是以163邮箱为发信邮箱测试通过
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        transport.connect("1345244694@qq.com", "gdjuvdmhjabwjhia");
        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }

    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "邮件", "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "亲爱的开发者", "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject("主题", "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent("这是一条测试邮件", "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }
}




