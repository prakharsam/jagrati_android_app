package com.example.lenovopc.jagrati;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by lenovo pc on 19-01-2018.
 */
public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer = new Thread(){

            public void run(){
                try{
                    sleep(5000);

                }catch(InterruptedException e){

                    e.printStackTrace();

                }finally {
                    Intent main = new Intent("android.intent.action.AFTERMAIN");
                    startActivity(main);

                }
            }

        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
