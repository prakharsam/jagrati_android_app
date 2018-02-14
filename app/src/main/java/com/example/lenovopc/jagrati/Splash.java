package com.example.lenovopc.jagrati;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by lenovo pc on 14-02-2018.
 */
public class Splash extends Activity{



    @Override
    protected void onCreate(Bundle Lenovo) {
        super.onCreate(Lenovo);
        setContentView(R.layout.splash);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);

                }catch(InterruptedException e){

                    e.printStackTrace();

                }finally {

                    Intent openmain = new Intent("com.example.lenovopc.jagrati.MAIN");
                    startActivity(openmain);
                }
            }
        };
        timer.start();
    }

}
