package com.example.lenovopc.jagrati;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private Method method;
    private Context object;
    private CheckBox checkBox;

    public DownloadImageTask(ImageView bmImage, Method method, Context object, CheckBox checkBox) {
        this.bmImage = bmImage;
        this.method = method;
        this.object = object;
        this.checkBox = checkBox;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        try {
            Bitmap mIcon11;

            InputStream in = new java.net.URL(urlDisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

            return mIcon11;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        if (bmImage != null) {
            bmImage.setImageBitmap(result);
        }

        if (method != null && object != null) {
            Object[] parameters = new Object[2];
            parameters[0] = result;
            parameters[1] = checkBox;
            try {
                method.invoke(object, parameters);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
