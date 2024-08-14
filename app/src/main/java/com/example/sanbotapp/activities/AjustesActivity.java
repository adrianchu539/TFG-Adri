package com.example.sanbotapp.activities;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sanbotapp.R;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.example.sanbotapp.robotControl.AudioControl;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;

public class AjustesActivity extends TopBaseActivity {

    private Button botonAceptar;
    private Button botonAtras;
    private int volumenSanbot;
    private int velocidadSanbot;
    private int entonacionSanbot;
    private AudioManager audioManager;
    private CheckBox checkConversacionAutomatica;
    private CheckBox checkModoTeclado;
    private Boolean conversacionAuto;
    private Boolean modoTeclado;
    private SeekBar volumenSB;
    private TextView visualizarVolumen;
    private Button volumenUp;
    private Button volumenDown;
    private SeekBar velocidadSB;
    private TextView visualizarVelocidad;
    private Button speedUp;
    private Button speedDown;
    private SeekBar entonacionSB;
    private TextView visualizarEntonacion;
    private Button intonationUp;
    private Button intonationDown;

    private static final int MAX_VOLUMEN_SANBOT = 12;
    private static final int MAX_VELOCIDAD_SANBOT = 100;
    private static final int MAX_ENTONACION_SANBOT = 100;

    private SpeechManager speechManager;
    private SpeechControl speechControl;
    private AudioControl audioControl;

    private GestionSharedPreferences gestionSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_ajustes);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioControl = new AudioControl(audioManager);
        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        speechControl = new SpeechControl(speechManager);
        gestionSharedPreferences = new GestionSharedPreferences(this);

        // Obtengo los datos almacenados
        volumenSanbot = audioControl.getVolumen();
        velocidadSanbot = speechControl.getVelocidadHabla();
        entonacionSanbot = speechControl.getEntonacionHabla();
        conversacionAuto = gestionSharedPreferences.getBooleanSharedPreferences("conversacionAutomatica", false);
        modoTeclado = gestionSharedPreferences.getBooleanSharedPreferences("modoTeclado", false);


        botonAceptar = findViewById(R.id.botonAceptar);
        botonAtras = findViewById(R.id.botonAtras);
        checkConversacionAutomatica = findViewById(R.id.checkboxConversacionAutomatica);
        checkModoTeclado = findViewById(R.id.checkboxModoTeclado);
        volumenSB = findViewById(R.id.seekBarVolumen);
        volumenSB.setMax(MAX_VOLUMEN_SANBOT);
        visualizarVolumen = findViewById(R.id.visualizarVolumen);
        volumenUp = findViewById(R.id.botonSubirVolumen);
        volumenDown = findViewById(R.id.botonBajarVolumen);
        velocidadSB = findViewById(R.id.seekBarVelocidad);
        velocidadSB.setMax(MAX_VELOCIDAD_SANBOT);
        visualizarVelocidad = findViewById(R.id.visualizarVelocidad);
        speedUp = findViewById(R.id.botonSubirVelocidad);
        speedDown = findViewById(R.id.botonBajarVelocidad);
        entonacionSB = findViewById(R.id.seekBarEntonacion);
        entonacionSB.setMax(MAX_ENTONACION_SANBOT);
        visualizarEntonacion = findViewById(R.id.visualizarEntonacion);
        intonationUp = findViewById(R.id.botonSubirEntonacion);
        intonationDown = findViewById(R.id.botonBajarEntonacion);

        volumenSB.setProgress(audioControl.getVolumen());
        visualizarVolumen.setText("El volumen es de " + volumenSB.getProgress() + "/" + volumenSB.getMax());
        velocidadSB.setProgress(speechControl.getVelocidadHabla());
        visualizarVelocidad.setText("La velocidad es de " + velocidadSB.getProgress() + "/" + velocidadSB.getMax());
        entonacionSB.setProgress(speechControl.getEntonacionHabla());
        visualizarEntonacion.setText("La entonacion es de " + entonacionSB.getProgress() + "/" + entonacionSB.getMax());

        if(conversacionAuto){
            checkConversacionAutomatica.setChecked(true);
        }
        else{
            checkConversacionAutomatica.setChecked(false);
        }
        if(modoTeclado){
            checkModoTeclado.setChecked(true);
            checkConversacionAutomatica.setVisibility(View.VISIBLE);
        }
        else{
            checkModoTeclado.setChecked(false);
            checkConversacionAutomatica.setVisibility(View.INVISIBLE);
            checkConversacionAutomatica.setVisibility(View.GONE);
            checkConversacionAutomatica.setChecked(true);
        }

        checkConversacionAutomatica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkConversacionAutomatica.setChecked(true);
                    Log.d("Checked?", "si");
                }
                else{
                    checkConversacionAutomatica.setChecked(false);
                    Log.d("Checked?", "no");
                }
            }
        });
        checkModoTeclado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    checkConversacionAutomatica.setVisibility(View.VISIBLE);
                    Log.d("Checked?", "si");
                }
                else{
                    checkConversacionAutomatica.setVisibility(View.INVISIBLE);
                    Log.d("Checked?", "no");
                }
            }
        });
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                gestionSharedPreferences.putBooleanSharedPreferences("modoTeclado", "modoTeclado", checkModoTeclado.isChecked());
                if(checkModoTeclado.isChecked()){
                    gestionSharedPreferences.putBooleanSharedPreferences("conversacionAutomatica", "conversacionAutomatica", checkConversacionAutomatica.isChecked());
                }
                else{
                    gestionSharedPreferences.putBooleanSharedPreferences("conversacionAutomatica", "conversacionAutomatica", true);
                }
                finish();
            }
        });

        volumenSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                visualizarVolumen.setText("El volumen es de " + progress + "/" + volumenSB.getMax());
                audioControl.setVolumen(progress);
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
                audioControl.setVolumen(volumenSB.getProgress());
            }
        });

        volumenDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(volumenSB.getProgress() <= 0) {
                    volumenSB.setProgress(0);
                }
                volumenSB.setProgress(volumenSB.getProgress() - 1);
                audioControl.setVolumen(volumenSB.getProgress());
            }
        });

        velocidadSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                visualizarVelocidad.setText("La velocidad es de " + progress + "/" + velocidadSB.getMax());
                speechControl.setVelocidadHabla(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(velocidadSB.getProgress() >= MAX_VELOCIDAD_SANBOT) {
                    velocidadSB.setProgress(MAX_VELOCIDAD_SANBOT);
                }
                velocidadSB.setProgress(velocidadSB.getProgress() + 1);
                speechControl.setVelocidadHabla(velocidadSB.getProgress());
            }
        });

        speedDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(velocidadSB.getProgress() <= 0) {
                    velocidadSB.setProgress(0);
                }
                velocidadSB.setProgress(velocidadSB.getProgress() - 1);
                speechControl.setVelocidadHabla(velocidadSB.getProgress());
            }
        });

        entonacionSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                visualizarEntonacion.setText("La entonación es de " + progress + "/" + entonacionSB.getMax());
                speechControl.setEntonacionHabla(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        intonationUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(entonacionSB.getProgress() >= MAX_ENTONACION_SANBOT) {
                    entonacionSB.setProgress(MAX_ENTONACION_SANBOT);
                }
                entonacionSB.setProgress(entonacionSB.getProgress() + 1);
                speechControl.setEntonacionHabla(entonacionSB.getProgress());
            }
        });

        intonationDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                if(entonacionSB.getProgress() <= 0) {
                    entonacionSB.setProgress(0);
                }
                entonacionSB.setProgress(entonacionSB.getProgress() - 1);
                speechControl.setEntonacionHabla(entonacionSB.getProgress());
            }
        });

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent menuConfiguracionActivity = new Intent(AjustesActivity.this, MenuConfiguracionActivity.class);
                startActivity(menuConfiguracionActivity);
            }
        });




    }

    @Override
    protected void onMainServiceConnected() {

    }

}
