package com.example.sanbotapp;

import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.beans.EmotionsType;
import com.qihancloud.opensdk.function.unit.HandMotionManager;
import com.qihancloud.opensdk.function.unit.HardWareManager;
import com.qihancloud.opensdk.function.unit.HeadMotionManager;
import com.qihancloud.opensdk.function.unit.SpeechManager;
import com.qihancloud.opensdk.function.unit.SystemManager;

import java.io.IOException;

public class SettingsActivity extends TopBaseActivity {

    private Button botonAceptar;

    private static String vozSeleccionada;

    private int dropdownIndex;

    private int volumenSanbot;

    private AudioManager audioManager;

    private static final int MAX_VOLUMEN_SANBOT = 12;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //register(PresentacionActivity.class);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        SharedPreferences sharedPref = this.getSharedPreferences("voces", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        dropdownIndex = sharedPref.getInt("dropdownIndex", 0);
        volumenSanbot = sharedPref.getInt("volumenSanbot", 0);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Log.d("dropdown", "mi posicion es la " + dropdownIndex);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes_modulo);

        try {
            Spinner dropdown = findViewById(R.id.dropdownVoces);
            botonAceptar = findViewById(R.id.botonAceptar);
            SeekBar volumenSB = findViewById(R.id.seekBarVolumen);
            volumenSB.setMax(MAX_VOLUMEN_SANBOT);
            TextView textoPrueba = findViewById(R.id.pruebaVolumen);
            Button volumenUp = findViewById(R.id.volumeUp);
            Button volumenDown = findViewById(R.id.volumeDown);
            //create a list of items for the spinner.
            String[] items = new String[]{"Sanbot","Alloy", "Echo", "Fable", "Onyx", "Nova", "Shimmer"};
            //create an adapter to describe how the items are displayed, adapters are used in several places in android.
            //There are multiple variations of this, but this is the basic variant.
            ArrayAdapter<String> adapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
            //set the spinners adapter to the previously created one.
            dropdown.setAdapter(adapter);

            dropdown.setSelection(dropdownIndex);
            volumenSB.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            textoPrueba.setText("El volumen es de " + volumenSB.getProgress() + "/" + volumenSB.getMax());
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.d("dropdown", items[position]);
                    vozSeleccionada = items[position];
                    dropdownIndex = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> selectedItemView) {
                    Log.d("dropdown", "no he seleccionado nada");
                }

            });
            botonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Log.d("Voz a guardar en SP", vozSeleccionada);
                    editor.putString("voz", vozSeleccionada);
                    editor.putInt("dropdownIndex", dropdownIndex);
                    editor.putInt("volumenSanbot", volumenSanbot);
                    editor.apply();
                    //Intent moduloConversacionalActivity = new Intent(SettingsActivity.this, ModuloConversacional.class);
                    //startActivity(moduloConversacionalActivity);
                    finish();
                }
            });

            volumenSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    textoPrueba.setText("El volumen es de " + progress + "/" + volumenSB.getMax());
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            volumenUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    if(volumenSB.getProgress() >= MAX_VOLUMEN_SANBOT) {
                        volumenSB.setProgress(MAX_VOLUMEN_SANBOT);
                    }
                    volumenSB.setProgress(volumenSB.getProgress() + 1);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumenSB.getProgress(), 0);
                }
            });

            volumenDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    if(volumenSB.getProgress() <= 0) {
                        volumenSB.setProgress(0);
                    }
                    volumenSB.setProgress(volumenSB.getProgress() - 1);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumenSB.getProgress(), 0);
                }
            });


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onMainServiceConnected() {

    }

}
