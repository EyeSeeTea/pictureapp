package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.exception.EmptyLocationException;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.MonitorFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.layout.listeners.SurveyLocationListener;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.GradleVariantConfig;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.dialog.AnnouncementMessageDialog;

public abstract class ADashboardActivityStrategy {
    private final static String TAG = ".DashActivityStrategy";
    protected DashboardActivity mDashboardActivity;
    protected DashboardUnsentFragment unsentFragment;
    protected DashboardSentFragment sentFragment;
    protected MonitorFragment monitorFragment;

    public void onCreate() {
        if (mDashboardActivity.getIntent().getBooleanExtra(
                mDashboardActivity.getString(R.string.show_announcement_key), true)
                && Session.getCredentials() != null
                && !Session.getCredentials().isDemoCredentials()) {
            new AsyncAnnouncement().execute();
        }
    }

    public abstract void reloadStockFragment(Activity activity);

    public void reloadMonitorFragment(Activity activity, MonitorFragment monitorFragment) {
        monitorFragment.reloadData();
        monitorFragment.reloadHeader(activity);
    }

    public abstract boolean showStockFragment(Activity activity, boolean isMoveToLeft);

    public abstract void newSurvey(Activity activity);

    public abstract void sendSurvey();

    public abstract boolean beforeExit(boolean isBackPressed);

    public abstract void completeSurvey();

    public abstract boolean isHistoricNewReceiptBalanceFragment(Activity activity);


    public ADashboardActivityStrategy(DashboardActivity dashboardActivity) {
        mDashboardActivity = dashboardActivity;
    }

    public void prepareLocationListener(Activity activity, SurveyDB surveyDB) {

        SurveyLocationListener locationListener = new SurveyLocationListener(surveyDB.getId_survey());

        LocationManager locationManager = null;
        try {
            locationManager =
                    (LocationManager) LocationMemory.getContext().getSystemService(
                            Context.LOCATION_SERVICE);
        }
        catch (NullPointerException e){
            new EmptyLocationException(e);
        }

        if (locationManager == null)
        {
            saveDefaultLocationIfIsNotAvailable(activity, locationListener);
            return;
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Log.d(TAG, "requestLocationUpdates via NETWORK");
            if (ActivityCompat.checkSelfPermission(activity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                saveDefaultLocationIfIsNotAvailable(activity, locationListener);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "requestLocationUpdates via GPS");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                    locationListener);
        } else {
            Location lastLocation = locationManager.getLastKnownLocation(
                    LocationManager.NETWORK_PROVIDER);

            if (lastLocation != null) {
                Log.d(TAG, "location not available via GPS|NETWORK, last know: " + lastLocation);
                locationListener.saveLocation(lastLocation);
            } else {
                saveDefaultLocationIfIsNotAvailable(activity, locationListener);
            }
        }
    }

    private void saveDefaultLocationIfIsNotAvailable(Activity activity,
            SurveyLocationListener locationListener) {
        Location defaultLocation = createDefaultLocation(activity);
        locationListener.saveLocation(defaultLocation);
    }

    @NonNull
    private Location createDefaultLocation(Activity activity) {
        String defaultLatitude = activity.getString(
                R.string.GPS_LATITUDE_DEFAULT);
        String defaultLongitude = activity.getString(
                R.string.GPS_LONGITUDE_DEFAULT);
        Location defaultLocation = new Location(
                activity.getString(R.string.GPS_PROVIDER_DEFAULT));
        defaultLocation.setLatitude(Double.parseDouble(defaultLatitude));
        defaultLocation.setLongitude(Double.parseDouble(defaultLongitude));
        Log.d(TAG, "location not available via GPS|NETWORK, default: " + defaultLocation);
        return defaultLocation;
    }

    public void showFirstFragment() {
        mDashboardActivity.setLoadingReview(false);
        unsentFragment = new DashboardUnsentFragment();
        unsentFragment.setArguments(mDashboardActivity.getIntent().getExtras());
        mDashboardActivity.replaceListFragment(R.id.dashboard_details_container, unsentFragment);
        unsentFragment.reloadHeader(mDashboardActivity);

    }

    public void reloadFirstFragment() {
        if (unsentFragment != null) {
            unsentFragment.reloadData();
        }
        mDashboardActivity.findViewById(R.id.common_header).setVisibility(View.VISIBLE);
    }

    public void reloadFirstFragmentHeader() {
        if (unsentFragment != null) {
            unsentFragment.reloadHeader(mDashboardActivity);
        }
    }

    public void showSecondFragment() {
        sentFragment = new DashboardSentFragment();
        sentFragment.setArguments(mDashboardActivity.getIntent().getExtras());
        sentFragment.reloadData();
        mDashboardActivity.replaceListFragment(R.id.dashboard_completed_container, sentFragment);
    }

    public void reloadSecondFragment() {
        sentFragment.reloadData();
        sentFragment.reloadHeader(mDashboardActivity);
    }

    public void showFourthFragment() {
        if (monitorFragment == null) {
            monitorFragment = new MonitorFragment();
        }
        mDashboardActivity.replaceFragment(R.id.dashboard_charts_container, monitorFragment);
    }

    public void hideMonitoring(){
        mDashboardActivity.findViewById(R.id.dashboard_charts_container).setVisibility(View.GONE);
    }

    public void reloadFourthFragment() {
        monitorFragment.reloadData();
        monitorFragment.reloadHeader(mDashboardActivity);
    }

    public void showAVFragment() {
    }

    public int getSurveyContainer() {
        return R.id.dashboard_details_container;
    }

    public void showUnsentFragment() {
        reloadFirstFragment();
        showFirstFragment();
    }


    public void initNavigationController() throws LoadingNavigationControllerException {
        IProgramRepository programRepository = new ProgramRepository();
        Program userProgram = programRepository.getUserProgram();
        ProgramDB program = ProgramDB.findByName(userProgram.getCode());

        TabDB userProgramTab = TabDB.findTabByProgram(program.getId_program()).get(0);
        NavigationBuilder.getInstance().buildController(userProgramTab);
    }

    public void onUnsentTabSelected(DashboardActivity dashboardActivity) {

    }

    public void onSentTabSelected(DashboardActivity dashboardActivity) {

    }

    public void openSentSurvey() {
        mDashboardActivity.getTabHost().setCurrentTabByTag(
                mDashboardActivity.getResources().getString(R.string.tab_tag_assess));
        mDashboardActivity.initSurvey();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public abstract void reloadAVFragment();

    public boolean onWebViewBackPressed(TabHost tabHost) {
        return false;
    }

    public void onResume() {

    }

    public void exitReview(boolean fromSurveysList) {
        if (!DynamicTabAdapter.isClicked || fromSurveysList) {
            DynamicTabAdapter.isClicked = true;
            mDashboardActivity.reviewShowDone(fromSurveysList);
        }
    }

    public void onPause() {

    }

    public void onConnectivityStatusChange() {

    }

    /**
     * Checks if a survey fragment is active
     */
    protected boolean isReviewFragmentActive(Fragment reviewFragment) {
        return isFragmentActive(reviewFragment, getSurveyContainer());
    }

    /**
     * Checks if a survey fragment is active
     */
    protected boolean isSurveyFragmentActive(Fragment surveyFragment) {
        return isFragmentActive(surveyFragment, getSurveyContainer());
    }

    protected boolean isNewHistoricReceiptBalanceFragmentActive() {
        return isHistoricNewReceiptBalanceFragment(mDashboardActivity);
    }

    /**
     * Checks if a dashboardUnsentFragment is active
     */
    public boolean isFragmentActive(Fragment fragment, int layout) {
        Fragment currentFragment = mDashboardActivity.getFragmentManager().findFragmentById(layout);
        if (currentFragment != null && currentFragment.equals(fragment)) {
            return true;
        }
        return false;
    }

    public void initTabWidget(final TabHost tabHost, final Fragment reviewFragment,
            final Fragment surveyFragment,
            final boolean isReadOnly) {
          /* set tabs in order */
        LayoutUtils.setTabHosts(mDashboardActivity);
        LayoutUtils.setTabDivider(mDashboardActivity);
        //set the tabs background as transparent
        setTabsBackgroundColor(R.color.tab_unpressed_background, tabHost);

        //set first tab as selected:
        tabHost.getTabWidget().getChildAt(0).setBackgroundColor(
                mDashboardActivity.getResources().getColor(R.color.tab_pressed_background));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                /** If current tab is android */

                //set the tabs background as transparent
                setTabsBackgroundColor(R.color.tab_unpressed_background, tabHost);

                //If change of tab from surveyFragment or FeedbackFragment they could be closed.
                if (isSurveyFragmentActive(surveyFragment)) {
                    mDashboardActivity.onSurveyBackPressed();
                }
                if (isReviewFragmentActive(reviewFragment)) {
                    mDashboardActivity.exitReviewOnChangeTab(null);
                }
                if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_assess))) {
                    if (!isReadOnly) {
                        reloadFirstFragment();
                    }
                    reloadFirstFragmentHeader();
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_improve))) {
                    reloadSecondFragment();
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_stock))) {
                    reloadStockFragment(mDashboardActivity);
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_monitor))) {
                    if (GradleVariantConfig.isMonitoringFragmentActive()) {
                        reloadFourthFragment();
                    }
                } else if (tabId.equalsIgnoreCase(
                        mDashboardActivity.getResources().getString(R.string.tab_tag_av))) {
                    reloadAVFragment();
                }
                tabHost.getCurrentTabView().setBackgroundColor(
                        mDashboardActivity.getResources().getColor(R.color.tab_pressed_background));
            }
        });

        // init tabHost
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setFocusable(false);
        }


        ((TextView) mDashboardActivity.findViewById(R.id.header_extra_info)).setText(
                Utils.getInternationalizedString(R.string.unsent_vouchers, mDashboardActivity));
    }

    protected void setTabsBackgroundColor(int color, TabHost tabHost) {
        //set the tabs background as transparent
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundColor(
                    mDashboardActivity.getResources().getColor(color));
        }
    }

    public void setStockTab(TabHost tabHost) {
        setTab(tabHost, mDashboardActivity.getResources().getString(R.string.tab_tag_stock),
                R.id.tab_stock_layout,
                mDashboardActivity.getResources().getDrawable(R.drawable.tab_stock));
    }

    protected void setTab(TabHost tabHost, String tabName, int layout, Drawable image) {
        TabHost.TabSpec tab = tabHost.newTabSpec(tabName);
        tab.setContent(layout);
        tab.setIndicator("", image);
        tabHost.addTab(tab);
        addTagToLastTab(tabHost, tabName);
    }

    private void addTagToLastTab(TabHost tabHost, String tabName) {
        TabWidget tabWidget = tabHost.getTabWidget();
        int numTabs = tabWidget.getTabCount();
        ViewGroup tabIndicator = (ViewGroup) tabWidget.getChildTabViewAt(numTabs - 1);

        ImageView imageView = (ImageView) tabIndicator.getChildAt(0);
        imageView.setTag(tabName);
        TextView textView = (TextView) tabIndicator.getChildAt(1);
        textView.setGravity(Gravity.CENTER);
        textView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;

    }

    public void onStart(){
    }

    public void initStockControlFragment() {

    }
    public void setStockControlTab(TabHost tabHost) {

    }

    public boolean isStockTableFragmentActive(DashboardActivity dashboardActivity) {
        return false;
    }

    protected class AsyncAnnouncement extends AsyncTask<Void, Void, Void> {
        UserDB mLoggedUserDB;

        @Override
        protected Void doInBackground(Void... params) {
            mLoggedUserDB = UserDB.getLoggedUser();
            if (mLoggedUserDB != null) {
                try {
                    mLoggedUserDB = ServerAPIController.pullUserAttributes(mLoggedUserDB);
                } catch (ApiCallException e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mLoggedUserDB != null) {
                if (mLoggedUserDB.getAnnouncement() != null
                        && !mLoggedUserDB.getAnnouncement().equals("")
                        && !PreferencesState.getInstance().isUserAccept()) {
                    Log.d(TAG, "show logged announcement");
                    AnnouncementMessageDialog.showAnnouncement(R.string.admin_announcement,
                            mLoggedUserDB.getAnnouncement(),
                            mDashboardActivity);
                } else {
                    AnnouncementMessageDialog.checkUserClosed(mLoggedUserDB,
                            mDashboardActivity);
                }
            }
        }
    }
}
