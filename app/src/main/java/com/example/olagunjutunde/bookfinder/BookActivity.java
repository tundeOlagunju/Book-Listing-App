package com.example.olagunjutunde.bookfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;




public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {


    private static final String  LOG_TAG  = BookActivity.class.getName();
     private static String  BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
     private static String query = "";
    private BookAdapter mAdapter;
   private TextView mEmptyStateTextView;
    private  GridView gridView;
   private  Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG,"Test:OnCreate() called:");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        toolbar  = (Toolbar)findViewById(R.id.toolbar);

        //designate the toolbar as the action bar
        setSupportActionBar(toolbar);




    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG,"Test:OnCreateLoader() called:");

        //show the progress bar
        setProgressLoading(true);

        return new BookLoader(this,BASE_URL+query );

    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        Log.i(LOG_TAG,"Test:OnLoadFinished() called:");

        //hide the progress bar when loading is completed
        setProgressLoading(false);

        //make the grid view visible to show the list of books
        gridView.setVisibility(View.VISIBLE);

        //set the empty state of the grid view to this
        mEmptyStateTextView.setText("No results found for "+"\""+query +"\"");

       //set the current title of the tool bar to the search query
        toolbar.setTitle(query);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);

        }
    }


    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG,"Test:OnLoadReset() called:");

        // Loader reset, so we can clear out our existing data.
        // When the activity is being shut down,LoaderManager takes care of destroying the loader
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG,"Test:OnCreateOptionsMenu() called:");

        //find a reference to the empty view in the layout
          mEmptyStateTextView = (TextView)findViewById(R.id.empty_state_text);

        //find a reference to the grid view in the layout
        gridView = (GridView) findViewById(R.id.grid_view);

        //find a reference to the coordinator layout in the layout
        CoordinatorLayout coordinatorLayout= (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        gridView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter (this,new ArrayList<Book>());

        // Set the adapter on the {@link GridView}
        // so the list can be populated in the user interface
        gridView.setAdapter(mAdapter);

        //check if there is internet connectivity
        boolean isConnected = checkInternetConnectivity();


        if (isConnected) {

            // set the empty state to this
             mEmptyStateTextView.setText(R.string.search);

            //display this on the snack bar
           Snackbar sb = Snackbar.make(coordinatorLayout,"Connection Established",Snackbar.LENGTH_SHORT);
            View sbView = sb.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.GREEN);
            textView.setTextSize(15);
            sb.show();


        } else {

            //display this on the snack bar if there is no internet connection
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,"No Connection!",Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            textView.setTextSize(15);
            snackbar.show();

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);

        }

        // inflate the menu
        getMenuInflater().inflate(R.menu.menu_book_list,menu);
        final  MenuItem searchItem = menu.findItem(R.id.action_search);
      final  SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String newText) {

                //hide the keyboard on pressing the search button on the keyboard
                hideKeyboard(BookActivity.this);

                //Update query to text searched
                query = !TextUtils.isEmpty(newText) ? newText : null;//initialize the newText submitted to query if it is not empty

                  //check internet connectivity
                boolean isConnected = checkInternetConnectivity();

                if(isConnected) {
                    //hide the preceding grid list before displaying another
                    gridView.setVisibility(View.GONE);

                    //hide the empty state text view to allow the progress bar to show
                    mEmptyStateTextView.setVisibility(View.GONE);

                    //show the progress bar
                    setProgressLoading(true);

                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = getSupportLoaderManager();

                    //restart loader ensures onCreateLoader is called to fetch new  book data any time the new query is submitted
                    // Restart the loader. Pass in the int ID constant defined above and pass in null for
                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                    // because this activity implements the LoaderCallbacks interface).
                    loaderManager.restartLoader(0, null, BookActivity.this);
                }

                else
                    {
                        //If it is not connected, no need to show the progress bar
                        setProgressLoading(false);

                        //Update the empty view to this
                         mEmptyStateTextView.setText(R.string.no_internet_connection);
                }



                // Set an item click listener on the ListView, which sends an intent to a web browser
                // to open a website with more information about the selected earthquake.
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        // Find the current book that was clicked on

                        Book currentBook = mAdapter.getItem(position);
                        // Convert the String URL into a URI object (to pass into the Intent constructor)
                        Uri bookUri = Uri.parse(currentBook.getBuyLink());

                        // Create a new intent to view the book URI
                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                        // Send the intent to launch a new activity
                        startActivity(websiteIntent);
                    }
                });

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // This is called when the query entered is changed
                //We are not implementing this,so return false
                return false;
            }
        });


                return true;
}


    /**
     * Helper Method to check the connectivity status
     */
    private Boolean checkInternetConnectivity(){

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }



    /**
     *   Helper Method to hide the Keyboard
     **/
    private static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }


    /**
     * Helper Method to hide or show the progress bar
     */
    private void setProgressLoading(boolean isLoading){
   View loadingIndicator = findViewById(R.id.loading_indicator);
    if (isLoading)
    {
        // show the loading indicator
        loadingIndicator.setVisibility(View.VISIBLE);
    }
    else{
        //hide the loading indicator
       loadingIndicator.setVisibility(View.GONE);
    }
}


}
