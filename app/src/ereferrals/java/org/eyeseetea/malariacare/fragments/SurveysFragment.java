package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.usecase.DeleteSurveyByUidUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsByProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.domain.usecase.GetUserProgramUseCase;
import org.eyeseetea.malariacare.factories.MetadataFactory;
import org.eyeseetea.malariacare.factories.SurveyFactory;
import org.eyeseetea.malariacare.layout.SwipeRecyclerViewSurveysCallback;
import org.eyeseetea.malariacare.layout.adapters.survey.SurveysAdapter;
import org.eyeseetea.malariacare.presentation.models.SurveyViewModel;
import org.eyeseetea.malariacare.presentation.presenters.SurveysPresenter;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SurveysFragment extends Fragment implements IDashboardFragment, SurveysPresenter.View {
    public static final String TAG = ".UnsentFragment";

    protected SurveysAdapter adapter;
    private List<SurveyViewModel> mSurveyDBs;
    RecyclerView mRecyclerView;


    private SurveysPresenter presenter;
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

        initRecyclerView();
        initPresenter();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        registerForContextMenu(mRecyclerView);
    }

    private void initPresenter() {
        MetadataFactory metadataFactory = new MetadataFactory();
        SurveyFactory surveyFactory = new SurveyFactory();

        GetUserProgramUseCase getUserProgramUseCase = metadataFactory.getUserProgramUseCase();

        GetSurveysByProgram getSurveysByProgram = surveyFactory.getSurveysUseCaseByprogram();

        GetQuestionsByProgramUseCase getQuestionsByProgramUseCase =
                metadataFactory.getQuestionsByProgramUseCase();

        DeleteSurveyByUidUseCase deleteSurveyByUidUseCase =
                surveyFactory.deleteSurveyByUidUseCase();

        presenter = new
                SurveysPresenter(getUserProgramUseCase, getSurveysByProgram,
                getQuestionsByProgramUseCase, deleteSurveyByUidUseCase);

        presenter.attachView(this);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "AndroidLifeCycle: onResume");
        registerFragmentReceiver();

        super.onResume();
    }

    @Override
    public void onStop() {
        presenter.detachView();
        Log.d(TAG, "AndroidLifeCycle: onStop");
        unregisterFragmentReceiver();

        super.onStop();
    }

    @Override
    public void reloadData() {
        if (presenter != null) {
            presenter.refresh();
        }
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
            public void onSurveyClick(View view, SurveyViewModel surveyViewModel) {
                presenter.selectSurvey(surveyViewModel.getUid());
            }
        });

        SwipeRecyclerViewSurveysCallback swipeRecyclerViewSurveysCallback = new
                SwipeRecyclerViewSurveysCallback(getActivity(), adapter);

        swipeRecyclerViewSurveysCallback.setOnSurveySwipeListener(
                new SwipeRecyclerViewSurveysCallback.OnSurveySwipeListener() {
                    @Override
                    public void onSurveySwipe(final SurveyViewModel surveyViewModel) {
                        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                .setTitle(getActivity().getString(
                                        R.string.dialog_title_delete_survey))
                                .setMessage(getActivity().getString(
                                        R.string.dialog_info_delete_survey))
                                .setPositiveButton(android.R.string.yes,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                                presenter.deleteSurvey(surveyViewModel.getUid());

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

    public void reloadHeader(Activity activity) {
        reloadHeader(activity, R.string.tab_tag_assess);
    }

    public void reloadHeader(Activity activity, int id) {
        DashboardHeaderStrategy.getInstance().init(activity, id);
    }

    public void refreshRecyclerView() {
        this.adapter.setItems(mSurveyDBs);

    }

    @Override
    public void showSurveys(List<SurveyViewModel> surveyViewModels) {
        mSurveyDBs = surveyViewModels;
        adapter.setItems(surveyViewModels);
    }

    @Override
    public void showErrorLoadingSurveys() {
        Toast.makeText(getActivity(),
                Utils.getInternationalizedString(R.string.surveys_error_loading, getActivity()),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToSurvey(String surveyUid) {
        //TODO: To navigate a survey should not be coupled to SurveyDB
        Session.setMalariaSurveyDB(SurveyDB.findByUid(surveyUid));
        DashboardActivity.dashboardActivity.openSentSurvey();
    }

    //This methods is not used but be here because dashboard fragments must to have it
    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }
}