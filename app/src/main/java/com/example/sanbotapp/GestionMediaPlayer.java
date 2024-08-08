package com.example.sanbotapp;

import android.media.MediaDataSource;
import android.media.MediaPlayer;

import com.qihancloud.opensdk.base.TopBaseActivity;

public class GestionMediaPlayer {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    public GestionMediaPlayer(){
    }

    protected boolean mediaPlayerReproduciendose(){
        return mediaPlayer.isPlaying();
    }

    protected void reproducirMediaPlayer(byte[] data){
        mediaPlayer.reset();
        MediaDataSource mediaDataSource = new ByteArrayMediaDataSource(data);
        mediaPlayer.setDataSource(mediaDataSource);
        mediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.prepareAsync();
    }

    protected void pararMediaPlayer(){
        mediaPlayer.stop();
    }
}
