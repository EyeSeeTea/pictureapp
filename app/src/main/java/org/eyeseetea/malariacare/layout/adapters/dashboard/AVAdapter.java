package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.net.Uri;
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
import org.eyeseetea.sdk.common.VideoUtils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.io.File;
import java.util.List;

public class AVAdapter extends RecyclerView.Adapter {

    public enum ViewType {GRID, LIST}

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
        if(medias==null) {
            return 0;
        }
        return medias.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if(typeOfView.equals(ViewType.GRID)) {
            View rowView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.av_grid_item, viewGroup, false);
            return new GridMediaViewHolder(rowView);
        }else if(typeOfView.equals(ViewType.LIST)){
            View rowView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.av_list_item, viewGroup, false);
            return new ListItemMediaViewHolder(rowView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(viewHolder instanceof GridMediaViewHolder){
            GridMediaViewHolder mediaViewHolder = (GridMediaViewHolder) viewHolder;
            Media media = medias.get(position);
            mediaViewHolder.name.setText(media.getName());
            if(media.getType().equals(Media.MediaType.PICTURE)){
                File file = new File(media.getResourcePath());
                Uri uri = Uri.fromFile(file);
                mediaViewHolder.filename.setImageURI(uri);
            }else {

                mediaViewHolder.filename.setImageBitmap(VideoUtils.getVideoPreview(media.getResourcePath(), context));
            }
        }
        else if (viewHolder instanceof ListItemMediaViewHolder){
            ListItemMediaViewHolder mediaViewHolder = (ListItemMediaViewHolder) viewHolder;
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



    public static class GridMediaViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView filename;
        TextView name;

        GridMediaViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.av_card_view);
            filename = (ImageView)itemView.findViewById(R.id.image_content);
            name = (TextView)itemView.findViewById(R.id.resource_name);
        }
    }


    public class ListItemMediaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final CustomTextView fileName;
        private final CustomTextView size;

        public ListItemMediaViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            fileName = (CustomTextView) itemView.findViewById(R.id.filename);
            size = (CustomTextView) itemView.findViewById(R.id.size);
        }
    }
}