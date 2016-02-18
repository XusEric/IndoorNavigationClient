package com.pos.indoorpositioning;


import com.pos.indoorpositioning.R;
import com.pos.util.ConfigHelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View.OnClickListener;

public class SettingTab extends Fragment {
	private Button buttonSave,buttonReset;
	private EditText etTime,etCluster,etKNum,etFloor,etIfIndex;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settingtab, container,
				false);
		buttonSave = (Button)view.findViewById(R.id.btnSave);
		buttonReset = (Button)view.findViewById(R.id.btnReset);
    	etTime=(EditText)view.findViewById(R.id.colTime);
    	etCluster=(EditText)view.findViewById(R.id.etCluster);
    	etKNum=(EditText)view.findViewById(R.id.etKNum);
    	etFloor=(EditText)view.findViewById(R.id.etFloor);
    	etIfIndex=(EditText)view.findViewById(R.id.etIfIndex);

		buttonSave.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	String collectTime=etTime.getText().toString(); 
            	String clusterNum=etCluster.getText().toString(); 
            	String kNum=etKNum.getText().toString();  
            	String floor=etFloor.getText().toString();  
            	String index=etIfIndex.getText().toString();  
            	ConfigHelper.writes("COLLECTTIME", collectTime);
            	ConfigHelper.writes("CLUSTERNUM", clusterNum);
            	ConfigHelper.writes("KNUM", kNum);
            	ConfigHelper.writes("FLOOR", floor);
            	ConfigHelper.writes("IFINDEX", index);
            	Toast.makeText(SettingTab.this.getActivity(), "±£´æ³É¹¦£¡", Toast.LENGTH_LONG).show();  
            }  
        }); 
		buttonReset.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	InitSettings();
            }  
        }); 
		InitSettings();
		return view;
	}
	
	private void InitSettings(){ 
    	etTime.setText(ConfigHelper.getCollectTime());
    	etCluster.setText(ConfigHelper.getClusterNum());
    	etKNum.setText(ConfigHelper.getKNum());
    	etFloor.setText(ConfigHelper.getFloor());
    	etIfIndex.setText(ConfigHelper.getIfIndex());
	}
}
