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

public class FoundAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private ArrayList<FoundItem> items;

    public FoundAdapter (Context context, ArrayList<FoundItem> founds) {
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = founds;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public FoundItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final FoundViewHolder foundViewHolder;
        FoundItem item = items.get(i);

        if ( view == null ) {

            view = inflater.inflate(R.layout.list_item, viewGroup, false);

            foundViewHolder = new FoundViewHolder();

            foundViewHolder.iv1 = (ImageView) view.findViewById(R.id.image_tv);
            foundViewHolder.tv1 = (TextView)view.findViewById(R.id.keyword_tv);
            foundViewHolder.tv2 = (TextView)view.findViewById(R.id.title_tv);
            foundViewHolder.tv3 = (TextView)view.findViewById(R.id.contents_tv);
            foundViewHolder.tv4 = (TextView)view.findViewById(R.id.location_tv);
            foundViewHolder.tv5 = (TextView)view.findViewById(R.id.date_tv);
            foundViewHolder.tv6 = (TextView)view.findViewById(R.id.owner_tv);

            view.setTag(foundViewHolder);
        }
        else{
            foundViewHolder = (FoundViewHolder) view.getTag();
        }

        foundViewHolder.tv1.setText("["+item.getKeyword()+"]");
        foundViewHolder.tv2.setText(item.getFound_titleET());
        foundViewHolder.tv3.setText(item.getFound_contentET());
        foundViewHolder.tv4.setText(item.getLocation());
        foundViewHolder.tv5.setText(item.getDate());
        foundViewHolder.tv6.setText(item.getName());
        foundViewHolder.iv1.setImageResource(R.drawable.picture);

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://skkufindlost.appspot.com").child("found_image");
        final String fileName = item.getFound_image();
        storageRef = storageRef.child(fileName);

        StorageReference pathReference = storageRef;

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri u) {
                Glide.with(foundViewHolder.iv1.getContext()).load(u.toString()).into(foundViewHolder.iv1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        return view;
    }
}