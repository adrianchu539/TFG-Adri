package com.example.sanbotapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import com.qihancloud.opensdk.function.unit.MediaManager;
import com.sanbot.opensdk.base.BindBaseActivity;
import com.sanbot.opensdk.beans.FuncConstant;
import com.sanbot.opensdk.function.beans.FaceRecognizeBean;
import com.sanbot.opensdk.function.unit.interfaces.media.FaceRecognizeListener;

import java.io.File;
import java.util.List;

public class FaceRecognitionControl extends BindBaseActivity {

    MediaManager mediaManager;
    @Override
    protected void onMainServiceConnected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        register(FaceRecognitionControl.class);
        super.onCreate(savedInstanceState);
        final ImageView imageView = new ImageView(this);
        setContentView(imageView);

        mediaManager = (MediaManager) getUnitManager(FuncConstant.MEDIA_MANAGER);

       /*mediaManager.setMediaListener(new FaceRecognizeListener() {
            @Override
            public void recognizeResult(List<FaceRecognizeBean> faceRecognizeBean) {
                Bitmap bitmap = mediaManager.getVideoImage();
                if(bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        });*/
    }


    private static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
}




