package com.example.videostream;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.BoringLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.example.videostream.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<Video_Model> video_modelList;
    Context context;
    Boolean info;
    Boolean show_fav_btn;

    public MyAdapter(Context context, List<Video_Model> list, Boolean info, Boolean show_fav_btn)
    {
        this.context = context;
        this.video_modelList = list;
        this.info = info;
        this.show_fav_btn = show_fav_btn;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        TextView description;
        CheckBox fav;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.video_image);
            this.name = itemView.findViewById(R.id.video_name);
            this.description = itemView.findViewById(R.id.video_description);
            this.fav = itemView.findViewById(R.id.favorite);
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_background);
        Glide.with(context).load(video_modelList.get(position).pic).into(holder.image);

        holder.name.setText(video_modelList.get(position).name);
        holder.description.setText(video_modelList.get(position).description);

        if(video_modelList.get(position).fav==true)
        {
            holder.fav.setChecked(true);
        }
        else {
            holder.fav.setChecked(false);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func(holder, position);
            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                func(holder,position);
            }
        });


        if(show_fav_btn==false)
        {
            holder.fav.setVisibility(View.GONE);
        }


        holder.fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.fav.isChecked()==true)
                {

                    FirebaseFirestore.getInstance().collection("Movie_meta_data").document(video_modelList.get(position).id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map favorite = new HashMap<>();
                            favorite.put("favorite",true);
                            favorite.put("video_meta_data", documentSnapshot.getReference());
                            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(video_modelList.get(position).id).set(favorite,SetOptions.merge());

                        }
                    });


                }
                else
                {

                    FirebaseFirestore.getInstance().collection("Movie_meta_data").document(video_modelList.get(position).id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map favorite = new HashMap<>();
                            favorite.put("favorite",false);
                            favorite.put("video_meta_data", documentSnapshot.getReference());
                            FirebaseFirestore.getInstance().collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favorites").document(video_modelList.get(position).id).set(favorite,SetOptions.merge());

                        }
                    });

                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return video_modelList.size();
    }

    void func(ViewHolder holder, int position)
    {

        if(this.info == false)
        {
            Intent intent = new Intent(context, InfoPage.class);
            intent.putExtra("info_object", (Serializable) video_modelList.get(position));
            context.startActivity(intent);

        }
        else
        {

            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("video_url",video_modelList.get(position).url);
            intent.putExtra("sub_url", video_modelList.get(position).sub_url);
            context.startActivity(intent);

        }



    }
}
