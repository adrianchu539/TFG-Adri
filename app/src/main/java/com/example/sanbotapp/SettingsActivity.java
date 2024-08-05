package com.example.sanbotapp;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.qihancloud.opensdk.base.TopBaseActivity;

public class SettingsActivity extends TopBaseActivity {

    private Button botonAceptar;
    private Button botonAtras;
    private int volumenSanbot;
    private AudioManager audioManager;
    private CheckBox checkConversacionAutomatica;
    private CheckBox checkModoTeclado;
    private Button botonPersonalizacion;
    private Boolean conversacionAuto;
    private Boolean modoTeclado;

    private static final int MAX_VOLUMEN_SANBOT = 12;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_ajustes);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Creo una sección de almacenamiento local donde se guardará la voz seleccionada del robot
        SharedPreferences sharedPrefVolumen = this.getSharedPreferences("volumen", MODE_PRIVATE);
        SharedPreferences.Editor editorVolumen = sharedPrefVolumen.edit();

        // Creo una sección de almacenamiento local donde se guardará si la conversación automática está activada
        SharedPreferences sharedPrefConversacionAuto = this.getSharedPreferences("conversacionAutomatica", MODE_PRIVATE);
        SharedPreferences.Editor editorConversacionAuto = sharedPrefConversacionAuto.edit();

        // Creo una sección de almacenamiento local donde se guardará si está el modo teclado activado
        SharedPreferences sharedPrefModoTeclado = this.getSharedPreferences("modoTeclado", MODE_PRIVATE);
        SharedPreferences.Editor editorModoTeclado = sharedPrefModoTeclado.edit();

        // Obtengo los datos almacenados
        volumenSanbot = sharedPrefVolumen.getInt("volumenSanbot", 0);
        conversacionAuto = sharedPrefConversacionAuto.getBoolean("conversacionAutomatica", false);
        modoTeclado = sharedPrefModoTeclado.getBoolean("modoTeclado", false);

        try {
            botonAceptar = findViewById(R.id.botonAceptar);
            botonAtras = findViewById(R.id.botonAtras);
            checkConversacionAutomatica = findViewById(R.id.checkboxConversacionAutomatica);
            checkModoTeclado = findViewById(R.id.checkboxModoTeclado);
            SeekBar volumenSB = findViewById(R.id.seekBarVolumen);
            volumenSB.setMax(MAX_VOLUMEN_SANBOT);
            TextView visualizarVolumen = findViewById(R.id.visualizarVolumen);
            Button volumenUp = findViewById(R.id.botonSubirVolumen);
            Button volumenDown = findViewById(R.id.botonBajarVolumen);

            volumenSB.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            visualizarVolumen.setText("El volumen es de " + volumenSB.getProgress() + "/" + volumenSB.getMax());

            if(conversacionAuto){
                checkConversacionAutomatica.setChecked(true);
            }
            else{
                checkConversacionAutomatica.setChecked(false);
            }
            if(modoTeclado){
                checkModoTeclado.setChecked(true);
            }
            else{
                checkModoTeclado.setChecked(false);
            }

            checkConversacionAutomatica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        conversacionAuto = true;
                        Log.d("Checked?", "si");
                    }
                    else{
                        conversacionAuto = false;
                        Log.d("Checked?", "no");
                    }
                }
            });
            checkModoTeclado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b){
                        modoTeclado = true;
                        Log.d("Checked?", "si");
                    }
                    else{
                        modoTeclado = false;
                        Log.d("Checked?", "no");
                    }
                }
            });
            botonAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    editorVolumen.putInt("volumenSanbot", volumenSanbot);
                    editorVolumen.apply();
                    editorConversacionAuto.putBoolean("conversacionAutomatica", checkConversacionAutomatica.isChecked());
                    Log.d("conversacionAutomatica", "conversacion automatica es " + checkConversacionAutomatica.isChecked());
                    editorModoTeclado.putBoolean("modoTeclado", checkModoTeclado.isChecked());
                    Log.d("conversacionAutomatica", "modo teclado es " + checkModoTeclado.isChecked());
                    editorConversacionAuto.apply();
                    editorModoTeclado.apply();
                    finish();
                }
            });

            volumenSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    visualizarVolumen.setText("El volumen es de " + progress + "/" + volumenSB.getMax());
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

            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    Intent menuConfiguracionActivity = new Intent(SettingsActivity.this, MenuConfiguracion.class);
                    startActivity(menuConfiguracionActivity);
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
