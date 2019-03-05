package org.eyeseetea.malariacare.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.layout.adapters.survey.SurveysAdapter;
import org.eyeseetea.malariacare.presentation.models.SurveyViewModel;

public class SwipeRecyclerViewSurveysCallback extends ItemTouchHelper.SimpleCallback {

    private SurveysAdapter surveysAdapter;

    private Drawable icon;
    private final ColorDrawable background;

    public interface OnSurveySwipeListener {
        void onSurveySwipe(SurveyViewModel surveyViewModel);
    }

    private OnSurveySwipeListener onSurveySwipeListener;


    public SwipeRecyclerViewSurveysCallback(Context context,
            SurveysAdapter surveysAdapter) {
        super(0, ItemTouchHelper.LEFT);
        this.surveysAdapter = surveysAdapter;
        icon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        background = new ColorDrawable(Color.RED);
    }

    public void setOnSurveySwipeListener(OnSurveySwipeListener listener) {
        this.onSurveySwipeListener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target) {
        // used for up and down movements
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        SurveyViewModel surveyViewModel = surveysAdapter.getSurvey(viewHolder.getAdapterPosition());

        if (onSurveySwipeListener != null) {
            onSurveySwipeListener.onSurveySwipe(surveyViewModel);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20; //so background is behind the rounded corners of itemView

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        icon.draw(c);
    }
}
