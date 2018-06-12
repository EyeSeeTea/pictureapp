package org.eyeseetea.malariacare.strategies;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;

public class DropdownAdapterStrategy {

    public static void drawDropDown(int position, View convertView, View parent, Context context) {
        int transparent = ContextCompat.getColor(context, R.color.transparent);
        int selected = ContextCompat.getColor(context, android.R.color.secondary_text_dark);

        if((parent!=null && parent instanceof ListView && ((ListView)parent).getAdapter()!=null)) {
            if(((ListView) parent) instanceof  ListView){
                ListView listView = ((ListView)parent);
                if(listView.getCheckedItemPosition()==position){
                    convertView.setBackgroundColor(selected);
                }else{
                    convertView.setBackgroundColor(transparent);
                }
            }
            }else{
                convertView.setBackgroundColor(transparent);
        }
    }
}
