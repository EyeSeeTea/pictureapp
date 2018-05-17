package org.eyeseetea.malariacare.strategies;

import android.content.Context;
import android.graphics.Color;
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
            if(((ListView) parent).getCheckedItemPosition()==position){
                convertView.setBackgroundColor(selected);
            }
            }else{
                convertView.setBackgroundColor(transparent);
        }
    }
}
