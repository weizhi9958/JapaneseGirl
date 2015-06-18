package com.zhiz.japanesegirl;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;


public class GameMenu extends Activity implements View.OnClickListener {

    ImageView oImgVw_2x2, oImgVw_4x4, oImgVw_6x6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        oImgVw_2x2=(ImageView)findViewById(R.id.imgVw_2x2);
        oImgVw_4x4=(ImageView)findViewById(R.id.imgVw_4x4);
        oImgVw_6x6=(ImageView)findViewById(R.id.imgVw_6x6);
        oImgVw_2x2.setOnClickListener(this);
        oImgVw_4x4.setOnClickListener(this);
        oImgVw_6x6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgVw_2x2:
                Intent it = new Intent(this,Game_2x2.class);
                startActivity(it);
                overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
                break;

        }
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
