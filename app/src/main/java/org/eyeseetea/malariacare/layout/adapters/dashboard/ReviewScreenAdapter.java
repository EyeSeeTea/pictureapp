package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.strategies.ReviewScreenAdapterStrategy;

import java.util.List;

public class ReviewScreenAdapter extends BaseAdapter implements IDashboardAdapter {

    public interface onClickListener {
        void onClickOnValue(String UId);
    }
    ReviewScreenAdapter.onClickListener onClickListener;
    List<Value> items;
    private LayoutInflater lInflater;
    private Context context;
    private Integer headerLayout;
    private Integer footerLayout;
    private Integer recordLayout;
    private String title;
    private Integer subHeaderLayout;

    public ReviewScreenAdapter(List<Value> items, LayoutInflater inflater, Context context,ReviewScreenAdapter.onClickListener onClickListener) {
        this.items = items;
        this.context = context;
        this.lInflater = inflater;
        this.headerLayout = R.layout.review_header;
        this.subHeaderLayout = R.layout.review_sub_header;
        this.recordLayout = R.layout.review_item_row;
        this.onClickListener = onClickListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setItems(List items) {
        this.items = (List<Value>) items;
    }

    public Integer getSubHeaderLayout() {
        return this.subHeaderLayout;
    }

    public void setSubHeaderLayout(Integer subHeaderLayout) {
        this.subHeaderLayout = subHeaderLayout;
    }

    @Override
    public Integer getHeaderLayout() {
        return this.headerLayout;
    }

    @Override
    public Integer getFooterLayout() {
        return footerLayout;
    }

    @Override
    public Integer getRecordLayout() {
        return this.recordLayout;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void remove(Object item) {
        this.items.remove(item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Value value = (Value) getItem(position); // Get the row layout

        TableRow rowView = (TableRow) this.lInflater.inflate(getRecordLayout(), parent, false);

        ReviewScreenAdapterStrategy reviewFragmentStrategy =
                new ReviewScreenAdapterStrategy(new ReviewScreenAdapter.onClickListener() {
                    @Override
                    public void onClickOnValue(String UId) {
                        onClickListener.onClickOnValue(UId);
                    }
                });

        return reviewFragmentStrategy.createViewRow(rowView, value, position);
    }
}
