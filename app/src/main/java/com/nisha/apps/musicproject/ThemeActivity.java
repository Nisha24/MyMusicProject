package com.nisha.apps.musicproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class ThemeActivity extends AppCompatActivity {
SwitchCompat switchCompat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);
        switchCompat = findViewById(R.id.switcher);
        Button ok = findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        switchCompat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getActionMasked() == MotionEvent.ACTION_MOVE;
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            switchCompat.setText("Disable Dark Mode");
            /*MainActivity m=new MainActivity();
            m.init();*/
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            switchCompat.setText("Enable Dark Mode");
            /*MainActivity m=new MainActivity();
            m.init();*/
        }

        switchCompat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // When user taps the enable/disable
                // dark mode button
                if (isDarkModeOn) {

                    // if dark mode is on it
                    // will turn it off
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    // it will set isDarkModeOn
                    // boolean to false
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();

                    // change text of Button
                    switchCompat.setText("Enable Dark Mode");
                } else {

                    // if dark mode is off
                    // it will turn it on
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    // it will set isDarkModeOn
                    // boolean to true
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();

                    // change text of Button
                    switchCompat.setText("Disable Dark Mode");
                }
            }
        });
    }
}
