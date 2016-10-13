package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;

import java.util.List;

/**
 * Created by idelcano on 13/10/2016.
 */

public abstract class AReviewAdapter  extends BaseAdapter implements IDashboardAdapter {

    List<Value> items;
    protected LayoutInflater lInflater;
    protected Context context;
    protected Integer headerLayout;
    protected Integer subHeaderLayout;
    protected Integer footerLayout;
    protected Integer recordLayout;
    protected String title;

    public AReviewAdapter() {

    }

    public AReviewAdapter(List<Value> items, Context context, Integer headerLayout, Integer subHeaderLayout, Integer footerLayout, Integer recordLayout, String title) {
        this.items = items;
        this.context = context;
        this.lInflater = LayoutInflater.from(context);
        this.headerLayout = headerLayout;
        this.subHeaderLayout = subHeaderLayout;
        this.footerLayout = footerLayout;
        this.recordLayout = recordLayout;
        this.title = title;
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

    public void setSubHeaderLayout(Integer subHeaderLayout) {
        this.subHeaderLayout = subHeaderLayout;
    }

    public Integer getSubHeaderLayout() {
        return this.subHeaderLayout;
    }
    @Override
    public void setHeaderLayout(Integer headerLayout) {
        this.headerLayout = headerLayout;
    }

    @Override
    public Integer getHeaderLayout() {
        return this.headerLayout;
    }

    @Override
    public void setFooterLayout(Integer footerLayout) {
        this.footerLayout = footerLayout;
    }

    @Override
    public Integer getFooterLayout() {
        return footerLayout;
    }

    @Override
    public void setRecordLayout(Integer recordLayout) {
        this.recordLayout = recordLayout;
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
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void remove(Object item) {
        this.items.remove(item);
    }

}