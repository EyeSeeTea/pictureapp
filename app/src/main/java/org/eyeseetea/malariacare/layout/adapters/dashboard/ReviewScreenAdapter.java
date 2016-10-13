package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.views.TextCard;

import java.util.List;

/**
 * Created by idelcano on 13/10/2016.
 */

public class ReviewScreenAdapter extends AReviewAdapter {

    public ReviewScreenAdapter(List<Value> items, LayoutInflater inflater, Context context) {
        this.items = items;
        this.context = context;
        this.lInflater = inflater;
        this.headerLayout = R.layout.review_header;
        this.subHeaderLayout = R.layout.review_sub_header;
        this.recordLayout = R.layout.review_item_row;
    }

    @Override
    public IDashboardAdapter newInstance(List items, Context context) {
        return new ReviewScreenAdapter((List<Value>) items, lInflater, context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Value value = (Value) getItem(position);

        // Get the row layout
        View rowView = this.lInflater.inflate(getRecordLayout(), parent, false);

        //Sets the value text in the row and add the question as tag.
        TextCard textCard = (TextCard) rowView.findViewById(R.id.review_content_text);
        textCard.setText((value.getOption() != null) ? value.getOption().getCode() : value.getValue());
        if ((value.getQuestion() != null)) {
            textCard.setTag(value.getQuestion());

            //Adds click listener to hide the fragment and go to the clicked question.
            textCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Question question = (Question) v.getTag();
                    DashboardActivity.dashboardActivity.hideReview(question);
                }
            });

            if (value.getOption() != null && value.getOption().getBackground_colour() != null) {
                textCard.setBackgroundColor(Color.parseColor("#" + value.getOption().getBackground_colour()));
            }

        }
        return rowView;
    }
}
