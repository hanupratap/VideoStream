package com.example.videostream;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleService;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class InfoPage extends AppCompatActivity {

    Video_Model object;

    FloatingActionButton fab;
    String url, sub_url, id;
    ImageView pic;
    List<Video_Model> list = new ArrayList<>();
    Boolean show_episodes;
    String dr;
    DocumentReference documentReference;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_page);

        getSupportActionBar().hide();

        collapsingToolbarLayout = findViewById(R.id.collapsableToolbar);
        pic = findViewById(R.id.info_image);
        object = (Video_Model) getIntent().getSerializableExtra("info_object");

        dr = object.doc_path;
         recyclerView = findViewById(R.id.infoRecycler);

        collapsingToolbarLayout.setTitle(object.name);

        Glide.with(InfoPage.this)
                .load(object.pic)
                .into(pic);

        documentReference = db.document(dr);






        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {




                if(documentSnapshot.getBoolean("show_episodes") == false)
                {
                    if(documentSnapshot.getBoolean("tv_show")==false)
                    {
                        list.add(object);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

                        recyclerView.setLayoutManager(staggeredGridLayoutManager);
                        MyAdapter myAdapter = new MyAdapter(InfoPage.this, list, true, false);
                        recyclerView.setAdapter(myAdapter);

                    }
                    else
                    {
                        FirebaseFirestore.getInstance().collection("Movie_meta_data").document(object.id).collection("Seasons").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for(DocumentSnapshot documentSnapshot1: queryDocumentSnapshots)
                                {
                                    String name = documentSnapshot1.getString("name");
                                    String url = documentSnapshot1.getString("url");
                                    String description = documentSnapshot1.getString("description");
                                    String sub_url = documentSnapshot1.getString("sub_url");
                                    String pic = documentSnapshot1.getString("pic");
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();


                                    list.add(new Video_Model(name, url, description,pic, documentSnapshot1.getId(), sub_url,false, documentSnapshot1.getReference().getPath()));
                                }
                                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

                                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                                MyAdapter myAdapter = new MyAdapter(InfoPage.this, list, false, false);
                                recyclerView.setAdapter(myAdapter);
                            }
                        });
                    }
                }
                else {
                    documentReference.collection("Episodes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(DocumentSnapshot documentSnapshot1: queryDocumentSnapshots)
                            {
                                String name = documentSnapshot1.getString("name");
                                String url = documentSnapshot1.getString("url");
                                String description = documentSnapshot1.getString("description");
                                String sub_url = documentSnapshot1.getString("sub_url");
                                String pic = documentSnapshot1.getString("pic");
                                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                                list.add(new Video_Model(name, url, description,pic, documentSnapshot1.getId(), sub_url,false, documentSnapshot1.getReference().getPath()));
                            }
                            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

                            recyclerView.setLayoutManager(staggeredGridLayoutManager);
                            MyAdapter myAdapter = new MyAdapter(InfoPage.this, list, true, false);
                            recyclerView.setAdapter(myAdapter);
                        }
                    });
                }
            }
        });




    }
}
