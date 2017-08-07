package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

/**
 * Created by ignac on 04/08/2017.
 */

public class AVAdapter extends RecyclerView.Adapter {

    public enum ViewType {cardview, detailed}

    ViewType typeOfView;

    List<Media> medias;

    Context context;

    public AVAdapter(List<Media> medias, ViewType typeOfView, Context context){
        this.medias = medias;
        this.typeOfView = typeOfView;
        this.context = context;
    }
    @Override
    public int getItemCount() {
        return medias.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(typeOfView.equals(ViewType.cardview)) {
            View rowView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.av_cardview, viewGroup, false);
            return new CardviewMediaViewHolder(rowView);
        }else if(typeOfView.equals(ViewType.detailed)){
            View rowView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.av_detailed_row, viewGroup, false);
            return new DetailedMediaViewHolder(rowView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(viewHolder instanceof CardviewMediaViewHolder){
            CardviewMediaViewHolder mediaViewHolder = (CardviewMediaViewHolder) viewHolder;
            mediaViewHolder.name.setText(medias.get(position).getName());
            Drawable drawable = medias.get(position).getFileFromPath(viewHolder.itemView.getContext());
            mediaViewHolder.filename.setImageDrawable(drawable);
        }
        else if (viewHolder instanceof DetailedMediaViewHolder){
            DetailedMediaViewHolder mediaViewHolder = (DetailedMediaViewHolder) viewHolder;
            Media media = medias.get(position);
            if (media.getName() != null) {
                mediaViewHolder.fileName.setText(media.getName());
            }
            if (media.getSize() != null) {
                mediaViewHolder.size.setText(media.getSize());
            }
            if (media.isPicture()) {
                mediaViewHolder.icon.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_image_black_18dp));
            }
            if (media.isVideo()) {
                mediaViewHolder.icon.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_movie_black_18dp));
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



    public static class CardviewMediaViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView filename;
        TextView name;

        CardviewMediaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.av_card_view);
            filename = (ImageView)itemView.findViewById(R.id.image_content);
            name = (TextView)itemView.findViewById(R.id.resource_name);
        }
    }


    public class DetailedMediaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final CustomTextView fileName;
        private final CustomTextView size;

        public DetailedMediaViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            fileName = (CustomTextView) itemView.findViewById(R.id.filename);
            size = (CustomTextView) itemView.findViewById(R.id.size);
        }
    }
}