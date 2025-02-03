package com.example.lovelink;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Referencia al botón de registro
        Button signupButton = findViewById(R.id.signupButton);

        // Configurar el botón de registro
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la primera pantalla de configuración del perfil
                Intent intent = new Intent(SignUpActivity.this, ProfileSetup1Activity.class);
                startActivity(intent);
            }
        });
    }
}