package de.opmlg.betternotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ListView lvNotes;
    private ImageView btnNewNote;
    private ArrayList<String> noteList;
    private ArrayAdapter<String> adapterNoteList;
    public NoteManager noteManager;
    public static String filesDir;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));

        noteManager = new NoteManager();
        filesDir = getApplicationContext().getFilesDir().getAbsolutePath();

        lvNotes = (ListView) findViewById(R.id.lvList);
        btnNewNote = findViewById(R.id.btnNewNote);
        noteList = new ArrayList<>();
        adapterNoteList = new ArrayAdapter<>(getApplicationContext(), R.layout.notes_list_item, noteList);
        lvNotes.setAdapter(adapterNoteList);
        noteManager.loadNotes(adapterNoteList);

        btnNewNote.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getMainActivityContext());
            builder.setTitle(getString(R.string.dialog_new_name));

            EditText input = new EditText(getMainActivityContext());

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> noteManager.addNote(adapterNoteList, input.getText().toString(), getApplicationContext()));
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            Dialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.buttons);
            input.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        noteManager.addNote(adapterNoteList, input.getText().toString(), getApplicationContext());
                        dialog.cancel();
                        return true;
                    }
                    return false;
                }
            });
        });

        lvNotes.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, NoteActivity.class);
            Bundle noteName = new Bundle();
            noteName.putString("noteName", (String) parent.getItemAtPosition(position));
            intent.putExtras(noteName); //Put your id to your next Intent
            startActivity(intent);
        });

        lvNotes.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getMainActivityContext());
            builder.setTitle(getString(R.string.dialog_delete_name));

            TextView content = new TextView(getMainActivityContext());

            content.setPadding(50, 0, 50, 0);
            content.setText(R.string.dialog_delete_text);
            builder.setView(content);

            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> noteManager.deleteNote(adapterNoteList, position, getApplicationContext()));
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            Dialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.buttons);
            return true;
        });
    }

    public Context getMainActivityContext() {
        return this;
    }
}