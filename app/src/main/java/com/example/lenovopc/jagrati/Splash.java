package com.example.lenovopc.jagrati;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
                    boolean isUserLoggedIn = checkIfUserLoggedIn();
                    if (isUserLoggedIn) {
                        Intent homeIntent = new Intent("com.example.lenovopc.MAIN");
                        startActivity(homeIntent);
                    } else {
                        Intent loginIntent = new Intent("com.example.lenovopc.jagrati.LOGIN");
                        startActivity(loginIntent);
                    }
                }
            }
        };
        timer.start();
    }

    private boolean checkIfUserLoggedIn() {
        // TODO: Add checks if user is already logged in.
        return false;
    }

}