package org.eyeseetea.malariacare.layout.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {
    public static void setActionBar(android.support.v7.app.ActionBar actionBar) {
        setActionBarAppAndUser(actionBar);
    }

    public static void setTabHosts(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabHostsWithImages();
    }


    public static void setActionBarAppAndUser(ActionBar actionBar) {
        Context context = PreferencesState.getInstance().getContext();
        actionBar.setLogo(R.drawable.pictureapp_logo);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        int color = ContextCompat.getColor(context, R.color.text_first_color);
        String colorString = String.format("%X", color).substring(2);
        Spanned spannedTitle = Html.fromHtml(String.format("<font color=\"#%s\" size=\"10\"><b>%s</b></font>", colorString , context.getString(R.string.app_name)));
        color = ContextCompat.getColor(context, R.color.text_second_color);
        colorString = String.format("%X", color).substring(2);
        User user= User.getLoggedUser();
        String userName;
        if(user==null)
        {
            userName="";
        }else{
            userName=user.getName();
        }
        Spanned spannedSubTitle = Html.fromHtml(String.format("<font color=\"#%s\"><b>%s</b></font>", colorString , "Volunteer: "+ userName +""));
        actionBar.setCustomView(R.layout.custom_action_bar);
        TextView title =(TextView) actionBar.getCustomView().findViewById(R.id.action_bar_multititle_title);
        title.setText(spannedTitle);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" +  context.getString(R.string.light_font));
        title.setTypeface(tf);
        TextView subtitle =(TextView) actionBar.getCustomView().findViewById(R.id.action_bar_multititle_subtitle);
        subtitle.setText(spannedSubTitle);
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/" +  context.getString(R.string.light_font));
        subtitle.setTypeface(tf);

    }
}
