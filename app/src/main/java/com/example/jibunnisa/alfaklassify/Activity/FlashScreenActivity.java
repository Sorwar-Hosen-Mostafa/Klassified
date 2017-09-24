package com.example.jibunnisa.alfaklassify.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jibunnisa.alfaklassify.MainActivity;
import com.example.jibunnisa.alfaklassify.R;

public class FlashScreenActivity extends AppCompatActivity {

    private ImageView logo;
    private TextView flashscreentext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);

        final Animation zoomin = AnimationUtils.loadAnimation(FlashScreenActivity.this, R.anim.zoom_in);
        final Animation zoomout = AnimationUtils.loadAnimation(FlashScreenActivity.this, R.anim.zoom_out);


        logo= (ImageView) findViewById(R.id.app_logo);
        flashscreentext=(TextView)findViewById(R.id.app_flash_screen_text);

        logo.startAnimation(zoomout);
        flashscreentext.startAnimation(zoomout);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */

                Intent mainIntent = new Intent(FlashScreenActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 1000);


    }
}
