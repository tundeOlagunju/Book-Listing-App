package com.example.olagunjutunde.bookfinder;


import android.content.Context;

import java.util.List;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.example.olagunjutunde.bookfinder.QueryUtils.fetchBookData;

/**
 * Created by OLAGUNJU TUNDE on 11/29/2017.
 */


/**
 * Loads a list of books by using an AsyncTask to perform the
 * network request to the given URL.
 * Book Loader is a custom Loader which fetches a list of books
 */
public class BookLoader extends android.support.v4.content.AsyncTaskLoader<List<Book>> {

    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();

    /** Query URL */
    private String mUrl;


    /**
     * Constructs a new {@link BookLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
 public  BookLoader(Context context,String url){
     // call to the superclass' constructor
        super(context);
        mUrl = url;
    }



    @Override
    protected void onStartLoading() {

        Log.i(LOG_TAG,"Test:OnStartLoading() called:");
        forceLoad();
    }

    /**
     * This is on a background thread(RT).
     * returns a list of books
     */
    @Override
    public List<Book> loadInBackground() {

        Log.i(LOG_TAG,"Test:LoadInBackGround() called:");

        //return early if the URL to query is null
        if(mUrl  == null){
            return null;
        }
     //Perform the network request, parse the response, and extract a list of books.
       List<Book> books = QueryUtils.fetchBookData(mUrl);

        // informs the loaderManager and passes the data(list of books) to the onLoadFinished() method
        return books;
    }

}

