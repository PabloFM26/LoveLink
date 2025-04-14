package com.example.lovelink;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileSetup3Activity extends AppCompatActivity {

    private static final int MAX_IMAGES = 6;
    private List<Uri> imageUris = new ArrayList<>();
    private LinearLayout hobbyListContainer;
    private EditText bioEditText, hobbyInput;
    private Button addHobbyButton, finishButton;
    private ImageView[] imageSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_3);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Recuperar los datos pasados desde las actividades anteriores
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String gender = intent.getStringExtra("gender");
        String birthday = intent.getStringExtra("birthday");
        int age = intent.getIntExtra("age", 0);
        String city = intent.getStringExtra("city");
        String orientation = intent.getStringExtra("orientation");
        String zodiac = intent.getStringExtra("zodiac");
        String intention = intent.getStringExtra("intention");
        String job = intent.getStringExtra("job");
        String height = intent.getStringExtra("height");

        // Referencias
        hobbyListContainer = findViewById(R.id.hobbyListContainer);
        bioEditText = findViewById(R.id.bioEditText);
        hobbyInput = findViewById(R.id.hobbyInput);
        addHobbyButton = findViewById(R.id.addHobbyButton);
        finishButton = findViewById(R.id.finishButton);

        imageSlots = new ImageView[] {
                findViewById(R.id.imageSlot1),
                findViewById(R.id.imageSlot2),
                findViewById(R.id.imageSlot3),
                findViewById(R.id.imageSlot4),
                findViewById(R.id.imageSlot5),
                findViewById(R.id.imageSlot6)
        };

        // Set up image click listener
        for (int i = 0; i < imageSlots.length; i++) {
            int finalI = i;
            imageSlots[i].setOnClickListener(v -> openGalleryOrCamera(finalI));
        }

        // Hobby input
        addHobbyButton.setOnClickListener(v -> addHobby());

        // Finish button
        finishButton.setOnClickListener(v -> finishProfileSetup(intent));
    }

    private void openGalleryOrCamera(int slotIndex) {
        // Open a dialog or two buttons to select gallery or camera
        String[] options = {"Tomar foto", "Seleccionar de la galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera(slotIndex);
                    } else {
                        openGallery(slotIndex);
                    }
                });
        builder.create().show();
    }

    private void openCamera(int slotIndex) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = createImageUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private void openGallery(int slotIndex) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryActivityResultLauncher.launch(intent);
    }

    private Uri createImageUri() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "ProfileImage");
        return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    // Camera Activity Result
    private final ActivityResultCallback<ActivityResult> cameraActivityResultCallback = result -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri photoUri = result.getData().getData();
            updateImageSlot(photoUri, imageUris.size());
        }
    };

    // Gallery Activity Result
    private final ActivityResultCallback<ActivityResult> galleryActivityResultCallback = result -> {
        if (result.getResultCode() == RESULT_OK) {
            Uri selectedImageUri = result.getData().getData();
            updateImageSlot(selectedImageUri, imageUris.size());
        }
    };

    private void updateImageSlot(Uri imageUri, int slotIndex) {
        if (slotIndex < MAX_IMAGES) {
            imageUris.add(imageUri);
            imageSlots[slotIndex].setImageURI(imageUri);
        }
    }

    private void addHobby() {
        String hobby = hobbyInput.getText().toString().trim();
        if (!hobby.isEmpty() && hobbyListContainer.getChildCount() < 5) {
            Button hobbyButton = new Button(this);
            hobbyButton.setText(hobby);
            hobbyButton.setBackgroundResource(R.drawable.rounded_button);
            hobbyButton.setTextColor(getResources().getColor(R.color.black)); // Change the text color to black
            hobbyButton.setBackgroundColor(getResources().getColor(R.color.magenta_500)); // Set background color to magenta
            hobbyButton.setOnClickListener(v -> {
                // Delete the hobby when clicked twice
                hobbyButton.setOnClickListener(v1 -> hobbyListContainer.removeView(hobbyButton));
            });
            hobbyListContainer.addView(hobbyButton);
            hobbyInput.setText("");  // Clear the input after adding
        } else {
            Toast.makeText(this, "Solo puedes agregar hasta 5 hobbies", Toast.LENGTH_SHORT).show();
        }
    }

    private void finishProfileSetup(Intent intent) {
        String bio = bioEditText.getText().toString();
        List<String> hobbies = new ArrayList<>();
        for (int i = 0; i < hobbyListContainer.getChildCount(); i++) {
            Button hobbyButton = (Button) hobbyListContainer.getChildAt(i);
            hobbies.add(hobbyButton.getText().toString());
        }

        if (bio.isEmpty() || hobbies.size() == 0 || imageUris.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the data passed from previous activities
        String phone = intent.getStringExtra("phone");
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");
        String name = intent.getStringExtra("name");
        String surname = intent.getStringExtra("surname");
        String gender = intent.getStringExtra("gender");
        String birthday = intent.getStringExtra("birthday");
        int age = intent.getIntExtra("age", 0);
        String city = intent.getStringExtra("city");
        String orientation = intent.getStringExtra("orientation");
        String zodiac = intent.getStringExtra("zodiac");
        String intention = intent.getStringExtra("intention");
        String job = intent.getStringExtra("job");
        String height = intent.getStringExtra("height");

        // Call the method to insert the data into your database
        insertUserDataToDatabase(phone, email, password, name, surname, gender, birthday, age, city, orientation, zodiac, bio, hobbies, job, intention, height);
    }

    private void insertUserDataToDatabase(String phone, String email, String password, String name, String surname, String gender, String birthday, int age, String city, String orientation, String zodiac, String bio, List<String> hobbies, String job, String intention, String height) {
        // Here you would implement the database insertion logic.
        // Example: Insert data into an SQLite database or send to a server.

        // You can also save the user data in SharedPreferences for persistence across app restarts:
        // Acceso a SharedPreferences correctamente
        SharedPreferences.Editor editor = getSharedPreferences("LoveLinkPrefs", MODE_PRIVATE).edit();

// Guardar valores
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.putString("name", name);
        editor.putString("surname", surname);
        editor.putString("gender", gender);
        editor.putString("birthday", birthday);
        editor.putInt("age", age);
        editor.putString("city", city);
        editor.putString("orientation", orientation);
        editor.putString("zodiac", zodiac);
        editor.putString("bio", bio);
        editor.putString("job", job);
        editor.putString("intention", intention);
        editor.putString("height", height);
        editor.putString("hobbies", String.join(",", hobbies));

// Guardar los cambios
        editor.apply();


        // Redirect user to the next screen (matches screen or home screen)
        Intent intent = new Intent(ProfileSetup3Activity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Activity result launchers
    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), cameraActivityResultCallback);

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), galleryActivityResultCallback);
}
