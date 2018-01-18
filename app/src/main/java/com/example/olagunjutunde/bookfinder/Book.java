package com.example.olagunjutunde.bookfinder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by OLAGUNJU TUNDE on 11/29/2017.
 */

public class Book {

    private String title;
    private String authors;
    private String buyLink;
    private String publisher;
    private String publishedDate;
    private String price;
    private int pages;
    private double averageRating ;
    private String description;
    private Bitmap thumbnail;


    public Book(String title,
                String authors,
                String publisher,
                String publishedDate,
                String price,
                int pages,
                double averageRating ,
                String description,
                Bitmap thumbnail){


        this.title = title;
        this.authors = authors;
        this.description= description;
        this.averageRating = averageRating;
        this.price = price;
        this.publishedDate=publishedDate;
        this.publisher= publisher;
        this.pages =pages;
        this.thumbnail = thumbnail;

    }
    public Book (String title,String authors,Bitmap thumbnail,String buyLink){
        this.title = title;this.authors= authors;this.thumbnail=thumbnail;
        this.buyLink=buyLink;

    }


    public String getTitle(){return title;}

    public String getAuthors() {
        return authors;
    }

    public int getPages() {
        return pages;
    }

    public String getPublisher() {
        return publisher;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getBuyLink() {
        return buyLink;
    }
}
