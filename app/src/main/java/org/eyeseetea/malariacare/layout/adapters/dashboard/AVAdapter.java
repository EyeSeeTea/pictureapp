package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Media;

import java.util.List;

/**
 * Created by ignac on 04/08/2017.
 */

public class AVAdapter extends RecyclerView.Adapter<AVAdapter.MediaViewHolder>{

    MediaViewHolder mediaViewHolder;

    List<Media> medias;

    public AVAdapter(List<Media> medias){
        this.medias = medias;
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView filename;
        TextView name;

        MediaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.av_card_view);
            filename = (ImageView)itemView.findViewById(R.id.image_content);
            name = (TextView)itemView.findViewById(R.id.resource_name);
        }
    }

    @Override
    public int getItemCount() {
        return medias.size();
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View cardview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.av_cardview, viewGroup, false);
        mediaViewHolder = new MediaViewHolder(cardview);
        return mediaViewHolder;
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        mediaViewHolder.name.setText(medias.get(position).getName());
        Drawable drawable = medias.get(position).getFileFromPath(holder.itemView.getContext());
        mediaViewHolder.filename.setImageDrawable(drawable);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}