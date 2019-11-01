package edu.skku.map.skku_findlost;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class LostAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private ArrayList<LostItem> items;

    public LostAdapter (Context context, ArrayList<LostItem> losts) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = losts;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public LostItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final LostViewHolder lostViewHolder;
        LostItem item = items.get(i);

        if ( view == null ) {

            view = inflater.inflate(R.layout.list_item, viewGroup, false);

            lostViewHolder = new LostViewHolder();

            lostViewHolder.iv1 = (ImageView)view.findViewById(R.id.image_tv);
            lostViewHolder.tv1 = (TextView)view.findViewById(R.id.keyword_tv);
            lostViewHolder.tv2 = (TextView)view.findViewById(R.id.title_tv);
            lostViewHolder.tv3 = (TextView)view.findViewById(R.id.contents_tv);
            lostViewHolder.tv4 = (TextView)view.findViewById(R.id.location_tv);
            lostViewHolder.tv5 = (TextView)view.findViewById(R.id.date_tv);
            lostViewHolder.tv6 = (TextView)view.findViewById(R.id.owner_tv);

            view.setTag(lostViewHolder);
        }
        else{
            lostViewHolder = (LostViewHolder) view.getTag();
        }

        lostViewHolder.tv1.setText("["+item.getKeyword()+"]");
        lostViewHolder.tv2.setText(item.getLost_titleET());
        lostViewHolder.tv3.setText(item.getLost_contentET());
        lostViewHolder.tv4.setText(item.getLocation());
        lostViewHolder.tv5.setText(item.getDate());
        lostViewHolder.tv6.setText(item.getName());
        lostViewHolder.iv1.setImageResource(R.drawable.picture);

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("lost_image");

        final String fileName = item.getLost_image();
        storageRef = storageRef.child(fileName);
        StorageReference pathReference = storageRef;

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(lostViewHolder.iv1.getContext()).load(u.toString()).into(lostViewHolder.iv1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        return view;
    }

}