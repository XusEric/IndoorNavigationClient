package com.pos.indoorpositioning;

import com.pos.indoorpositioning.MainTab;
import com.pos.indoorpositioning.MeTab;
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
	private RelativeLayout mainLayout, settingLayout, meLayout;

	// �ײ���ǩ�л���Fragment
	private Fragment mainFragment, settingFragment, meFragment,
			currentFragment;
	// �ײ���ǩͼƬ
	private ImageView mainImg, settingImg, meImg;
	// �ײ���ǩ���ı�
	private TextView mainTv, settingTv, meTv;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		meLayout = (RelativeLayout) findViewById(R.id.rl_me);
		mainLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
		meLayout.setOnClickListener(this);

		mainImg = (ImageView) findViewById(R.id.iv_maintab);
		settingImg = (ImageView) findViewById(R.id.iv_setting);
		meImg = (ImageView) findViewById(R.id.iv_me);
		mainTv = (TextView) findViewById(R.id.tv_maintab);
		settingTv = (TextView) findViewById(R.id.tv_setting);
		meTv = (TextView) findViewById(R.id.tv_me);

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
			meImg.setImageResource(R.drawable.btn_my_nor);
			meTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));

		}

	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_maintab: // ��λ
			clickTab1Layout();
			break;
		case R.id.rl_setting: // �ɼ�
			clickTab2Layout();
			break;
		case R.id.rl_me: // �ҵ�
			clickTab3Layout();
			break;
		default:
			break;
		}
	}
	
	/**
	 * �����һ��tab
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
		meImg.setImageResource(R.drawable.btn_my_nor);
		meTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
	}
	
	/**
	 * ����ڶ���tab
	 */
	private void clickTab2Layout() {
		if (settingFragment == null) {
			settingFragment = new SettingTab();
		}
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), settingFragment);
		
		mainImg.setImageResource(R.drawable.btn_know_nor);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		settingImg.setImageResource(R.drawable.btn_wantknow_pre);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_press));
		meImg.setImageResource(R.drawable.btn_my_nor);
		meTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));

	}

	/**
	 * ���������tab
	 */
	private void clickTab3Layout() {
		if (meFragment == null) {
			meFragment = new MeTab();
		}
		
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), meFragment);
		mainImg.setImageResource(R.drawable.btn_know_nor);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
		settingImg.setImageResource(R.drawable.btn_wantknow_nor);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		meImg.setImageResource(R.drawable.btn_my_pre);
		meTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
		
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
			transaction.hide(currentFragment)
					.add(R.id.content_layout, fragment).commit();
		} else {
			transaction.hide(currentFragment).show(fragment).commit();
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
