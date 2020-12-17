package de.opmlg.betternotes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import java.util.Objects;

public class NoteActivity extends AppCompatActivity {

    private String noteName = "";

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));

        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            noteName = parameters.getString("noteName");
        }
        setTitle(noteName);
    }
}