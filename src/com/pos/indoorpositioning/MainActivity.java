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
	// 三个tab布局
	private RelativeLayout mainLayout, settingLayout, meLayout;

	// 底部标签切换的Fragment
	private Fragment mainFragment, settingFragment, meFragment,
			currentFragment;
	// 底部标签图片
	private ImageView mainImg, settingImg, meImg;
	// 底部标签的文本
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
	 * 初始化UI
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
	 * 初始化底部标签
	 */
	private void initTab() {
		if (mainFragment == null) {
			mainFragment = new MainTab();
		}

		if (!mainFragment.isAdded()) {
			// 提交事务
			getSupportFragmentManager().beginTransaction()
					.add(R.id.content_layout, mainFragment).commit();

			// 记录当前Fragment
			currentFragment = mainFragment;
			// 设置图片文本的变化
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
		case R.id.rl_maintab: // 定位
			clickTab1Layout();
			break;
		case R.id.rl_setting: // 采集
			clickTab2Layout();
			break;
		case R.id.rl_me: // 我的
			clickTab3Layout();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 点击第一个tab
	 */
	private void clickTab1Layout() {
		if (mainFragment == null) {
			mainFragment = new MainTab();
		}
		addOrShowFragment(getSupportFragmentManager().beginTransaction(), mainFragment);
		
		// 设置底部tab变化
		mainImg.setImageResource(R.drawable.btn_know_pre);
		mainTv.setTextColor(getResources().getColor(R.color.bottomtab_press));
		settingImg.setImageResource(R.drawable.btn_wantknow_nor);
		settingTv.setTextColor(getResources().getColor(
				R.color.bottomtab_normal));
		meImg.setImageResource(R.drawable.btn_my_nor);
		meTv.setTextColor(getResources().getColor(R.color.bottomtab_normal));
	}
	
	/**
	 * 点击第二个tab
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
	 * 点击第三个tab
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
	 * 添加或者显示碎片
	 * 
	 * @param transaction
	 * @param fragment
	 */
	private void addOrShowFragment(FragmentTransaction transaction,
			Fragment fragment) {
		if (currentFragment == fragment)
			return;

		if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
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
            	if (secondTime - firstTime > 2000) {                                         //如果两次按键时间间隔大于2秒，则不退出  
	            	Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();   
	            	firstTime = secondTime;//更新firstTime  
	            	return true;   
            	} else {                                                    //两次按键小于2秒时，退出应用  
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
            Toast.makeText(this, "再按一次退出程序 ", Toast.LENGTH_SHORT).show();  
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
