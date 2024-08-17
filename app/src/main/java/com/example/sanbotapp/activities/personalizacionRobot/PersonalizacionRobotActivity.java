package com.example.sanbotapp.activities.personalizacionRobot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.sanbotapp.activities.TutorialModuloConversacionalActivity;
import com.example.sanbotapp.activities.personalizacionUsuario.EdadUsuarioActivity;
import com.example.sanbotapp.activities.ModuloConversacionalActivity;
import com.example.sanbotapp.R;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.example.sanbotapp.robotControl.SpeechControl;
import com.qihancloud.opensdk.base.TopBaseActivity;
import com.qihancloud.opensdk.beans.FuncConstant;
import com.qihancloud.opensdk.function.unit.SpeechManager;


public class PersonalizacionRobotActivity extends TopBaseActivity {

    private SpeechManager speechManager;
    private static String vozSeleccionada;
    private Button botonAceptar;
    private Button botonAtras;
    private Button botonOmitir;
    private Button botonGrabar;
    private EditText contextoPersonalizacion;
    private Spinner dropdownVoz;
    private Spinner dropdownGeneroRobot;
    private Spinner dropdownGrupoEdadRobot;
    private int dropdownIndexVoz;
    private int dropdownIndexGeneroRobot;
    private int dropdownIndexGrupoEdadRobot;
    private String generoSeleccionado;
    private String grupoEdadSeleccionado;

    private SpeechControl speechControl;
    private GestionSharedPreferences gestionSharedPreferences;
    private Handler handler = new Handler(Looper.getMainLooper());

    /*
    public void onResume(){
        super.onResume();
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menuConfiguracionActivity = new Intent(PersonalizacionRobotActivity.this, MenuConfiguracionActivity.class);
                startActivity(menuConfiguracionActivity);
                finish();
            }
        });
    }

     */

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo_personalizacion_robot);

        speechManager = (SpeechManager) getUnitManager(FuncConstant.SPEECH_MANAGER);
        speechControl = new SpeechControl(speechManager);
        gestionSharedPreferences = new GestionSharedPreferences(this);

        /*
        // Creo una sección de almacenamiento local donde se guardará la voz seleccionada del robot
        vozSeleccionada = gestionSharedPreferences.getStringSharedPreferences("vozSeleccionada", null);
        SharedPreferences.Editor editorVoz = sharedPrefVoz.edit();

        // Creo una sección de almacenamiento local donde se guardará el género del robot
        SharedPreferences sharedPrefGenero = this.getSharedPreferences("generoRobotPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorGenero = sharedPrefGenero.edit();

        // Creo una sección de almacenamiento local donde se guardará el grupo de edad del robot
        SharedPreferences sharedPrefGrupoEdad = this.getSharedPreferences("grupoEdadRobotPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorGrupoEdad = sharedPrefGrupoEdad.edit();

        // Creo una sección de almacenamiento local donde se guardará el contexto
        SharedPreferences sharedPrefContexto = this.getSharedPreferences("contextoPersonalizacion", MODE_PRIVATE);
        SharedPreferences.Editor editorContexto = sharedPrefContexto.edit();

         */

        dropdownIndexVoz = gestionSharedPreferences.getIntSharedPreferences("dropdownIndexVoz", 0);
        dropdownIndexGeneroRobot = gestionSharedPreferences.getIntSharedPreferences("dropdownIndexGeneroRobot", 0);
        dropdownIndexGrupoEdadRobot = gestionSharedPreferences.getIntSharedPreferences("dropdownIndexGrupoEdadRobot", 0);


        dropdownVoz = findViewById(R.id.spinnerVoces);
        contextoPersonalizacion = findViewById(R.id.textoContexto);
        botonAceptar = findViewById(R.id.botonAceptar);
        botonAtras = findViewById(R.id.botonAtras);
        botonOmitir = findViewById(R.id.botonOmitir);
        botonGrabar = findViewById(R.id.botonGrabarRespuesta);
        dropdownGeneroRobot = findViewById(R.id.spinnerGeneroRobot);
        dropdownGrupoEdadRobot = findViewById(R.id.spinnerGrupoEdadRobot);

        // Creo el seleccionador de voces
        String[] items = new String[]{"Sanbot","Alloy", "Echo", "Fable", "Onyx", "Nova", "Shimmer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(PersonalizacionRobotActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdownVoz.setAdapter(adapter);

        dropdownVoz.setSelection(dropdownIndexVoz);

        dropdownVoz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                vozSeleccionada = items[position];
                dropdownIndexVoz = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                // ...
            }

        });

        // Creo el seleccionador de género de robot
        String[] itemsGeneroRobot = new String[]{"-", "Masculino", "Femenino"};
        ArrayAdapter<String> adapterGeneroRobot = new ArrayAdapter<>(PersonalizacionRobotActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsGeneroRobot);
        dropdownGeneroRobot.setAdapter(adapterGeneroRobot);

        dropdownGeneroRobot.setSelection(dropdownIndexGeneroRobot);

        dropdownGeneroRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                generoSeleccionado = itemsGeneroRobot[position];
                dropdownIndexGeneroRobot = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                // ...
            }

        });

        // Creo el seleccionador de grupo de edad del robot
        String[] itemsGrupoEdadRobot = new String[]{"-", "Niño", "Adolescente", "Adulto", "Anciano"};
        ArrayAdapter<String> adapterGrupoEdadRobot = new ArrayAdapter<>(PersonalizacionRobotActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsGrupoEdadRobot);
        dropdownGrupoEdadRobot.setAdapter(adapterGrupoEdadRobot);

        dropdownGrupoEdadRobot.setSelection(dropdownIndexGeneroRobot);

        dropdownGrupoEdadRobot.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("dropdown", itemsGrupoEdadRobot[position]);
                grupoEdadSeleccionado = itemsGrupoEdadRobot[position];
                dropdownIndexGrupoEdadRobot = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> selectedItemView) {
                // ...
            }

        });

        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("voz", "voy a meter " + vozSeleccionada);
                gestionSharedPreferences.putStringSharedPreferences("vozSeleccionada", "vozSeleccionada", vozSeleccionada);
                gestionSharedPreferences.putIntSharedPreferences("vozSeleccionada", "dropdownIndexVoz", dropdownIndexVoz);

                if (grupoEdadSeleccionado.equals("-")) {
                    gestionSharedPreferences.putStringSharedPreferences("grupoEdadRobotPersonalizacion", "grupoEdadRobotPersonalizacion", null);
                    gestionSharedPreferences.putIntSharedPreferences("grupoEdadRobotPersonalizacion", "dropdownIndexGrupoEdadRobot", 0);
                } else {
                    gestionSharedPreferences.putStringSharedPreferences("grupoEdadRobotPersonalizacion", "grupoEdadRobotPersonalizacion", grupoEdadSeleccionado);
                    gestionSharedPreferences.putIntSharedPreferences("grupoEdadRobotPersonalizacion", "dropdownIndexGrupoEdadRobot", dropdownIndexGrupoEdadRobot);
                }

                if (generoSeleccionado.equals("-")) {
                    gestionSharedPreferences.putStringSharedPreferences("generoRobotPersonalizacion", "generoRobotPersonalizacion", null);
                    gestionSharedPreferences.putIntSharedPreferences("generoRobotPersonalizacion", "dropdownIndexGeneroRobot", 0);
                } else {
                    gestionSharedPreferences.putStringSharedPreferences("generoRobotPersonalizacion", "generoRobotPersonalizacion", generoSeleccionado);
                    gestionSharedPreferences.putIntSharedPreferences("generoRobotPersonalizacion", "dropdownIndexGeneroRobot", dropdownIndexGeneroRobot);
                }
                if (contextoPersonalizacion.getText().equals(null)) {
                    gestionSharedPreferences.putStringSharedPreferences("contextoPersonalizacion", "contextoPersonalizacion", null);
                } else {
                    String contexto = String.valueOf(contextoPersonalizacion.getText());
                    gestionSharedPreferences.putStringSharedPreferences("contextoPersonalizacion", "contextoPersonalizacion", contexto);
                }
                // Pasamos a la actividad de modulo conversacional
                Intent moduloConversacionalActivity = new Intent(PersonalizacionRobotActivity.this, ModuloConversacionalActivity.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });

        botonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run(){
                        String respuesta = speechControl.modoEscucha();
                        while (respuesta.isEmpty()) {
                        }

                        Log.d("respuesta", "el valor de respuesta es " + respuesta);

                        // Una vez que la variable tiene valor, ejecuta la acción en el hilo principal
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                handler.removeCallbacksAndMessages(null);
                                contextoPersonalizacion.setText(respuesta);
                            }
                        });
                    }
                }).start();
            }
        });
        botonOmitir.setOnClickListener(new View.OnClickListener() {
            // Pasamos a la actividad de modulo conversacional
            @Override
            public void onClick(View v) {
                Intent moduloConversacionalActivity = new Intent(PersonalizacionRobotActivity.this, TutorialModuloConversacionalActivity.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });
        botonAtras.setOnClickListener(new View.OnClickListener() {
            // Pasamos a la actividad de cuestionario edad
            @Override
            public void onClick(View v) {
                Intent cuestionarioEdadActivity = new Intent(PersonalizacionRobotActivity.this, EdadUsuarioActivity.class);
                startActivity(cuestionarioEdadActivity);
                finish();
            }
        });
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
