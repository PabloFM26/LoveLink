package com.example.lovelink;

import android.app.Activity;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import java.util.Arrays;
import java.util.List;

public class ProfileSetup2Activity extends Activity {

    private String selectedOrientation = "";
    private String selectedZodiac = "";
    private String selectedIntention = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup_2);

        EditText jobEditText = findViewById(R.id.jobEditText);
        EditText heightEditText = findViewById(R.id.heightEditText);
        Button nextButton = findViewById(R.id.nextButton);

        // === ORIENTACIÓN SEXUAL ===
        List<TextView> orientationOptions = Arrays.asList(
                findViewById(R.id.orientationHetero),
                findViewById(R.id.orientationHomo),
                findViewById(R.id.orientationBi),
                findViewById(R.id.orientationPan),
                findViewById(R.id.orientationAsexual),
                findViewById(R.id.orientationOt)
        );

        View.OnClickListener orientationClickListener = view -> {
            for (TextView option : orientationOptions) {
                option.setSelected(false);
                option.setTextColor(getResources().getColor(R.color.gender_unselected));
                option.setBackgroundResource(R.drawable.gender_selected_magenta);
            }
            view.setSelected(true);
            ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
            view.setBackgroundResource(R.drawable.gender_selected_magenta);
            selectedOrientation = ((TextView) view).getText().toString();
        };

        for (TextView option : orientationOptions) {
            option.setOnClickListener(orientationClickListener);
        }

        // === SIGNO DEL ZODIACO ===
        List<TextView> zodiacOptions = Arrays.asList(
                findViewById(R.id.zodiacAries), findViewById(R.id.zodiacTaurus), findViewById(R.id.zodiacGemini),
                findViewById(R.id.zodiacCancer), findViewById(R.id.zodiacLeo), findViewById(R.id.zodiacVirgo),
                findViewById(R.id.zodiacLibra), findViewById(R.id.zodiacScorpio), findViewById(R.id.zodiacSagittarius),
                findViewById(R.id.zodiacCapricorn), findViewById(R.id.zodiacAquarius), findViewById(R.id.zodiacPisces)
        );

        View.OnClickListener zodiacClickListener = view -> {
            for (TextView option : zodiacOptions) {
                option.setSelected(false);
                option.setTextColor(getResources().getColor(R.color.gender_unselected));
                option.setBackgroundResource(R.drawable.gender_selected_magenta);
            }
            view.setSelected(true);
            ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
            view.setBackgroundResource(R.drawable.gender_selected_magenta);
            selectedZodiac = ((TextView) view).getText().toString();
        };

        for (TextView option : zodiacOptions) {
            option.setOnClickListener(zodiacClickListener);
        }

        // === INTENCIÓN EN LA APP ===
        List<TextView> intentionOptions = Arrays.asList(
                findViewById(R.id.intentionRelationship),
                findViewById(R.id.intentionCasual),
                findViewById(R.id.intentionFriendship),
                findViewById(R.id.intentionUnknown)
        );

        View.OnClickListener intentionClickListener = view -> {
            for (TextView option : intentionOptions) {
                option.setSelected(false);
                option.setTextColor(getResources().getColor(R.color.gender_unselected));
                option.setBackgroundResource(R.drawable.gender_selected_magenta);
            }
            view.setSelected(true);
            ((TextView) view).setTextColor(getResources().getColor(android.R.color.black));
            view.setBackgroundResource(R.drawable.gender_selected_magenta);
            selectedIntention = ((TextView) view).getText().toString();
        };

        for (TextView option : intentionOptions) {
            option.setOnClickListener(intentionClickListener);
        }

        // === BOTÓN CONTINUAR ===
        nextButton.setOnClickListener(v -> {
            String job = jobEditText.getText().toString().trim();
            String height = heightEditText.getText().toString().trim();

            if (selectedOrientation.isEmpty() || selectedZodiac.isEmpty() || selectedIntention.isEmpty()
                    || job.isEmpty() || height.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos antes de continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(ProfileSetup2Activity.this, ProfileSetup3Activity.class);
            intent.putExtra("orientation", selectedOrientation);
            intent.putExtra("zodiac", selectedZodiac);
            intent.putExtra("intention", selectedIntention);
            intent.putExtra("job", job);
            intent.putExtra("height", height);
            startActivity(intent);
        });
    }
}
