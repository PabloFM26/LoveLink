package com.example.lovelink;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import java.util.Calendar;

public class ProfileSetup1Activity extends Activity {

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText birthdayEditText;
    private EditText cityEditText;
    private String selectedGender = ""; // Almacena el género seleccionado
    private int userAge = 0; // Edad calculada a partir de la fecha

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_1);

        // Referencias de los campos
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText = findViewById(R.id.surnameEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        cityEditText = findViewById(R.id.cityEditText);
        Button continueButton = findViewById(R.id.continueButton);

        // Referencias a los botones de género
        TextView genderMale = findViewById(R.id.genderMale);
        TextView genderFemale = findViewById(R.id.genderFemale);
        TextView genderOther = findViewById(R.id.genderOther);
        TextView genderNoSay = findViewById(R.id.genderNoSay);

        // Selector de género visual
        TextView[] genderOptions = {genderMale, genderFemale, genderOther, genderNoSay};

        View.OnClickListener genderClickListener = view -> {
            for (TextView option : genderOptions) {
                option.setSelected(false);
                option.setTextColor(getResources().getColor(R.color.gender_unselected));
            }

            view.setSelected(true);
            ((TextView) view).setTextColor(getResources().getColor(R.color.gender_selected));
            selectedGender = ((TextView) view).getText().toString();
        };

        for (TextView option : genderOptions) {
            option.setOnClickListener(genderClickListener);
        }

        // Lógica para el selector de fecha de nacimiento
        birthdayEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Calcular edad
                        userAge = year - selectedYear;
                        Calendar today = Calendar.getInstance();
                        if (today.get(Calendar.DAY_OF_YEAR) < Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) {
                            userAge--;
                        }

                        birthdayEditText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    },
                    year, month, day
            );
            picker.show();
        });

        // Botón continuar
        continueButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String surname = surnameEditText.getText().toString().trim();
            String birthday = birthdayEditText.getText().toString().trim();
            String city = cityEditText.getText().toString().trim();

            // Validación simple
            if (selectedGender.isEmpty()) {
                Toast.makeText(this, "Por favor, selecciona un género", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pasar datos a ProfileSetup2Activity
            Intent intent = new Intent(ProfileSetup1Activity.this, ProfileSetup2Activity.class);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            intent.putExtra("gender", selectedGender);
            intent.putExtra("birthday", birthday);
            intent.putExtra("age", userAge);
            intent.putExtra("city", city);
            startActivity(intent);
        });
    }
}
