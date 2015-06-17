package com.zhiz.japanesegirl;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class Game_2x2 extends Activity implements View.OnClickListener {

    private ImageView oImgVw2x2[] = new ImageView[4];
    private ImageView oHome, oAgain, oSound, oEndHome, oEndAgain, oNameBtn,oNameIcon;
    private TextView oTimer, oEndTime;
    private EditText oEditName;
    private FrameLayout oFlayoutEnd, oFlayoutName;
    private int tsec = 0, csec = 0, cmin = 0;
    private boolean startflag = false;
    boolean bFaceUp[] = new boolean[4];

    int iCard[][] = {{R.drawable.a0, 0}, {R.drawable.a1, 0}, {R.drawable.b0, 1}, {R.drawable.b1, 1}};
    int iFirstCard = -1, iLastCard = -1, iSeleCount = 0;
    String sTime = "";
    String sName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_2x2);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();

        if (sName.equals("")) {
            oFlayoutName.setVisibility(View.VISIBLE);
        } else {
            initCard();
        }


    }

    private void initView() {
        oFlayoutEnd = (FrameLayout) findViewById(R.id.FmLayoutEnd);
        oFlayoutName = (FrameLayout) findViewById(R.id.FmLayoutEditName);
        oHome = (ImageView) findViewById(R.id.imgVw2x2_home);
        oAgain = (ImageView) findViewById(R.id.imgVw2x2_again);
        oSound = (ImageView) findViewById(R.id.imgVw2x2_sound);
        oEndHome = (ImageView) findViewById(R.id.imgVw2x2_EndHome);
        oEndAgain = (ImageView) findViewById(R.id.imgVw2x2_EndAgain);
        oNameBtn = (ImageView) findViewById(R.id.imgVw2x2_NameBtn);
        oNameIcon = (ImageView) findViewById(R.id.imgVw2x2_NameIcon);
        oTimer = (TextView) findViewById(R.id.txt2x2_time);
        oEndTime = (TextView) findViewById(R.id.txt2x2_EndTime);
        oEditName = (EditText) findViewById(R.id.txt2x2_Name);

        oHome.setOnClickListener(this);
        oAgain.setOnClickListener(this);
        oSound.setOnClickListener(this);
        oEndHome.setOnClickListener(this);
        oEndAgain.setOnClickListener(this);
        oNameBtn.setOnClickListener(this);
        oNameIcon.setOnClickListener(this);

    }

    private void initCard(){
        //宣告Timer
        Timer timer01 = new Timer();
        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        timer01.schedule(task, 1000, 1000);

        for (int i = 0; i < iCard.length; i++) {
            //將背景圖放入ImageView裡
            String txtID = "imgVw2x2_" + i;
            int regID = getResources().getIdentifier(txtID, "id", "com.zhiz.japanesegirl");
            oImgVw2x2[i] = (ImageView) findViewById(regID);
            oImgVw2x2[i].setImageResource(R.drawable.word_background);
            oImgVw2x2[i].setOnClickListener(this);

            //false = 蓋牌
            bFaceUp[i] = false;

            //使用亂數將iCard陣列打亂
            int n1 = (int) (Math.random() * iCard.length);
            int n2 = (int) (Math.random() * iCard.length);

            int temp[] = iCard[n1];
            iCard[n1] = iCard[n2];
            iCard[n2] = temp;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgVw2x2_home:
                Intent itHome = new Intent(this, GameMenu.class);
                startActivity(itHome);
                finish();
                break;
            case R.id.imgVw2x2_again:
                Intent itAgain = new Intent(this, Game_2x2.class);
                startActivity(itAgain);
                finish();
                break;
            case R.id.imgVw2x2_EndHome:
                Intent itEndHome = new Intent(this, GameMenu.class);
                startActivity(itEndHome);
                finish();
                break;
            case R.id.imgVw2x2_EndAgain:
                Intent itEndAgain = new Intent(this, Game_2x2.class);
                startActivity(itEndAgain);
                finish();
                break;
            case R.id.imgVw2x2_NameIcon:
                startflag = false;
                if(!sName.equals("")){
                    oEditName.setText(sName);
                }
                oFlayoutName.setVisibility(View.VISIBLE);
                break;
            case R.id.imgVw2x2_NameBtn:
                startflag = true;
                sName = oEditName.getText().toString();
                oFlayoutName.setVisibility(View.GONE);
                initCard();
                break;
        }

        //迴圈判斷點擊的牌
        for (int i = 0; i < iCard.length; i++) {
            if (v.getId() == oImgVw2x2[i].getId()) {
                //如果有上次點錯紀錄 , 將兩張牌蓋回背面 , 設定第一張牌為flase , 翻牌數-1 , 將First及Last設為預設
                if (iLastCard != -1) {
                    oImgVw2x2[iLastCard].setImageResource(R.drawable.word_background);
                    oImgVw2x2[iFirstCard].setImageResource(R.drawable.word_background);
                    bFaceUp[iFirstCard] = false;
                    iSeleCount--;
                    iFirstCard = -1;
                    iLastCard = -1;
                }
                //如果點到的牌=false(翻面) 以及是第一張牌 , 將牌翻面 , 總翻牌數+1
                if (!bFaceUp[i] && iFirstCard == -1) {
                    oImgVw2x2[i].setImageResource(iCard[i][0]);
                    iFirstCard = i;
                    bFaceUp[i] = true;
                    iSeleCount++;

                    //如果點到的牌為翻面 以及不是第一張牌
                } else if (!bFaceUp[i] && iFirstCard != -1) {
                    //如果點到的牌之陣列第二維 = 第一張牌之陣列第二維 , 將牌翻面 , 總翻牌數+1
                    if (iCard[iFirstCard][1] == iCard[i][1]) {
                        oImgVw2x2[i].setImageResource(iCard[i][0]);
                        iFirstCard = -1;
                        bFaceUp[i] = true;
                        iSeleCount++;
                    } else {
                        //將牌翻面 , 將i值存入iLastCard
                        oImgVw2x2[i].setImageResource(iCard[i][0]);
                        iLastCard = i;
                    }
                }
            }
        }

        if (iSeleCount >= iCard.length) {
            startflag = false;
            oEndTime.setText(sTime);
            oFlayoutEnd.setVisibility(View.VISIBLE);
            //  new CreateData().execute();
        }


    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    csec = tsec % 60;
                    cmin = tsec / 60;

                    if (cmin < 10) {
                        sTime = "0" + cmin;
                    } else {
                        sTime = "" + cmin;
                    }
                    if (csec < 10) {
                        sTime = sTime + ":0" + csec;
                    } else {
                        sTime = sTime + ":" + csec;
                    }
                    //s字串為00:00格式
                    oTimer.setText(sTime);
                    break;
            }
        }
    };
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (startflag) {
                //如果startflag是true則每秒tsec+1
                tsec++;
                Message message = new Message();
                //傳送訊息1
                message.what = 1;
                handler.sendMessage(message);
            }
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_2x2, menu);
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
