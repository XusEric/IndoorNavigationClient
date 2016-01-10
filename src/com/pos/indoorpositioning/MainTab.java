package com.pos.indoorpositioning;

import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import com.pos.indoorpositioning.R;
import com.pos.util.WebServiceHelper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainTab extends Fragment{
	private EditText userNameEditText;  
    private EditText userPwdEditText;  
    private Button loginButton; 
    private TextView t;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.maintab, container,
				false);
		t=(TextView) view.findViewById(R.id.kuanTextView1); 
		loginButton=(Button) view.findViewById(R.id.login_Button);  
		userNameEditText=(EditText) view.findViewById(R.id.userName); 
        loginButton.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View arg0) {  
                String userName=userNameEditText.getText().toString();  
                if("".equals(userName)){  
                    Toast.makeText(MainTab.this.getActivity(), "用户名不能为空", Toast.LENGTH_LONG).show();  
                }  
                
                DoSomething ds1 = new DoSomething(userName);
                Thread t1 = new Thread(ds1);
                t1.start();
            }  
        });  
        
		return view;
	}
	
	public class DoSomething implements Runnable {
	    private String name;

	    public DoSomething(String name) {
	        this.name = name;
	    }

	    public void run() {
	    	Message msg = new Message();
	    	try
	    	{
		    	msg.what = 1;
		        HashMap<String, Object> paramsMap = new HashMap<String, Object>();  
		        paramsMap.put("name", name);  
		        SoapObject a= WebServiceHelper.getSoapObject("BasicData", "getUserList", null, paramsMap);
		        System.out.println("result:"+a.toString());
		        msg.obj=a;
	            handler.sendMessage(msg);
	    	}
	    	catch(Exception e)
	    	{
		    	msg.what = -1;
		        msg.obj=e;
	            handler.sendMessage(msg);
	    		System.out.println("Error:"+e);
	    	}
	    }
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //findViewById(R.id.Btn_Login).setEnabled(true);
            switch (msg.what)
            {
                case -1:
                    //exception
                    //MessageHelper.AlertDialog(Activity_Main.this, "异常提示", msg.obj.toString());
                	t.setText(msg.obj.toString()); 
                	System.out.println("异常提示:"+msg.obj.toString());
                    break;
                case 0:
                    //fail
                    //MessageHelper.AlertDialog(Activity_Main.this, "错误提示", msg.obj.toString());
                    System.out.println("错误:"+msg.obj.toString());
                    break;
                case 1:
                    //login success
                    //MessageHelper.AlertDialog(Activity_Main.this, "操作提示", "登录成功。");
        	        t.setText(msg.obj.toString()); 
                    System.out.println("操作提示:登录成功");
                    break;
                default:
                    break;
            }
        }
    };
}
