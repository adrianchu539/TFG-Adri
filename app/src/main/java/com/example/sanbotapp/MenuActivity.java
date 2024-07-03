package com.example.sanbotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.qihancloud.opensdk.base.TopBaseActivity;



public class MenuActivity extends TopBaseActivity {

    private Button btn_iniciar_conversacion;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        btn_iniciar_conversacion = findViewById(R.id.btn_iniciar_conversacion);

        btn_iniciar_conversacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsActivity = new Intent(MenuActivity.this, ModuloConversacional.class);
                startActivity(settingsActivity);
            }
        });
    }

    @Override
    protected void onMainServiceConnected() {

    }
}
