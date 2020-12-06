package de.opmlg.betternotes;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView lvNotes;
    private ImageView btnNewNote;
    private ArrayList<String> noteList;
    private ArrayAdapter<String> adapterNoteList;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.buttons));

        lvNotes = (ListView) findViewById(R.id.lvList);
        btnNewNote = findViewById(R.id.btnNewNote);
        noteList = new ArrayList<>();
        for(int i = 0; i < 2; i++){
            noteList.add("Row" + i);
        }
        adapterNoteList = new ArrayAdapter<String>(getApplicationContext(), R.layout.notes_list_item, noteList);
        lvNotes.setAdapter(adapterNoteList);

        btnNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterNoteList.add("YEET");
                adapterNoteList.notifyDataSetChanged();
            }
        });
    }
}