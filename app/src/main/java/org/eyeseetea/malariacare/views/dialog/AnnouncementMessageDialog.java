package org.eyeseetea.malariacare.views.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.Date;

public class AnnouncementMessageDialog {
    public static void showAnnouncement(int titleId, String message, final Context context) {
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(message));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

        final User loggedUser = User.getLoggedUser();
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleId))
                .setMessage(linkedMessage)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesState.getInstance().setUserAccept(true);
                        checkUserClosed(loggedUser, context);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkUserClosed(loggedUser, context);
                    }
                }).create();
        dialog.show();
        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(
                LinkMovementMethod.getInstance());
    }

    public static void checkUserClosed(User user, Context context) {
        if (user.getCloseDate() != null && user.getCloseDate().before(new Date())) {
            closeUser(R.string.admin_announcement,
                    PreferencesState.getInstance().getContext().getString(R.string.user_close),
                    context);
        }
    }

    /**
     * Shows an alert dialog asking for acceptance of the announcement. If ok calls the accept the
     * annoucement, do nothing otherwise
     */
    public static void closeUser(int titleId, String message, final Context context) {
        SpannableString linkedMessage = new SpannableString(Html.fromHtml(message));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleId))
                .setMessage(linkedMessage)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesState.getInstance().setUserAccept(false);
                        DashboardActivity.dashboardActivity.executeLogout();
                    }
                }).show();
        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(
                LinkMovementMethod.getInstance());
    }

}
