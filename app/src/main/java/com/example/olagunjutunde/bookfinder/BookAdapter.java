package com.example.olagunjutunde.bookfinder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.R.id.list;

/**
 * Created by OLAGUNJU TUNDE on 11/29/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, List<Book> books) {

        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridItemView = convertView;

        if (gridItemView == null) {

            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            gridItemView = layoutInflater.inflate(R.layout.grid_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView thumbNail = (ImageView) gridItemView.findViewById(R.id.thumbnail);
        thumbNail.setImageBitmap(currentBook.getThumbnail());

        TextView title = (TextView) gridItemView.findViewById(R.id.title);
        title.setText(currentBook.getTitle());

        TextView authors = (TextView) gridItemView.findViewById(R.id.authors);
        authors.setText(currentBook.getAuthors());

        return gridItemView;
    }
}
