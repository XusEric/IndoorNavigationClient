package com.pos.indoorpositioning;
import com.pos.indoorpositioning.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;

public class CollectTab extends Fragment{
	private Button buttonCollect,buttonFinger;
    
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
		buttonFinger = (Button)view.findViewById(R.id.btnFinger);

		buttonCollect.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
//            	Intent in = new Intent(MainActivity.this,WifiExample.class);
//    			startActivity(in);
            }  
        }); 
		buttonFinger.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
//            	Intent in = new Intent(getActivity(),FingerActivity.class);
//    			startActivity(in);
//            	getSupportFragmentManager().beginTransaction().hide(currentFragment).show(fragment).commit();

            }  
        }); 
		return view;
	}
}
