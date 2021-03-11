package de.opmlg.betternotes;

import android.graphics.Bitmap;
import android.net.Uri;

public class Element {
    String type;

    // Text:
    public final String TYPE_TEXT = "text";
    String text;
    int textCursorPosition = 0;

    // Image:
    public final String TYPE_IMAGE = "image";
    Uri imageUri;
}
