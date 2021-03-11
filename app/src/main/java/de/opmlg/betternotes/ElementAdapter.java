package de.opmlg.betternotes;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class ElementAdapter extends ArrayAdapter<String> {

    Context context;
    static ArrayList<Element> elementData;


    ElementAdapter(Context c, ArrayList<Element> element) {
        super(c, R.layout.element);
        this.context = c;
        elementData = element;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.element, parent, false);

        if (position < elementData.size()) {
            if (elementData.get(position).type.equals(elementData.get(position).TYPE_TEXT)) {
                EditText text_TextView = row.findViewById(R.id.elText_EditText);
                text_TextView.setVisibility(View.VISIBLE);
                text_TextView.setText(elementData.get(position).text);
                text_TextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        elementData.get(position).text = s.toString();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                text_TextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        elementData.get(position).textCursorPosition = text_TextView.getSelectionEnd();
                    }
                });
                try {
                    text_TextView.setSelection(elementData.get(position).textCursorPosition);
                }
                catch (Exception ex){

                }
            }

            if (elementData.get(position).type.equals(elementData.get(position).TYPE_IMAGE)) {
                ImageView image_ImageView = row.findViewById(R.id.elImage_ImageView);
                image_ImageView.setVisibility(View.VISIBLE);
                image_ImageView.setImageURI(elementData.get(position).imageUri);
            }
        }

        return row;
    }
}
