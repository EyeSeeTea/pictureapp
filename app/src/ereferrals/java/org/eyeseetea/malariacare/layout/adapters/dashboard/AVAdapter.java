package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
    public interface OnClickMediaListener {
        void onClick(Media media);
    }

    public enum ViewType {GRID, LIST}

    ViewType typeOfView;

    List<Media> medias;

    Context context;

    private OnClickMediaListener mOnClickMediaListener;

    public AVAdapter(List<Media> medias, ViewType typeOfView, Context context) {
        this.medias = medias;
        this.typeOfView = typeOfView;
        this.context = context;
    }

    public void setOnClickMediaListener(OnClickMediaListener onClickMediaListener) {
        mOnClickMediaListener = onClickMediaListener;
    }

    @Override
    public int getItemCount() {
        if (medias == null) {
            return 0;
        }
        return medias.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (typeOfView.equals(ViewType.GRID)) {
            View rowView = LayoutInflater.from(viewGroup.getContext()).inflate(
                    R.layout.av_grid_item, viewGroup, false);
            return new GridMediaViewHolder(rowView);
        } else if (typeOfView.equals(ViewType.LIST)) {
            View rowView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.av_list_item, viewGroup, false);
            return new ListItemMediaViewHolder(rowView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof GridMediaViewHolder) {
            GridMediaViewHolder mediaViewHolder = (GridMediaViewHolder) viewHolder;
            final Media media = medias.get(position);
            mediaViewHolder.name.setText(media.getName());

            handlePreviewImage(mediaViewHolder, media);

            mediaViewHolder.itemView.setOnClickListener(new ImageView.OnClickListener() {
                public void onClick(View v)
                {
                    if (mOnClickMediaListener != null) {
                        mOnClickMediaListener.onClick(media);
                    }
                }
            });
        } else if (viewHolder instanceof ListItemMediaViewHolder) {
            ListItemMediaViewHolder mediaViewHolder = (ListItemMediaViewHolder) viewHolder;
            final Media media = medias.get(position);
            if (media.getName() != null) {
                mediaViewHolder.fileName.setText(media.getName());
            }
            ((View) mediaViewHolder.itemView).setOnClickListener(
                    new ImageView.OnClickListener() {
                public void onClick(View v)
                {
                    if (mOnClickMediaListener != null) {
                        mOnClickMediaListener.onClick(media);
                    }
                }
            });
        }
    }

    private void handlePreviewImage(GridMediaViewHolder mediaViewHolder, Media media) {
        switch (media.getType()) {
            case PICTURE:
                handlePictureMediaTypePreview(mediaViewHolder, media);
                break;
            case VIDEO:
                handleVideoMediaTypePreview(mediaViewHolder, media);
                break;
            case UNKNOWN:
                handleUnknownMediaTypePreview(mediaViewHolder);
                break;
        }
    }

    private void handleUnknownMediaTypePreview(GridMediaViewHolder mediaViewHolder) {
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.no_image_available);
        mediaViewHolder.filename.setImageDrawable(drawable);
        mediaViewHolder.filename.setAdjustViewBounds(true);
        mediaViewHolder.filename.setScaleType(ImageView.ScaleType.CENTER);
    }

    private void handleVideoMediaTypePreview(GridMediaViewHolder mediaViewHolder,
            Media media) {
        if(media.getResourcePath()!=null) {
            mediaViewHolder.filename.setImageBitmap(
                    VideoUtils.getVideoPreview(media.getResourcePath(), context));
            mediaViewHolder.filename.setAdjustViewBounds(true);
            mediaViewHolder.filename.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
    }

    private void handlePictureMediaTypePreview(GridMediaViewHolder mediaViewHolder,
            Media media) {
        if(media.getResourcePath()!=null) {
            File file = new File(media.getResourcePath());
            Uri uri = Uri.fromFile(file);
            mediaViewHolder.filename.setImageURI(uri);
            mediaViewHolder.filename.setAdjustViewBounds(true);
            mediaViewHolder.filename.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
            cv = (CardView) itemView.findViewById(R.id.av_card_view);
            filename = (ImageView) itemView.findViewById(R.id.image_content);
            name = (TextView) itemView.findViewById(R.id.resource_name);
        }
    }


    public class ListItemMediaViewHolder extends RecyclerView.ViewHolder {
        private final CustomTextView fileName;

        public ListItemMediaViewHolder(View itemView) {
            super(itemView);
            fileName = (CustomTextView) itemView.findViewById(R.id.filename);
        }
    }
}