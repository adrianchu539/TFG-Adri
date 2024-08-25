package com.example.sanbotapp.activities.personalizacionUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.sanbotapp.R;
import com.example.sanbotapp.activities.personalizacionRobot.PersonalizacionRobotActivity;
import com.example.sanbotapp.gestion.GestionSharedPreferences;
import com.qihancloud.opensdk.base.TopBaseActivity;

public class EdadUsuarioActivity extends TopBaseActivity {

    // Componentes de la pantalla de cuestionario edad
    private Button botonContinuar;
    private Button botonAtras;
    private EditText edadUsuario;
    private int edad;

    private GestionSharedPreferences gestionSharedPreferences;

    /*
    @Override
    public void onResume() {
        super.onResume();

        // Creo una sección de almacenamiento local donde se guardará la edad del usuario
        SharedPreferences sharedPref = this.getSharedPreferences("edadUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        if(edad!=0){
            String edadString = Integer.toString(edad);
            edadUsuario.setText(edadString);
        }

        botonContinuar.setText("Aceptar");
        Drawable done = getContext().getResources().getDrawable(R.drawable.baseline_done_24);
        botonContinuar.setCompoundDrawablesWithIntrinsicBounds(null, null, done, null);
        botonContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Guardamos en el almacenamiento local la edad del usuario
                editor.putInt("edadUsuario", Integer.parseInt(String.valueOf(edadUsuario.getText())));
                editor.apply();
                Intent moduloConversacionalActivity = new Intent(CuestionarioEdadActivity.this, PersonalizacionRobotActivity.class);
                startActivity(moduloConversacionalActivity);
                finish();
            }
        });
    }

     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuestionario_previo_edad);

        gestionSharedPreferences = new GestionSharedPreferences(this);

        edad = gestionSharedPreferences.getIntSharedPreferences("edadUsuario", 0);

        try {
            botonContinuar = findViewById(R.id.botonContinuar);
            botonAtras = findViewById(R.id.botonAtras);
            edadUsuario = findViewById(R.id.edadUsuario);

            if(edad!=0){
                String edadString = Integer.toString(edad);
                edadUsuario.setText(edadString);
            }
            botonContinuar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // Guardamos en el almacenamiento local la edad del usuario
                    Log.d("Edad usuario", "edad usuario es " + Integer.parseInt(String.valueOf(edadUsuario.getText())));
                    gestionSharedPreferences.putIntSharedPreferences("edadUsuario", "edadUsuario", Integer.parseInt(String.valueOf(edadUsuario.getText())));
                    // Pasamos a la actividad de personalización
                    Intent personalizacionActivity = new Intent(EdadUsuarioActivity.this, PersonalizacionRobotActivity.class);
                    startActivity(personalizacionActivity);
                    finish();
                }
            });
            botonAtras.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){
                    // Pasamos a la actividad de cuestionario nombre
                    Intent cuestionarioNombreActivity = new Intent(EdadUsuarioActivity.this, NombreUsuarioActivity.class);
                    startActivity(cuestionarioNombreActivity);
                    finish();
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
