package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.views.TextCard;

/**
 * Created by idelcano on 05/12/2016.
 */

public class HeaderUseCase implements IHeaderCaseUse {
    /**
     * Singleton reference
     */
    private static HeaderUseCase instance;

    public static HeaderUseCase getInstance() {
        if (instance == null) {
            instance = new HeaderUseCase();
        }
        return instance;
    }

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return null;
    }

    @Override
    public void init(Activity activity, int tabTag) {
        if (activity == null) {
            return;
        }
        View view = activity.findViewById(R.id.common_header);
        if (view != null) {
            switch (tabTag) {
                case R.string.tab_tag_assess:
                    view.setVisibility(View.VISIBLE);
                    setTitle(view, R.id.header_title, R.string.header_title_unsent);
                    setTitle(view, R.id.header_subtitle, R.string.header_subtitle_unsent);
                    break;
                case R.string.tab_tag_improve:
                    view.setVisibility(View.VISIBLE);
                    setTitle(view, R.id.header_title, R.string.header_title_sent);
                    setTitle(view, R.id.header_subtitle, R.string.header_subtitle_sent);
                    break;
                case R.string.tab_tag_stock:
                    view.setVisibility(View.VISIBLE);
                    setTitle(view, R.id.header_title, R.string.header_title_stock);
                    setTitle(view, R.id.header_subtitle, R.string.header_subtitle_stock);
                    break;
                case R.string.tab_tag_monitor:
                    view.setVisibility(View.VISIBLE);
                    setTitle(view, R.id.header_title, R.string.header_title_monitoring);
                    setTitle(view, R.id.header_subtitle, R.string.header_subtitle_monitoring);
                    break;
            }
        }
    }

    private static void setTitle(View view, int keyId, int titleString) {
        TextCard title = (TextCard) view.findViewById(keyId);
        title.setText(view.getContext().getString(titleString));
    }

    @Override
    public void hideHeader(Activity activity) {
        if (activity != null) {
            View view = activity.findViewById(R.id.common_header);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }
}
