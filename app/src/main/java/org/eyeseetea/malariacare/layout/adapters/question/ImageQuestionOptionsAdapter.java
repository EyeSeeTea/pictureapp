package org.eyeseetea.malariacare.layout.adapters.question;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.TextCard;

import java.util.ArrayList;
import java.util.List;

public class ImageQuestionOptionsAdapter extends
        RecyclerView.Adapter<ImageQuestionOptionsAdapter.ViewHolder> {

    public interface OnOptionClickListener {
        void onItemClick(View view, Option option);
    }

    private static OnOptionClickListener sOnOptionClickListener;

    public List<Option> mOptions = new ArrayList<>();

    public ImageQuestionOptionsAdapter(List<Option> options) {
        this.mOptions = options;
    }

    public void setOnOptionClickListener(OnOptionClickListener listener) {
        sOnOptionClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dynamic_image_question_option, parent, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Option option = mOptions.get(position);

        holder.optionCotainerView.setBackgroundColor(
                Color.parseColor("#" + option.getBackground_colour()));

        BaseLayoutUtils.putImageInImageView(option.getInternationalizedPath(),
                holder.optionImageView);

        if (option.getOptionAttribute().hasHorizontalAlignment()
                && option.getOptionAttribute().hasVerticalAlignment()) {
            holder.optionTextCard.setText(Utils.getInternationalizedString(option.getCode()));
            holder.optionTextCard.setGravity(option.getOptionAttribute().getGravity());
        } else {
            holder.optionTextCard.setVisibility(View.GONE);
        }

        //TODO: holder.optionCounterTextCard.setText("");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sOnOptionClickListener != null) {
                    Option clickedOption = mOptions.get(position);

                    sOnOptionClickListener.onItemClick(view, clickedOption);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View optionCotainerView;
        public final TextCard optionTextCard;
        public final ImageView optionImageView;
        public final TextCard optionCounterTextCard;

        public ViewHolder(View view) {
            super(view);

            optionCotainerView = (View) view.findViewById(R.id.imageOptionContainer);
            optionImageView = (ImageView) view.findViewById(R.id.optionImage);
            optionTextCard = (TextCard) view.findViewById(R.id.optionText);
            optionCounterTextCard = (TextCard) view.findViewById(R.id.optionCounterText);
        }
    }
}
