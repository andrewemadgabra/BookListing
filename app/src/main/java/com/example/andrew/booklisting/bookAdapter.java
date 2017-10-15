package com.example.andrew.booklisting;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by andrew on 10/11/2017.
 */

public class bookAdapter extends ArrayAdapter<book> {
    private static final String LOG_TAG = book.class.getSimpleName();

    public bookAdapter(Activity context, ArrayList<book> book) {
        super(context, 0, book);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_list, parent, false);
        }
        book currentAndroid = getItem(position);
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(currentAndroid.getTitle());
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentAndroid.getauthor());
        TextView languageTextView = (TextView) listItemView.findViewById(R.id.language);
        languageTextView.setText(currentAndroid.getLanguage());
        return listItemView;
    }
}
