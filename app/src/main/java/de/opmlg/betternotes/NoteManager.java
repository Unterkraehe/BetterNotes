package de.opmlg.betternotes;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Objects;


public class NoteManager {
    public void addNote(ArrayAdapter<String> noteListAdapter, String name, Context context) {
        String fileName = "/" + name.replace(' ' , '_') + ".xml";
        File noteFile = new File(MainActivity.filesDir + fileName);
        if (!noteFile.exists()){
            try {
                FileOutputStream outputStream = new FileOutputStream(noteFile);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write("");
                writer.flush();
                writer.close();
                noteListAdapter.add(name);
                noteListAdapter.notifyDataSetChanged();
            }
            catch (Exception ex) {
                Toast toast = Toast.makeText(context, R.string.err_create_new, Toast.LENGTH_LONG);
                View toastView = toast.getView();
                toastView.setBackground(context.getDrawable(R.drawable.buttons));
                TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
                toastText.setTextColor(Color.WHITE);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(context, R.string.err_already_exists, Toast.LENGTH_LONG);
            View toastView = toast.getView();
            toastView.setBackground(context.getDrawable(R.drawable.buttons));
            TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
            toastText.setTextColor(Color.WHITE);
            toast.show();
        }
    }

    public void loadNotes(ArrayAdapter<String> noteListAdapter){
        File dataDir = new File(MainActivity.filesDir);

        for (File file: Objects.requireNonNull(dataDir.listFiles())) {
            noteListAdapter.add(file.getName().substring(0, file.getName().length() - 4).replace('_', ' '));
        }
        noteListAdapter.notifyDataSetChanged();
    }

    public void deleteNote(ArrayAdapter<String> noteListAdapter, int position, Context context) {
        String noteName = noteListAdapter.getItem(position);
        File file = new File(MainActivity.filesDir + "/" + noteName.replace(' ', '_') + ".xml");
        if (file.exists()){
            if (file.delete()){
                noteListAdapter.remove(noteListAdapter.getItem(position));

                Toast toast = Toast.makeText(context, noteName + " " + context.getString(R.string.note_delete_success), Toast.LENGTH_LONG);
                View toastView = toast.getView();
                toastView.setBackground(context.getDrawable(R.drawable.buttons));
                TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
                toastText.setTextColor(Color.WHITE);
                toast.show();
                return;
            }
        }
        Toast toast = Toast.makeText(context, context.getString(R.string.err_note_delete), Toast.LENGTH_LONG);
        View toastView = toast.getView();
        toastView.setBackground(context.getDrawable(R.drawable.buttons));
        TextView toastText = (TextView) toast.getView().findViewById(android.R.id.message);
        toastText.setTextColor(Color.WHITE);
        toast.show();
    }
}
