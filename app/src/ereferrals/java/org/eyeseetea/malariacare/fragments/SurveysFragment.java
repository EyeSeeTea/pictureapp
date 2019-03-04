package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.SwipeRecyclerViewSurveysCallback;
import org.eyeseetea.malariacare.layout.adapters.survey.SurveysAdapter;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;

import java.util.ArrayList;
import java.util.List;

public class SurveysFragment extends Fragment implements IDashboardFragment {
    public static final String TAG = ".UnsentFragment";

    protected SurveysAdapter adapter;
    private SurveyReceiver surveyReceiver;
    private List<SurveyDB> mSurveyDBs;

    RecyclerView mRecyclerView;

    private View rootView;

    public SurveysFragment() {
        this.mSurveyDBs = new ArrayList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        if (container == null) {
            return null;
        }

        rootView = inflater.inflate(R.layout.survey_list_fragment, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        initRecyclerView();
        registerForContextMenu(mRecyclerView);
    }


    @Override
    public void onResume() {
        Log.d(TAG, "AndroidLifeCycle: onResume");
        registerFragmentReceiver();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "AndroidLifeCycle: onDestroy");
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "AndroidLifeCycle: onStart");
        super.onStart();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "AndroidLifeCycle: onPause");
        super.onPause();
    }


    @Override
    public void onStop() {
        Log.d(TAG, "AndroidLifeCycle: onStop");
        unregisterFragmentReceiver();

        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initRecyclerView() {
        mRecyclerView = rootView.findViewById(R.id.surveyList);
        adapter = new SurveysAdapter(this.mSurveyDBs);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnSurveyClickListener(new SurveysAdapter.OnSurveyClickListener() {
            @Override
            public void onSurveyClick(View view, SurveyDB surveyDB) {
                Session.setMalariaSurveyDB(surveyDB);
                DashboardActivity.dashboardActivity.openSentSurvey();
            }
        });

        SwipeRecyclerViewSurveysCallback swipeRecyclerViewSurveysCallback = new
                SwipeRecyclerViewSurveysCallback(getActivity(), adapter);

        swipeRecyclerViewSurveysCallback.setOnSurveySwipeListener(
                new SwipeRecyclerViewSurveysCallback.OnSurveySwipeListener() {
                    @Override
                    public void onSurveySwipe(final SurveyDB surveyDB) {
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle(getActivity().getString(
                                        R.string.dialog_title_delete_survey))
                                .setMessage(getActivity().getString(
                                        R.string.dialog_info_delete_survey))
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                                deleteSurvey(surveyDB);

                                            }
                                        })
                                .setNegativeButton(android.R.string.no,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                refreshRecyclerView();
                                            }
                                        }).create();

                        dialog.show();

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                refreshRecyclerView();
                            }
                        });
                    }
                });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeRecyclerViewSurveysCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void deleteSurvey(SurveyDB surveyDB) {
        surveyDB.delete();
        mSurveyDBs.remove(mSurveyDBs.indexOf(surveyDB));
        refreshRecyclerView();
    }

    /**
     * Register a survey receiver to load surveys into the listadapter
     */
    public void registerFragmentReceiver() {
        Log.d(TAG, "initializeSurvey");

        if (surveyReceiver == null) {
            surveyReceiver = new SurveyReceiver();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(surveyReceiver,
                    new IntentFilter(SurveyService.ALL_UNSENT_SURVEYS_ACTION));
        }
    }


    /**
     * Unregisters the survey receiver.
     * It really important to do this, otherwise each receiver will invoke its code.
     */
    public void unregisterFragmentReceiver() {
        Log.d(TAG, "unregisterFragmentReceiver");
        if (surveyReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(surveyReceiver);
            surveyReceiver = null;
        }
    }

    public void reloadHeader(Activity activity) {
        reloadHeader(activity, R.string.tab_tag_assess);
    }

    public void reloadHeader(Activity activity, int id) {
        DashboardHeaderStrategy.getInstance().init(activity, id);
    }


    public void reloadData() {
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD, SurveyService.RELOAD_DASHBOARD_ACTION);
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }

    public void reloadSurveys(List<SurveyDB> newListSurveyDBs) {
        this.mSurveyDBs.clear();
        this.mSurveyDBs.addAll(newListSurveyDBs);

        this.adapter.setItems(mSurveyDBs);

    }

    public void refreshRecyclerView() {
        this.adapter.setItems(mSurveyDBs);

    }

    public void reloadSurveysFromService(List<SurveyDB> surveysUnsentFromService) {
        reloadSurveys(surveysUnsentFromService);
    }

    /**
     * Inner private class that receives the result from the service
     */
    public class SurveyReceiver extends BroadcastReceiver {
        private SurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");
            //Listening only intents from this method
            if (SurveyService.ALL_UNSENT_SURVEYS_ACTION.equals(intent.getAction())) {
                List<SurveyDB> surveysUnsentFromService;
                Session.valuesLock.readLock().lock();
                try {
                    surveysUnsentFromService = (List<SurveyDB>) Session.popServiceValue(
                            SurveyService.ALL_UNSENT_SURVEYS_ACTION);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                reloadSurveysFromService(surveysUnsentFromService);
            }
        }
    }
}