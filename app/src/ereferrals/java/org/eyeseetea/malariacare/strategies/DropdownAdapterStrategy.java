package org.eyeseetea.malariacare.strategies;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public class DropdownAdapterStrategy {

    public static void drawDropDown(int position, View convertView, View parent, Context context) {
        int transparent = ContextCompat.getColor(context, R.color.transparent);
        int selected = ContextCompat.getColor(context, android.R.color.secondary_text_dark);

        if((parent!=null && parent instanceof ListView && ((ListView)parent).getAdapter()!=null)) {
            if(((ListView) parent) instanceof  ListView){
                ListView listView = ((ListView)parent);
                if(listView.getCheckedItemPosition()==position){
                    convertView.setBackgroundColor(selected);
                }
                ColorDrawable myColor = new ColorDrawable(
                        PreferencesState.getInstance().getContext().getResources().getColor(
                                R.color.black_grey_purple)
                );
                listView.setDivider(myColor);
                listView.setDividerHeight(1);
            }
            }else{
                convertView.setBackgroundColor(transparent);
        }
    }
}
