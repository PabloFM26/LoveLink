package com.example.lovelink;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Referencias a los campos del layout
        EditText phoneEditText = findViewById(R.id.phoneEditText); // Este campo es el teléfono
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText password2EditText = findViewById(R.id.password2EditText);
        Button signupButton = findViewById(R.id.signupButton);

        signupButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            String password2 = password2EditText.getText().toString();

            if (phone.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(password2)) {
                Toast.makeText(SignUpActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else {
                // Guardar los datos temporalmente en SharedPreferences para ser utilizados después
                SharedPreferences.Editor editor = getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("phone", phone);
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                // Pasar los datos a la primera pantalla (Formulario 1)
                Intent intent = new Intent(SignUpActivity.this, ProfileSetup1Activity.class);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                startActivity(intent);
                finish();
            }
        });

    }
}
