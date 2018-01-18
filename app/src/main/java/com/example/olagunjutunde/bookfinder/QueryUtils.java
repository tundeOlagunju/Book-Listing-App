package com.example.olagunjutunde.bookfinder;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.example.olagunjutunde.bookfinder.R.id.authors;
import static java.lang.System.in;
import static java.net.URI.create;

/**
 * Created by OLAGUNJU TUNDE on 11/29/2017.
 */


public class QueryUtils {


    private static final String LOG_TAG = QueryUtils.class.getName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {

    }


    /**
     * Query the Google API and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {

        Log.i(LOG_TAG, "Test:fetBookData() called:");

        // Create URL object from the URL address
        URL url = createUrl(requestUrl);


        //initialize the jsonResponse to an empty string
        String jsonResponse = "";

        try {

            // Perform HTTP request to the URL and receive a JSON response back
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {

            Log.e(LOG_TAG, "Problem Making The HTTP Request", e);
        }

        // Extract relevant fields from the JSON response and create a list of books
        List<Book> books = extractBooksFromJson(jsonResponse);

        // return a list of {@link books}
        return books;

    }


    /**
     * Helper Method that converts the Url address into Url Object
     * Receives the URL address as the input and returns URL Object as the response
     **/
    private static URL createUrl(String StringUrl) {

        //initialize the URL object to null
        URL url = null;



        try {

            url = new URL(StringUrl);

        } catch (MalformedURLException malformedURLException) {

            Log.e(LOG_TAG, "Problem Building the URL", malformedURLException);

        }

        return url;
    }

    /**
     * Helper Method To Make an HTTP request to the given URL.
     * Receives URl object as an input and return String as the response.
     */

    private static String makeHttpRequest(URL url) throws IOException {

        //initialize the jsonResponse String to null
        String jsonResponse = "";


        //return the jsonResponse if url object is null
        if (url == null) {

            return jsonResponse;

        }


        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);

            urlConnection.connect();


            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                //Receive the response as an input stream
                inputStream = urlConnection.getInputStream();

                //Interpret the input stream and receive the jsonResponse
                jsonResponse = readFromStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error Response Code:" + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the BookJson Response", e);

        } finally {

            // disconnect url connection if it is not null
            if (urlConnection != null) {

                urlConnection.disconnect();
            }

            // close the input stream if it is not null
            if (inputStream != null) {

                inputStream.close();
            }


        }

        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */

    private static String readFromStream(InputStream inputStream) throws IOException {


        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();

            }

        }

        return output.toString();
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractBooksFromJson(String jSonResponse) {


        // If the JSON string is empty or null, then return early.
     //   if (TextUtils.isEmpty(jSonResponse)) {
      //      return null;
      //  }


        // Create an empty ArrayList that we can start adding earthquakes to
        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonObject = new JSONObject(jSonResponse);

            JSONArray booksArray = baseJsonObject.getJSONArray("items");

            for (int i = 0; i < booksArray.length(); i++) {

                String authorNames = "";

                JSONObject currentBook = booksArray.getJSONObject(i);

                String selfLink = currentBook.getString("selfLink");
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                // When book has more than one author
                for (int j = 0; j < authorsArray.length(); j++) {
                    authorNames += authorsArray.getString(j);
                    authorNames += "                                                    ";
                }

                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("thumbnail");

                // Loading the bitmap from given imageUrl
                Bitmap bookThumbnail = makeHttpRequest(imageUrl);

             //   JSONObject saleInfo = currentBook.getJSONObject("saleInfo");
             //   String price = saleInfo.getString("saleability");
             //   String buyLink = saleInfo.getString("buyLink");


                Book book = new Book(title, authorNames, bookThumbnail,selfLink);
                books.add(book);
            }


            return books;

        } catch (JSONException jsonException) {

            Log.e(LOG_TAG, "Problem parsing the JSON Response", jsonException);

        } catch (IOException ioException) {

            Log.e(LOG_TAG, "Problem closing the input stream of the image url", ioException);
        }


        return books;
    }



    /**
     * Helper Method To Make an HTTP request to the given imageURL and return a Bitmap as the response.
     * Receives a url address as input and returns a Bitmap
     */
    private static Bitmap makeHttpRequest(String imageUrl) throws IOException {

        //initialize the thumbnail to null
        Bitmap thumbnail = null;

        //return early if the image url address is empty
        if (imageUrl == null) {
            return thumbnail;
        }

        //create an image url object from the address
        URL url = createUrl(imageUrl);

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            //establish HTTP connection with the server
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {

                //receive the response as an input stream
                inputStream = urlConnection.getInputStream();

                //decode the input stream.The response is a Bitmap
                thumbnail = BitmapFactory.decodeStream(inputStream);
            }

        } catch (IOException e)
        {
            Log.e(LOG_TAG, "Error reading the input stream", e);
        } finally
        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return thumbnail;
    }

}
