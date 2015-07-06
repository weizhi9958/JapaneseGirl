package com.zhiz.japanesegirl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GameMenu extends FragmentActivity implements View.OnClickListener {

    private static final String TAG_INSTAL = "instal";
    private static final String TAG_NAME = "name";
    private static final String TAG_USERNAME = "username";


    FrameLayout oFmLayoutMain, oFmLayoutName, oFmLayoutRank, oFmLayoutIntro,oFmLayoutAbout;
    ImageView oImgVw_2x2, oImgVw_4x4, oImgVw_6x6;
    ImageView oBtnNameIcon, oBtnName;
    ImageView oRankIcon, oRankClose;
    ImageView oIntroIcon,oIntroClose;
    ImageView oAboutIcon,oAboutFb,oAboutClose;
    EditText oUserName;
    String sUserName;
    MediaPlayer mbk;
    SoundPool soundCk;
    int iCk;

    private TabHost mTabHost;
    private TabManager mTabManager;

    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        //全螢幕
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();
        mbk = MediaPlayer.create(this, R.raw.backmenu);

        mbk.setLooping(true);
        if (!mbk.isPlaying()) {
            mbk.start();
        }
        //如果有回傳姓名資料則不顯示輸入視窗
        Intent it = getIntent();
        if (it.hasExtra(TAG_NAME)) {
            sUserName = it.getStringExtra(TAG_NAME);
            oUserName.setText(it.getStringExtra(TAG_NAME));
            oFmLayoutName.setVisibility(View.GONE);
        }else {
            sUserName = it.getStringExtra(TAG_USERNAME);
            oUserName.setText(it.getStringExtra(TAG_USERNAME));
        }


    }

    private void initView() {
        oImgVw_2x2 = (ImageView) findViewById(R.id.imgVw_2x2);
        oImgVw_4x4 = (ImageView) findViewById(R.id.imgVw_4x4);
        oImgVw_6x6 = (ImageView) findViewById(R.id.imgVw_6x6);
        oBtnName = (ImageView) findViewById(R.id.imgVw_NameBtn);
        oBtnNameIcon = (ImageView) findViewById(R.id.imgVw_Name);
        oRankIcon = (ImageView) findViewById(R.id.imgVw_Rank);
        oRankClose = (ImageView) findViewById(R.id.imgVw_Close);
        oIntroIcon = (ImageView) findViewById(R.id.imgVw_Intro);
        oIntroClose = (ImageView) findViewById(R.id.imgVw_Close2);
        oAboutIcon = (ImageView) findViewById(R.id.imgVw_About);
        oAboutFb = (ImageView) findViewById(R.id.imgVw_Fb);
        oAboutClose = (ImageView) findViewById(R.id.imgVw_Close3);
        oFmLayoutName = (FrameLayout) findViewById(R.id.FmLayoutName);
        oFmLayoutMain = (FrameLayout) findViewById(R.id.FmLayoutMain);
        oFmLayoutRank = (FrameLayout) findViewById(R.id.FmLayoutRank);
        oFmLayoutIntro = (FrameLayout) findViewById(R.id.FmLayoutIntro);
        oFmLayoutAbout = (FrameLayout) findViewById(R.id.FmLayoutAbout);
        oUserName = (EditText) findViewById(R.id.txt_Name);

        oImgVw_2x2.setOnClickListener(this);
        oImgVw_4x4.setOnClickListener(this);
        oImgVw_6x6.setOnClickListener(this);
        oBtnName.setOnClickListener(this);
        oBtnNameIcon.setOnClickListener(this);
        oRankIcon.setOnClickListener(this);
        oRankClose.setOnClickListener(this);
        oIntroIcon.setOnClickListener(this);
        oIntroClose.setOnClickListener(this);
        oAboutIcon.setOnClickListener(this);
        oAboutFb.setOnClickListener(this);
        oAboutClose.setOnClickListener(this);

        soundCk = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        iCk = soundCk.load(this, R.raw.card_click, 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mbk.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mbk.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVw_2x2:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent it22 = new Intent(this, Game_2x2.class);
                it22.putExtra(TAG_NAME, sUserName);
                startActivity(it22);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                finish();
                break;
            case R.id.imgVw_4x4:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent it44 = new Intent(this, Game_4x4.class);
                it44.putExtra(TAG_NAME, sUserName);
                startActivity(it44);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                finish();
                break;
            case R.id.imgVw_NameBtn:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);

                String limitEx="[ `~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";

                Pattern pattern = Pattern.compile(limitEx);
                Matcher m = pattern.matcher(oUserName.getText().toString());

                if(m.find()){
                    Toast.makeText(GameMenu.this, "不允許輸入特殊符號", Toast.LENGTH_LONG).show();
                }else {
                    if(oUserName.getText().toString().equals("")){
                        Toast.makeText(GameMenu.this, "請輸入ID", Toast.LENGTH_LONG).show();
                    }else{
                        sUserName = oUserName.getText().toString();

                        //將使用者名稱存入偏好設定
                        SharedPreferences settings = getSharedPreferences(TAG_INSTAL, 0);
                        settings.edit().putString(TAG_USERNAME,sUserName).commit();

                        oFmLayoutName.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.imgVw_Name:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                oFmLayoutName.setVisibility(View.VISIBLE);
                break;
            case R.id.imgVw_Rank:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                oFmLayoutRank.setVisibility(View.VISIBLE);
                CreateTab();
                break;
            case R.id.imgVw_Close:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                mTabHost.setCurrentTab(0);
                mTabHost.clearAllTabs();
                oFmLayoutRank.setVisibility(View.GONE);
                break;
            case R.id.imgVw_Intro:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                oFmLayoutIntro.setVisibility(View.VISIBLE);
                break;
            case R.id.imgVw_Close2:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                oFmLayoutIntro.setVisibility(View.GONE);
                break;
            case R.id.imgVw_About:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                oFmLayoutAbout.setVisibility(View.VISIBLE);
                break;
            case R.id.imgVw_Fb:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.facebook.com/Nihongogirl0312"));
                startActivity(intent);
                break;
            case R.id.imgVw_Close3:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                oFmLayoutAbout.setVisibility(View.GONE);
                break;
        }
    }

    private void CreateTab() {

        mTabHost = (TabHost) findViewById(R.id.tabHost);
        mTabHost.setup();
        mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

        mTabHost.setCurrentTab(0);//設定一開始就跳到第一個分頁
        mTabManager.addTab(
                mTabHost.newTabSpec("4x4").setIndicator("4x4"),
                Give_4X4_Top10_All.class, null);

        Give_4X4_Top10_Me b = new Give_4X4_Top10_Me();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_NAME, sUserName);
        b.setArguments(bundle);
        mTabManager.addTab(
                mTabHost.newTabSpec("我的排名").setIndicator("我的排名"),
                Give_4X4_Top10_Me.class, bundle);

        TabWidget tabWidget = mTabHost.getTabWidget();   //取得tab的物件
        int count = tabWidget.getChildCount();   //取得tab的分頁有幾個
        for (int i = 0; i < count; i++) {
            //修改字體大小及顏色
            TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(18);
            tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //捕获返回键按下的事件
        if(keyCode == KeyEvent.KEYCODE_BACK){
            //获取当前系统时间的毫秒数
            currentBackTime = System.currentTimeMillis();
            //比较上次按下返回键和当前按下返回键的时间差，如果大于2秒，则提示再按一次退出
            if(currentBackTime - lastBackTime > 2 * 1000){
                Toast.makeText(this, "再按一次返回鍵退出", Toast.LENGTH_SHORT).show();
                lastBackTime = currentBackTime;
            }else{ //如果两次按下的时间差小于2秒，则退出程序
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

}
