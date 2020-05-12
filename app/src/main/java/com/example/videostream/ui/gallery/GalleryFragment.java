package com.example.videostream.ui.gallery;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    // private SearchView searchView;
    List<Video_Model> list = new ArrayList<>();
    private RecyclerView recyclerView;
    ProgressDialog progressDialog;
    Map<String, Boolean> favMap = new HashMap<>();

    MaterialSearchBar searchView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        searchView = root.findViewById(R.id.searchView);
        searchView.setSpeechMode(false);
        searchView.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(final CharSequence text) {


                list.clear();

                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            favMap.put(documentSnapshot.getId(), documentSnapshot.getBoolean("favorite"));
                        }


                        FirebaseFirestore.getInstance().collection("Movie_meta_data").whereEqualTo("name", text.toString().toLowerCase()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                list.clear();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    String name = documentSnapshot.getString("name");
                                    String url = documentSnapshot.getString("url");
                                    String description = documentSnapshot.getString("description");
                                    String sub_url = documentSnapshot.getString("sub_url");
                                    String pic = documentSnapshot.getString("pic");
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();

                                    if (favMap.get(documentSnapshot.getId()) == true) {
                                        list.add(new Video_Model(name, url, description, pic, documentSnapshot.getId(), sub_url, true, documentSnapshot.getReference().getPath()));
                                    } else {
                                        list.add(new Video_Model(name, url, description, pic, documentSnapshot.getId(), sub_url, false, documentSnapshot.getReference().getPath()));
                                    }
                                }

                                recyclerView = root.findViewById(R.id.search_result);
                                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

                                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                                MyAdapter myAdapter = new MyAdapter(getContext(), list, false, true);
                                recyclerView.setAdapter(myAdapter);

                            }
                        });

                    }
                });


            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(final String query) {
//
//                progressDialog = new ProgressDialog(getActivity());
//                progressDialog.setTitle("");
//                progressDialog.setMessage("Loading Content, please wait!");
//                progressDialog.show();
//                list.clear();
//
//                FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//
//                        for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
//                        {
//                            favMap.put(documentSnapshot.getId(),documentSnapshot.getBoolean("favorite"));
//                        }
//
//
//                        FirebaseFirestore.getInstance().collection("Movie_meta_data").whereEqualTo("name",query.toLowerCase()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                                list.clear();
//                                for(DocumentSnapshot documentSnapshot:queryDocumentSnapshots)
//                                {
//                                    String name = documentSnapshot.getString("name");
//                                    String url = documentSnapshot.getString("url");
//                                    String description = documentSnapshot.getString("description");
//                                    String sub_url = documentSnapshot.getString("sub_url");
//                                    String pic = documentSnapshot.getString("pic");
//                                    name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
//
//                                    if(favMap.get(documentSnapshot.getId())==true)
//                                    {
//                                        list.add(new Video_Model(name, url, description,pic, documentSnapshot.getId(), sub_url,true, documentSnapshot.getReference().getPath()));
//                                    }
//                                    else
//                                    {
//                                        list.add(new Video_Model(name, url, description,pic, documentSnapshot.getId(), sub_url,false, documentSnapshot.getReference().getPath()));
//                                    }
//                                }
//
//                                recyclerView = root.findViewById(R.id.search_result);
//                                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
//
//                                recyclerView.setLayoutManager(staggeredGridLayoutManager);
//                                MyAdapter myAdapter = new MyAdapter(getContext(), list, false, true);
//                                recyclerView.setAdapter(myAdapter);
//                                progressDialog.dismiss();
//                            }
//                        });
//
//                    }
//                });
//
//
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        return root;
    }
}
