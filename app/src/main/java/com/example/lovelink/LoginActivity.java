package com.example.lovelink;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginActivity extends Activity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias a los campos de entrada
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Acción del botón de iniciar sesión
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Por favor, ingresa correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar si las credenciales son correctas
            boolean isAuthenticated = authenticateUser(email, password);

            if (isAuthenticated) {
                // Guardar que el usuario ha iniciado sesión
                SharedPreferences.Editor editor = getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("email", email);
                editor.apply();

                // Redirigir a la pantalla principal
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        // Acción del botón de registrarse
        signupButton.setOnClickListener(v -> {
            // Redirigir al usuario a la actividad de registro
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    // Método para autenticar al usuario (consultando la base de datos)
    private boolean authenticateUser(String email, String password) {
        boolean isAuthenticated = false;

        // Establecer conexión con la base de datos
        Connection connection = null;
        ResultSet resultSet = null;

        try {
            // Aquí conectas a tu base de datos usando JDBC o mediante una API REST
            // Ejemplo con JDBC:
            connection = DatabaseHelper.getConnection(); // Asegúrate de tener tu clase DatabaseHelper configurada

            // Realizar consulta SQL para verificar si el correo y la contraseña existen
            String query = "SELECT * FROM LOVELINK_USERS WHERE correo = ? AND contrasena = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            // Si el resultado tiene un registro, significa que las credenciales son correctas
            if (resultSet.next()) {
                isAuthenticated = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Cerrar la conexión a la base de datos
            try {
                if (resultSet != null) resultSet.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isAuthenticated;
    }
}
