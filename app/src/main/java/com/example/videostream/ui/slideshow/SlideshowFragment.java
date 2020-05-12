package com.example.videostream.ui.slideshow;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.videostream.MyAdapter;
import com.example.videostream.R;
import com.example.videostream.Video_Model;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    RecyclerView recyclerView;
    ProgressDialog progressDialog ;
    Map<String,Boolean> favMap = new HashMap<>();
    List<Video_Model> list = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        recyclerView = root.findViewById(R.id.fav_list);

        FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").whereEqualTo("favorite",true).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentReference documentReference;
                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {

                        if(documentSnapshot.getDocumentReference("video_meta_data")!=null)
                        {
                            documentReference = documentSnapshot.getDocumentReference("video_meta_data");
                            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String name = documentSnapshot.getString("name");
                                    String url = documentSnapshot.getString("url");
                                    String description = documentSnapshot.getString("description");
                                    String sub_url = documentSnapshot.getString("sub_url");
                                    String pic = documentSnapshot.getString("pic");
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                                    list.add(new Video_Model(name, url, description,pic, documentSnapshot.getId(), sub_url,true, documentSnapshot.getReference().getPath()));

                                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL);

                                    recyclerView.setLayoutManager(staggeredGridLayoutManager);
                                    MyAdapter myAdapter = new MyAdapter(getContext(), list, false, documentSnapshot.getBoolean("fav_button_show"));
                                    recyclerView.setAdapter(myAdapter);

                                }
                            });
                        }




                }

            }
        });






        return root;
    }
}
