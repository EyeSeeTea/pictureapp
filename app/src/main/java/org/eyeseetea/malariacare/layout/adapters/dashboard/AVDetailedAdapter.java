package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by ignac on 04/08/2017.
 */

public class AVDetailedAdapter extends
        RecyclerView.Adapter<AVDetailedAdapter.MediaViewHolder> {
    List<Media> items;
    Context context;
    LayoutInflater lInflater;
    protected Integer rowLayout;

    public AVDetailedAdapter(List<Media> items, Context context) {
        this.items = items;
        this.context = context;
        this.lInflater = LayoutInflater.from(context);
        this.rowLayout = R.layout.av_detailed_row;
    }

    @Override
    public MediaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.av_detailed_row, parent, false);

        return new MediaViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(MediaViewHolder holder, int position) {
        Media media = items.get(position);
        if (media.getName() != null) {
            holder.fileName.setText(media.getName());
        }
        if (media.getSize() != null) {
            holder.size.setText(media.getSize());
        }
        if (media.isPicture()) {
            holder.icon.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_image_black_18dp));
        }
        if (media.isVideo()) {
            holder.icon.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_movie_black_18dp));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getItemCount() {
        return items.size();
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final CustomTextView fileName;
        private final CustomTextView size;

        public MediaViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            fileName = (CustomTextView) itemView.findViewById(R.id.filename);
            size = (CustomTextView) itemView.findViewById(R.id.size);
        }
    }
}

