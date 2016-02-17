package com.pos.indoorpositioning;
import com.pos.indoorpositioning.R;
import com.pos.indoorpositioning.MainTab.DoLocation;
import com.pos.util.DataBaseHelper;
import com.pos.rssi.*;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class CollectTab extends Fragment{
	private Button buttonCollect,buttonStopCollect,buttonSplit;
	private EditText rateNumberText;  
	Intent intent;
	private SQLiteDatabase sqliteDatabase=null;//sqlite操作对象
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.collecttab, container,
				false);
		buttonCollect = (Button)view.findViewById(R.id.btnCollect);
		buttonStopCollect = (Button)view.findViewById(R.id.btnStopCollect);
		buttonSplit = (Button)view.findViewById(R.id.btnSplit);
		rateNumberText=(EditText)view.findViewById(R.id.rate_number); 

		buttonCollect.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	intent = new Intent(getActivity(),CollectService.class);
            	Bundle bundle  = new Bundle();
    	        bundle.putLong("rate", Long.parseLong(rateNumberText.getText().toString()));
    	        intent.putExtras(bundle);
    	        getActivity().startService(intent);
            }  
        }); 
		buttonStopCollect.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	getActivity().stopService(intent); 
            }  
        }); 
		buttonSplit.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	DoSplit ds1 = new DoSplit();
	            Thread t1 = new Thread(ds1);
	            t1.start();
            }  
        }); 
		//*********打开数据库相关**********
        // 创建了一个DatabaseHelper对象 
        DataBaseHelper dbHelper = new DataBaseHelper(this.getContext(),"MyWifiCollect",null,1);  
        // 创建或打开一个连接  
        sqliteDatabase = dbHelper.getReadableDatabase(); 
		return view;
	}
	
	//时态分割
	public class DoSplit implements Runnable {
	    public void run() {
    		Message msg = new Message();
	    	try
	    	{
	    		Cursor cursor = sqliteDatabase.rawQuery("select count(*) as total,MAC from CollectWifi group by MAC order by count(*) desc limit 1 ", new String[0]);
	    		cursor.moveToFirst(); 
	            String mac= cursor.getString(cursor.getColumnIndex("MAC"));
	            int total= cursor.getInt(cursor.getColumnIndex("total"));
	            cursor = sqliteDatabase.rawQuery("select Rssi,CreateTime from CollectWifi where MAC=? ", new String[]{mac});
	            double[] rssi = new double[total];
	            String[] time = new String[total];
	            int i=0;
	            while (cursor.moveToNext()) {
	            	rssi[i]= cursor.getDouble(cursor.getColumnIndex("Rssi"));
	            	time[i]= cursor.getString(cursor.getColumnIndex("CreateTime"));
	            	i++;
	            }
	            int index=Integer.parseInt(Splits.startSplit(rssi));
	    		msg.what = 1;
	    		msg.obj=time[index];
	            handler.sendMessage(msg);
	    	}
	    	catch(Exception e)
	    	{
		    	msg.what = -1;
		        msg.obj=e;
	            handler.sendMessage(msg);
	    		e.printStackTrace();
	    	}
	    }
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what)
            {
                case -1:
                	Toast.makeText(getContext(),"异常提示:"+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    System.out.println("错误:"+msg.obj.toString());
                    break;
                case 1:
                	Toast.makeText(getContext(),"分割完毕，时间点： "+msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
