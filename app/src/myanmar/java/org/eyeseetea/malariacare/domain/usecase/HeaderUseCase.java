package org.eyeseetea.malariacare.domain.usecase;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.layout.adapters.dashboard.strategies.IHeaderCaseUse;
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

    private static void setTitle(View view, int keyId, int titleString) {
        TextCard title = (TextCard) view.findViewById(keyId);
        title.setText(view.getContext().getString(titleString));
    }

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return null;
    }

    @Override
    public void init(Activity activity, int tabTag) {
        View headerView = getHeaderView(activity);
        int titleResource = 0, subtitleResource = 0;
        switch (tabTag) {
            case R.string.tab_tag_assess:
                titleResource = R.string.header_title_unsent;
                subtitleResource = R.string.header_subtitle_unsent;
                break;
            case R.string.tab_tag_improve:
                titleResource = R.string.header_title_sent;
                subtitleResource = R.string.header_subtitle_sent;
                break;
            case R.string.tab_tag_stock:
                titleResource = R.string.header_title_stock;
                subtitleResource = R.string.header_subtitle_stock;
                break;
            case R.string.tab_tag_monitor:
                titleResource = R.string.header_title_monitoring;
                subtitleResource = R.string.header_subtitle_monitoring;
                break;
        }
        setTitle(headerView, R.id.header_title, titleResource);
        setTitle(headerView, R.id.header_subtitle, subtitleResource);
        headerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideHeader(Activity activity) {
        View headerView = getHeaderView(activity);
        if (headerView == null) return;
        headerView.setVisibility(View.GONE);
    }

    @Nullable
    private View getHeaderView(Activity activity) {
        if (activity == null) {
            return null;
        }
        View headerView = activity.findViewById(R.id.common_header);
        if (headerView == null) {
            return null;
        }
        return headerView;
    }
}
