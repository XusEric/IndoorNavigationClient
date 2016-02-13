package com.pos.indoorpositioning;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.SupportMapFragment;
import com.pos.indoorpositioning.MainTab;
import com.pos.indoorpositioning.CollectTab;
import com.pos.indoorpositioning.R;
import com.pos.indoorpositioning.SettingTab;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements OnClickListener {
	// ����tab����
	private RelativeLayout mainLayout, settingLayout, collectLayout, fingerLayout;

	// �ײ���ǩ�л���Fragment
	private Fragment mainFragment, settingFragment, collectFragment, fingerFragment,
			currentFragment;
	// �ײ���ǩͼƬ
	private ImageView mainImg, settingImg, collectImg, fingerImg;
	// �ײ���ǩ���ı�
	private TextView mainTv, settingTv, collectTv, fingerTv;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
            // We were just launched 
			initUI();
			initTab();
            
        } else {
            // We are being restored
            //Bundle map = savedInstanceState.getBundle(ICICLE_KEY);
//        	if (map != null) {
//                mSnakeView.restoreState(map);
//            } else {
//                mSnakeView.setMode(SnakeView.PAUSE);
//            }
        }
	}

//	@Override
//	protected void onSaveInstanceState(Bundle savedInstanceState) {
//		System.out.println("************onSaveInstanceState");
//		super.onSaveInstanceState(savedInstanceState);
//		//savedInstanceState.putBundle(ICICLE_KEY, mSnakeView.saveState());
//	}
	
	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		mainLayout = (RelativeLayout) findViewById(R.id.rl_maintab);
		settingLayout = (RelativeLayout) findViewById(R.id.rl_setting);
		collectLayout = (RelativeLayout) findViewById(R.id.rl_collect);
		fingerLayout = (RelativeLayout) findViewById(R.id.rl_finger);
		mainLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		collectLayout.setOnClickListener(this);
		fingerLayout.setOnClickListener(this);

		mainImg = (ImageView) findViewById(R.id.iv_maintab);
		settingImg = (ImageView) findViewById(R.id.iv_setting);
		collectImg = (ImageView) findViewById(R.id.iv_collect);
		fingerImg = (ImageView) findViewById(R.id.iv_finger);
		mainTv = (TextView) findViewById(R.id.tv_maintab);
		settingTv = (TextView) findViewById(R.id.tv_setting);
		collectTv = (TextView) findViewById(R.id.tv_collect);
		fingerTv = (TextView) findViewById(R.id.tv_finger);

	}

	/**
	 * ��ʼ���ײ���ǩ
	 */
	private void initTab() {
		if (mainFragment == null) {
			mainFragment = new MainTab();
		}

		if (!mainFragment.isAdded()) {
			// �ύ����
			getSupportFragmentManager().beginTransaction()
					.add(R.id.content_layout, mainFragment).commit();

			// ��¼��ǰFragment
			currentFragment = mainFragment;
			// ����ͼƬ�ı��ı仯
			mainImg.setImageResource(R.drawable.btn_know_pre);
			mainTv.setTextColor(getResources()
					.getColor(R.color.bottomtab_press));
			settingImg.setImageResource(R.drawable.btn_wantknow_nor);
			settingTv.setTextColor(getResources().getColor(
					R.color.bottomtab_normal));
			collectImg.setImageResource(R.drawable.btn_my_nor);
			collectTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));

		}

	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_maintab: // ��λ
			clickTab1Layout();
			break;
		case R.id.rl_finger: // ָ�Ʋɼ�
			clickTab2Layout();
			break;
		case R.id.rl_collect: // Rssi�ռ�
			clickTab3Layout();
			break;
		case R.id.rl_setting: // ϵͳ����
			clickTab4Layout();
			break;
		default:
			break;
		}
	}
	
	/**
	 * �����һ��tab�����ڶ�λ��ҳ
	 */
	private void clickTab1Layout() {
		if (mainFragment == null) {
			mainFragment = new MainTab();
		}
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), mainFragment);
		
		// ���õײ�tab�仯
		mainImg.setImageResource(R.drawable.btn_know_pre);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
		settingImg.setImageResource(R.drawable.btn_wantknow_nor);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		collectImg.setImageResource(R.drawable.btn_my_nor);
		collectTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		fingerImg.setImageResource(R.drawable.btn_my_nor);
		fingerTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
	}

	/**
	 * ����ڶ���tab��ָ�Ʋɼ���ҳ
	 */
	public void clickTab2Layout() {
		if (fingerFragment == null) {
			fingerFragment = new FingerTab();
		}
		
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), fingerFragment);
		mainImg.setImageResource(R.drawable.btn_know_nor);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		settingImg.setImageResource(R.drawable.btn_wantknow_nor);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		collectImg.setImageResource(R.drawable.btn_my_nor);
		collectTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		fingerImg.setImageResource(R.drawable.btn_my_pre);
		fingerTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
	}
	
	/**
	 * ���������tab��RSSI�ռ���ҳ
	 */
	private void clickTab3Layout() {
		if (collectFragment == null) {
			collectFragment = new CollectTab();
		}
		
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), collectFragment);
		mainImg.setImageResource(R.drawable.btn_know_nor);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		settingImg.setImageResource(R.drawable.btn_wantknow_nor);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		collectImg.setImageResource(R.drawable.btn_my_pre);
		collectTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
		fingerImg.setImageResource(R.drawable.btn_my_nor);
		fingerTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		
	}


	/**
	 * ������ĸ�tab��ϵͳ������ҳ
	 */
	private void clickTab4Layout() {
		if (settingFragment == null) {
			settingFragment = new SettingTab();
		}
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), settingFragment);
		
		mainImg.setImageResource(R.drawable.btn_know_nor);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		settingImg.setImageResource(R.drawable.btn_wantknow_pre);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_press));
		collectImg.setImageResource(R.drawable.btn_my_nor);
		collectTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		fingerImg.setImageResource(R.drawable.btn_my_nor);
		fingerTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));

	}

	/**
	 * ��ӻ�����ʾ��Ƭ
	 * 
	 * @param transaction
	 * @param fragment
	 */
	private void addOrShowFragment(FragmentTransaction transaction,
			Fragment fragment) {
		if (currentFragment == fragment)
			return;

		if (!fragment.isAdded()) { // �����ǰfragmentδ����ӣ�����ӵ�Fragment��������
			transaction.remove(currentFragment)
					.add(R.id.content_layout, fragment).commit();
		} else {
			transaction.remove(currentFragment).show(fragment).commit();
		}

		currentFragment = fragment;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    private long firstTime = 0;  
    @Override  
    public boolean onKeyUp(int keyCode, KeyEvent event) {  
        // TODO Auto-generated method stub  
        switch(keyCode)  
        {  
        	case KeyEvent.KEYCODE_BACK:  
            	long secondTime = System.currentTimeMillis();   
            	if (secondTime - firstTime > 2000) {                                         //������ΰ���ʱ��������2�룬���˳�  
	            	Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();   
	            	firstTime = secondTime;//����firstTime  
	            	return true;   
            	} else {                                                    //���ΰ���С��2��ʱ���˳�Ӧ��  
            		System.exit(0);  
            	}   
            break;  
        }  
      return super.onKeyUp(keyCode, event);  
    }  
    
    private int mBackKeyPressedTimes = 0;  
    
    @Override  
    public void onBackPressed() {  
        if (mBackKeyPressedTimes == 0) {  
            Toast.makeText(this, "�ٰ�һ���˳����� ", Toast.LENGTH_SHORT).show();  
            mBackKeyPressedTimes = 1;  
            new Thread() {  
                @Override  
                public void run() {  
                    try {  
                            Thread.sleep(2000);  
                    } catch (InterruptedException e) {  
                            e.printStackTrace();  
                    } finally {  
                            mBackKeyPressedTimes = 0;  
                    }  
                }  
            }.start();  
            return;  
        }
        else{  
           this.finish();  
        }  
        super.onBackPressed();  
    }
    
}
