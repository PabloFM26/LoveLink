package com.example.lovelink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileSetup1Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_1);

        // Referencia al botón de continuar
        Button continueButton = findViewById(R.id.continueButton);

        // Configurar el botón de continuar
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la segunda pantalla de configuración del perfil
                Intent intent = new Intent(ProfileSetup1Activity.this, ProfileSetup2Activity.class);
                startActivity(intent);
            }
        });
    }
}