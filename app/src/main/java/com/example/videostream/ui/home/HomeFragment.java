package com.example.videostream.ui.home;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.videostream.MainActivity;
import com.example.videostream.MyAdapter;
import com.example.videostream.R;

import com.example.videostream.Video_Model;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private static final int NUM_COLS = 2;
    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    List<Video_Model> list = new ArrayList<>();
    Map<String,Boolean> favMap = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog progressDialog ;


    private AdView mAdView, mAdView1;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
//        Toast.makeText(getActivity(), FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("");
        progressDialog.setMessage("Loading Content, please wait!");
        progressDialog.show();


        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView1 = root.findViewById(R.id.adView1);
        AdRequest adRequest1 = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest1);





        db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                {
                    favMap.put(documentSnapshot.getId(),documentSnapshot.getBoolean("favorite"));
                }


                db.collection("Movie_meta_data").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                        {
                            String name = documentSnapshot.getString("name");
                            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                            String description = documentSnapshot.getString("description");
                            String url = documentSnapshot.getString("url");
                            String pic = documentSnapshot.getString("pic");
                            if(favMap.get(documentSnapshot.getId())!=null)
                            {
                                if(favMap.get(documentSnapshot.getId())==true)
                                {
                                    list.add(new Video_Model(name, url, description,pic, documentSnapshot.getId(), documentSnapshot.getString("sub_url"),true, documentSnapshot.getReference().getPath()));
                                }
                                else
                                {
                                    list.add(new Video_Model(name, url, description,pic, documentSnapshot.getId(), documentSnapshot.getString("sub_url"),false, documentSnapshot.getReference().getPath()));
                                }
                            }
                            else
                            {
                                list.add(new Video_Model(name, url, description,pic, documentSnapshot.getId(), documentSnapshot.getString("sub_url"),false, documentSnapshot.getReference().getPath()));

                            }

                        }
                        recyclerView = root.findViewById(R.id.recyclerView);
                        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLS, LinearLayoutManager.VERTICAL);

                        recyclerView.setLayoutManager(staggeredGridLayoutManager);
                        MyAdapter myAdapter = new MyAdapter(getContext(), list, false, true);
                        recyclerView.setAdapter(myAdapter);
                        progressDialog.dismiss();

                    }
                });
            }
        });







        return root;
    }

}
