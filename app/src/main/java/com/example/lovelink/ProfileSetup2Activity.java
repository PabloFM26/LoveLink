package com.example.lovelink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileSetup2Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_2);

        // Referencia al botón de finalizar
        Button finishButton = findViewById(R.id.finishButton);

        // Configurar el botón de finalizar
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la pantalla principal
                Intent intent = new Intent(ProfileSetup2Activity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Cierra la actividad actual
            }
        });
    }
}