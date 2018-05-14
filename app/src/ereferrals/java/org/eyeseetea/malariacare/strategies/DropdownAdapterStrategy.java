package org.eyeseetea.malariacare.strategies;

import android.view.View;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;

public class DropdownAdapterStrategy {

    public static void drawDropDown(int position, View convertView, View parent) {
        convertView.setBackgroundResource(R.drawable.dropdown_line);
        if((parent!=null && parent instanceof ListView && ((ListView)parent).getAdapter()!=null)) {
            if(((ListView) parent).getAdapter().getCount()-1==position){
                if(((ListView) parent).getCheckedItemPosition()==position){
                    convertView.setBackgroundResource(R.drawable.dropdown_line_selected_final);
                }else {
                    convertView.setBackgroundResource(R.drawable.dropdown_line_final);
                }
            } else if(((ListView) parent).getCheckedItemPosition()==position){
                convertView.setBackgroundResource(R.drawable.dropdown_line_selected);
            }
        }
    }
}
