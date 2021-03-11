package de.opmlg.betternotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity {

    private String noteName = "";
    private ListView elementListView;
    private ElementAdapter elementAdapter;
    private static ArrayList<Element> elementList;
    NoteManager manager = new NoteManager();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));

        Bundle parameters = getIntent().getExtras();
        if (parameters != null) {
            noteName = parameters.getString("noteName");
        } else {
            Toast toast = Toast.makeText(this, this.getString(R.string.err_note_not_found), Toast.LENGTH_LONG);
            toast.setView(View.inflate(this, R.layout.toast_layout, null));
            TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
            String message = this.getString(R.string.err_note_not_found);
            toastText.setText(message);
            toast.show();
        }
        elementList = new ArrayList<>();
        elementList = manager.readNoteContent(noteName);

        elementListView = findViewById(R.id.lvElementList);
        int listViewHeight = elementListView.getHeight();
        elementListView.setDividerHeight(listViewHeight);
        elementListView.setMinimumHeight(listViewHeight);
        elementAdapter = new ElementAdapter(this, elementList);
        for (Element element : elementList) {
            elementAdapter.add("");
        }
        elementListView.setAdapter(elementAdapter);
        ImageView btnNoteAdd = (ImageView) findViewById(R.id.btnNewNote);
        String[] elementsDialogList = {getApplicationContext().getString(R.string.element_text), getApplicationContext().getString(R.string.element_image)};

        setTitle(noteName);

        btnNoteAdd.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.dialog_element_add));

            builder.setItems(elementsDialogList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Element element = new Element();
                    if (which == 0) {
                        element.type = element.TYPE_TEXT;
                        element.text = "";

                        elementList.add(element);
                        elementAdapter.add("");
                        elementAdapter.notifyDataSetChanged();
                    }

                    if (which == 1) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);
                    }
                }
            });
            Dialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.buttons);
        });

        elementListView.setOnItemLongClickListener((parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
            builder.setTitle(getString(R.string.dialog_delete_name));

            TextView content = new TextView(getActivityContext());

            content.setPadding(50, 0, 50, 0);
            content.setText(R.string.dialog_delete_element);
            builder.setView(content);

            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                elementList.remove(position);
                elementAdapter.clear();
                for (Element element : elementList) {
                    elementAdapter.add("");
                }
                elementAdapter.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
            Dialog dialog = builder.create();
            dialog.show();
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.buttons);

            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            manager.saveNoteContent(noteName, elementList, getApplicationContext());
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(this, this.getString(R.string.err_note_save), Toast.LENGTH_LONG);
            toast.setView(View.inflate(this, R.layout.toast_layout, null));
            TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
            String message = this.getString(R.string.err_note_save);
            toastText.setText(message);
            toast.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            manager.saveNoteContent(noteName, elementList, getApplicationContext());
        } catch (FileNotFoundException e) {
            Toast toast = Toast.makeText(this, this.getString(R.string.err_note_save), Toast.LENGTH_LONG);
            toast.setView(View.inflate(this, R.layout.toast_layout, null));
            TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
            String message = this.getString(R.string.err_note_save);
            toastText.setText(message);
            toast.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            try {
                Uri selectedImage = imageReturnedIntent.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                File targetLocation;
                File[] existingFiles = new File(MainActivity.filesDir + "/" + noteName).listFiles();
                if (existingFiles != null) {
                    targetLocation = new File(MainActivity.filesDir + "/" + noteName + "/" + String.valueOf(existingFiles.length + 1) + ".jpg");
                } else {
                    targetLocation = new File(MainActivity.filesDir + "/" + noteName + "/" + "0.jpg");
                }

                try (FileOutputStream out = new FileOutputStream(targetLocation)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Uri imagePath = Uri.fromFile(targetLocation);

                Element element = new Element();
                element.type = element.TYPE_IMAGE;
                element.imageUri = imagePath;
                elementList.add(element);
                elementAdapter.add("");
                elementAdapter.notifyDataSetChanged();
            }
            catch (Exception exception){
                Log.e("YEET", "onActivityResult: ", exception);
            }
        }
    }

    private Context getActivityContext() {
        return this;
    }
}