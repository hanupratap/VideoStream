package com.example.videostream;

import android.os.Parcelable;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;

import java.io.Serializable;

public class Video_Model implements Serializable {
    String name;
    String url;
    String description;
    String pic;
    String id;
    String sub_url;
    Boolean fav;
    String doc_path;




    public Video_Model(String name, String url, String description, String pic, String id, String sub_url, Boolean fav, String doc_path)
    {
        this.name = name;
        this.url = url;
        this.description = description;
        this.pic = pic;
        this.id = id;
        this.sub_url = sub_url;
        this.fav = fav;
        this.doc_path = doc_path;


    }



}
