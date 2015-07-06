package com.zhiz.japanesegirl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {


    private static String url_get_version = "http://wesley.huhu.tw/japan/get_version_name.php";
    private static String url_updata_instal = "http://wesley.huhu.tw/japan/update_instal_count.php";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_VERSION = "version";
    private static final String TAG_VALUE = "value";
    private static final String TAG_INSTAL = "instal";
    private static final String TAG_USERNAME = "username";

    RelativeLayout oRlay;
    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();
    // products JSONArray
    JSONArray data = null;
    ProgressDialog pDialog;
    ProgressDialog mProgressDialog;
    AlertDialog pDownloadDialog;

    String sVersionNameNow;
    String sVersionNameMySQL;
    String sInstal;
    String sUserName;
    boolean bInstal = false;

    //上次按下返回键的系统时间
    private long lastBackTime = 0;
    //当前按下返回键的系统时间
    private long currentBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        oRlay = (RelativeLayout) findViewById(R.id.rlay);

        //起始淡入動畫
        Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.first_view);
        oRlay.setAnimation(alphaAnimation);

        //取得PackageName版本資訊
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            sVersionNameNow = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //檢查是否已有apk 有即刪除
        File f=new File("/sdcard/Download/JapaneseGirl.apk");
        if(f.exists()) {
            f.delete();
        }

        //讀取偏好設定
        SharedPreferences settings = getSharedPreferences(TAG_INSTAL, 0);
        sInstal = settings.getString(TAG_INSTAL,"");
        sUserName = settings.getString(TAG_USERNAME,"Player");

        //第一次開啟後設instal值為t
        if("".equals(sInstal)){
            bInstal = false;
            settings.edit().putString(TAG_INSTAL,"t").commit();
        }else{
            //已有數值就不做更新
            bInstal = true;
        }

        //執行檢查版本Class
        new GetVersionName().execute();




    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent it = new Intent(MainActivity.this, GameMenu.class);
            it.putExtra(TAG_USERNAME,sUserName);
            startActivity(it);
            overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            finish();
        }
    };


    class GetVersionName extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("資料讀取中，請稍後..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            //從資料庫取得最新版本並存入變數
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair("vername", TAG_VERSION));
            try {
                //如果是第一次開啟 將資料庫value數值+1
                if(!bInstal){
                    jParser.makeHttpRequest(url_updata_instal, "POST", param);
                }

                JSONObject json = jParser.makeHttpRequest(url_get_version, "GET", param);
                // Check your log cat for JSON reponse
                Log.d("All Data: ", json.toString());

                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    data = json.getJSONArray(TAG_VERSION);
                    JSONObject c = data.getJSONObject(0);
                    sVersionNameMySQL = c.getString(TAG_VALUE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            //版本判斷
            if(sVersionNameNow.equals(sVersionNameMySQL)){
               // Toast.makeText(MainActivity.this,"版本一致",Toast.LENGTH_LONG).show();

                //一秒後換頁
                handler.sendMessageDelayed(new Message(), 1000);
            }else {
          //      Toast.makeText(MainActivity.this,"版本不同",Toast.LENGTH_LONG).show();

                pDownloadDialog = new AlertDialog.Builder(MainActivity.this).create();
                pDownloadDialog.setMessage("有最新版本，請按確定開始下載");
                pDownloadDialog.setCancelable(false);
                //   pDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDownloadDialog.setButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 執行DownloadTask Class 下載檔案
                        final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
                        downloadTask.execute("http://wesley.huhu.tw/japan/JapaneseGirl.apk");

                    }
                });
                pDownloadDialog.show();


            }

        }
    }


    // usually, subclasses of AsyncTask are declared inside the activity class.
// that way, you can easily modify the UI thread from here
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download

            // instantiate it within the onCreate method
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage("下載檔案中...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);


            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }


        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            FileOutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // 檔案儲存路徑
                input = connection.getInputStream();
                output = new FileOutputStream("/sdcard/Download/JapaneseGirl.apk");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();

            //判斷是否下載成功
            if (result != null) {
                    Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }
            else {
            //    Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

                //使用內建安裝程式開啟apk
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String patht = Environment.getExternalStorageDirectory().getPath();
                    File file = new File(patht + "/Download/" + "JapaneseGirl.apk");
                    if(file.exists()){
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        //切換到開啟檔案的app，注意!如果手機沒有可開啟檔案的程式，則會出現ActivityNotFoundException
                        startActivity(intent);
                    }else{
                        Log.i("ruby", file.getCanonicalPath() + " file isn't exists");
                    }
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "找不到檔案", Toast.LENGTH_SHORT).show();
                } catch (ActivityNotFoundException e) {
                    //手機上沒有任何應用程式可開啟此檔
                    Toast.makeText(MainActivity.this, "沒有任何APP可以開啟此檔案", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                }
            }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
