package com.zhiz.japanesegirl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class Game_4x4 extends Activity implements View.OnClickListener {

    private static final String url_create_data = "http://218.161.61.62/japan/create_4x4data.php";
    private static final String TAG_NAME = "name";
    private static final String TAG_TIME = "time";
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();

    private ImageView oImgVw4x4[] = new ImageView[16];
    boolean bFaceUp[] = new boolean[16];
    private ImageView oHome, oAgain, oSound, oEndHome, oEndAgain;
    private TextView oTimer, oEndTime;
    private FrameLayout oFlayoutEnd;
    private int tsec = 0, csec = 0, cmin = 0;
    private boolean startflag = true;

    int iCard[][] = {{R.drawable.a0, 0}, {R.drawable.a1, 0},
            {R.drawable.a0, 1}, {R.drawable.a1, 1},
            {R.drawable.a0, 2}, {R.drawable.a1, 2},
            {R.drawable.a0, 3}, {R.drawable.a1, 3},
            {R.drawable.a0, 4}, {R.drawable.a1, 4},
            {R.drawable.a0, 5}, {R.drawable.a1, 5},
            {R.drawable.a0, 6}, {R.drawable.a1, 6},
            {R.drawable.a0, 7}, {R.drawable.a1, 7}};
    int iFirstCard = -1, iLastCard = -1, iSeleCount = 0;
    String sTime = "";
    String sUserName = "";

    MediaPlayer mbk,mgo;
    SoundPool soundCk, soundFl, soundSc;
    int iCk, iFl, iSc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_4x4);
        //全螢幕
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        Intent it = getIntent();
        sUserName = it.getStringExtra(TAG_NAME);
        initView();
        initCard();
    }

    private void initView() {
        oFlayoutEnd = (FrameLayout) findViewById(R.id.FmLayoutEnd);
        oHome = (ImageView) findViewById(R.id.imgVw4x4_home);
        oAgain = (ImageView) findViewById(R.id.imgVw4x4_again);
        oSound = (ImageView) findViewById(R.id.imgVw4x4_sound);
        oEndHome = (ImageView) findViewById(R.id.imgVw4x4_EndHome);
        oEndAgain = (ImageView) findViewById(R.id.imgVw4x4_EndAgain);
        oTimer = (TextView) findViewById(R.id.txt4x4_time);
        oEndTime = (TextView) findViewById(R.id.txt4x4_EndTime);


        oHome.setOnClickListener(this);
        oAgain.setOnClickListener(this);
        oSound.setOnClickListener(this);
        oEndHome.setOnClickListener(this);
        oEndAgain.setOnClickListener(this);

        mbk = MediaPlayer.create(this, R.raw.backmusic);
        mbk.start();
        mgo = new MediaPlayer();
        mgo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mgo.release();
            }
        });
        soundCk = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        soundFl = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        soundSc = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        iCk = soundCk.load(this, R.raw.card_click, 1);
        iFl = soundFl.load(this, R.raw.card_fail, 1);
        iSc = soundSc.load(this, R.raw.card_success, 1);
    }

    private void initCard() {
        //宣告Timer
        Timer timer01 = new Timer();
        //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
        timer01.schedule(task, 1000, 1000);

        for (int i = 0; i < iCard.length; i++) {
            //將背景圖放入ImageView裡
            String txtID = "imgVw4x4_" + i;
            int regID = getResources().getIdentifier(txtID, "id", "com.zhiz.japanesegirl");
            oImgVw4x4[i] = (ImageView) findViewById(regID);
            oImgVw4x4[i].setImageResource(R.drawable.word_background);
            oImgVw4x4[i].setOnClickListener(this);

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
            case R.id.imgVw4x4_home:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent itHome = new Intent(this, GameMenu.class);
                itHome.putExtra(TAG_NAME, sUserName);
                startActivity(itHome);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                finish();
                break;
            case R.id.imgVw4x4_again:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent itAgain = new Intent(this, Game_4x4.class);
                startActivity(itAgain);
                finish();
                break;
            case R.id.imgVw4x4_EndHome:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent itEndHome = new Intent(this, GameMenu.class);
                itEndHome.putExtra(TAG_NAME, sUserName);
                startActivity(itEndHome);
                overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
                finish();
                break;
            case R.id.imgVw4x4_EndAgain:
                soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);
                Intent itEndAgain = new Intent(this, Game_4x4.class);
                startActivity(itEndAgain);
                finish();
                break;

        }

        //迴圈判斷點擊的牌
        for (int i = 0; i < iCard.length; i++) {
            if (v.getId() == oImgVw4x4[i].getId()) {
                //如果有上次點錯紀錄 , 將兩張牌蓋回背面 , 設定第一張牌為flase , 翻牌數-1 , 將First及Last設為預設
                if (iLastCard != -1) {
                    oImgVw4x4[iLastCard].setImageResource(R.drawable.word_background);
                    oImgVw4x4[iFirstCard].setImageResource(R.drawable.word_background);
                    bFaceUp[iFirstCard] = false;
                    iSeleCount--;
                    iFirstCard = -1;
                    iLastCard = -1;
                }
                //如果點到的牌=false(翻面) 以及是第一張牌 , 將牌翻面 , 總翻牌數+1
                if (!bFaceUp[i] && iFirstCard == -1) {
                    oImgVw4x4[i].setImageResource(iCard[i][0]);
                    iFirstCard = i;
                    bFaceUp[i] = true;
                    iSeleCount++;
                    soundCk.play(iCk, 1.0F, 1.0F, 0, 0, 1.0F);

                    //如果點到的牌為翻面 以及不是第一張牌
                } else if (!bFaceUp[i] && iFirstCard != -1) {
                    //如果點到的牌之陣列第二維 = 第一張牌之陣列第二維 , 將牌翻面 , 總翻牌數+1
                    if (iCard[iFirstCard][1] == iCard[i][1]) {
                        oImgVw4x4[i].setImageResource(iCard[i][0]);
                        iFirstCard = -1;
                        bFaceUp[i] = true;
                        iSeleCount++;
                        soundSc.play(iSc, 1.0F, 1.0F, 0, 0, 1.0F);
                    } else {
                        //將牌翻面 , 將i值存入iLastCard
                        oImgVw4x4[i].setImageResource(iCard[i][0]);
                        iLastCard = i;
                        soundFl.play(iFl, 1.0F, 1.0F, 0, 0, 1.0F);
                    }
                }
            }
        }

        //如果翻牌數量=總數 , 時間暫停 , 顯示成績 , 儲存成績
        if (iSeleCount >= iCard.length) {
            iSeleCount = 0;
            startflag = false;
            oEndTime.setText(sTime);
            oFlayoutEnd.setVisibility(View.VISIBLE);
            new CreateData().execute();
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

    class CreateData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Game_4x4.this);
            pDialog.setMessage("分數儲存中...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String time = oEndTime.getText().toString();
                // String timesub = time.substring(5);
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair(TAG_NAME, sUserName));
                param.add(new BasicNameValuePair(TAG_TIME, time));

                JSONObject json = jsonParser.makeHttpRequest(url_create_data, "POST", param);
                Log.d("Create Response", json.toString());
                // int success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
            mgo = MediaPlayer.create(Game_4x4.this, R.raw.gameover);
            mgo.start();
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_4x4, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
