package de.opmlg.betternotes;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Xml;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class NoteManager {
    public void addNote(ArrayAdapter<String> noteListAdapter, String name, Context context) {
        String fileName = "/" + name + ".xml";
        String folderName = "/" + name;
        File noteFile = new File(MainActivity.filesDir + fileName);
        File noteFolder = new File(MainActivity.filesDir + folderName);
        if (!noteFile.exists()){
            try {
                FileOutputStream outputStream = new FileOutputStream(noteFile);
                OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                writer.write("");
                writer.flush();
                writer.close();
                noteListAdapter.add(name);
                noteListAdapter.notifyDataSetChanged();
                noteFolder.mkdirs();
            }
            catch (Exception ex) {
                Toast toast = Toast.makeText(context, context.getString(R.string.err_create_new), Toast.LENGTH_LONG);
                toast.setView(View.inflate(context, R.layout.toast_layout, null));
                TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
                String message = context.getString(R.string.err_create_new);
                toastText.setText(message);
                toast.show();
            }
        }
        else {
            Toast toast = Toast.makeText(context, context.getString(R.string.err_already_exists), Toast.LENGTH_LONG);
            toast.setView(View.inflate(context, R.layout.toast_layout, null));
            TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
            String message = context.getString(R.string.err_already_exists);
            toastText.setText(message);
            toast.show();
        }
    }

    public void loadNotes(ArrayAdapter<String> noteListAdapter){
        File dataDir = new File(MainActivity.filesDir);

        for (File file: Objects.requireNonNull(dataDir.listFiles())) {
            if (file.getName().contains(".xml")){
                noteListAdapter.add(file.getName().substring(0, file.getName().length() - 4));
            }
        }
        noteListAdapter.notifyDataSetChanged();
    }

    public void deleteNote(ArrayAdapter<String> noteListAdapter, int position, Context context) {
        String noteName = noteListAdapter.getItem(position);
        File file = new File(MainActivity.filesDir + "/" + noteName + ".xml");
        File folder = new File(MainActivity.filesDir + "/" + noteName);
        if (folder.exists()) {
            if (folder.isDirectory()){
                String[] children = folder.list();
                if (children != null){
                    for (String child : children) {
                        new File(folder, child).delete();
                    }
                }
                folder.delete();

            }
        }
        if (file.exists()){
            if (file.delete()){
                noteListAdapter.remove(noteListAdapter.getItem(position));

                Toast toast = Toast.makeText(context, noteName + " " + context.getString(R.string.note_delete_success), Toast.LENGTH_LONG);
                toast.setView(View.inflate(context, R.layout.toast_layout, null));
                TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
                String message = noteName + " " + context.getString(R.string.note_delete_success);
                toastText.setText(message);
                toast.show();
                return;
            }
        }

        Toast toast = Toast.makeText(context, context.getString(R.string.err_note_delete), Toast.LENGTH_LONG);
        toast.setView(View.inflate(context, R.layout.toast_layout, null));
        TextView toastText = (TextView) toast.getView().findViewById(R.id.message);
        String message = context.getString(R.string.err_note_delete);
        toastText.setText(message);
        toast.show();
    }

    public void saveNoteContent(String noteName, ArrayList<Element> elementList, Context context) throws FileNotFoundException {
        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        OutputStream outputStream = new FileOutputStream(MainActivity.filesDir + "/" + noteName + ".xml");

        try{
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "Elements");
            for (Element element : elementList){
                serializer.startTag("", "Element");

                serializer.startTag("", "type");
                try{serializer.text(element.type);}
                catch (Exception ex){serializer.text("");}
                serializer.endTag("", "type");

                serializer.startTag("", "text");
                try{serializer.text(element.text);}
                catch (Exception ex){serializer.text("");}
                serializer.endTag("", "text");

                serializer.startTag("", "imageUri");
                try{serializer.text(element.imageUri.toString());}
                catch (Exception ex){serializer.text("");}
                serializer.endTag("", "imageUri");

                serializer.endTag("", "Element");
            }
            serializer.endTag("", "Elements");
            serializer.endDocument();
            String result = writer.toString();
            outputStream.write(result.getBytes(), 0, result.length());
            outputStream.close();
        }
        catch (Exception ex){

        }
    }

    public ArrayList<Element> readNoteContent(String noteName){
        Document document;
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        ArrayList<Element> noteElements = new ArrayList<>();

        try {
            builder = builderFactory.newDocumentBuilder();
            FileInputStream inputStream = new FileInputStream(MainActivity.filesDir + "/" + noteName + ".xml");
            document = builder.parse(inputStream);
            NodeList elements = document.getElementsByTagName("Element");
            String result = "";
            for(int i = 0; i < elements.getLength(); i++){
                Node element = elements.item(i);
                Element elementObject = new Element();
                NodeList elementData = element.getChildNodes();
                for (int j = 0; j < elementData.getLength(); j++){
                    if (elementData.item(j).getNodeName().equals("type")){
                        elementObject.type = elementData.item(j).getTextContent();
                    }
                    if (elementData.item(j).getNodeName().equals("text")){
                        elementObject.text = elementData.item(j).getTextContent();
                    }
                    if (elementData.item(j).getNodeName().equals("imageUri")){
                        elementObject.imageUri = Uri.parse(elementData.item(j).getTextContent());
                    }
                }
                noteElements.add(elementObject);
            }
        }
        catch (Exception ex){

        }
        return noteElements;
    }
}
