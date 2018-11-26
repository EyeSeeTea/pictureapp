package org.eyeseetea.malariacare.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.QuestionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ValueLocalDataSource;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.usecase.CreateSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsByProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveValueUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.AddBalanceReceiptAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.AddBalanceReceiptPresenter;

import java.util.List;


public class AddBalanceReceiptFragment extends Fragment implements
        AddBalanceReceiptPresenter.AddBalanceReceiptView,
        AddBalanceReceiptAdapter.onInteractionListener {
    private static final String TYPE_SURVEY = "TYPE_SURVEY";

    private int typeSurvey;
    private AddBalanceReceiptPresenter mAddBalanceReceiptPresenter;
    private RecyclerView mQuestionsList;


    public AddBalanceReceiptFragment() {
        // Required empty public constructor
    }

    public static AddBalanceReceiptFragment newInstance(int typeSurvey) {
        AddBalanceReceiptFragment fragment = new AddBalanceReceiptFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_SURVEY, typeSurvey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle == null) {
            bundle = savedInstanceState;
        }
        if (bundle != null) {
            typeSurvey = bundle.getInt(TYPE_SURVEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_balance_receipt, container, false);
        initRecyclerView(view);
        initSaveButton(view);
        initPresenter();
        return view;
    }

    private void initSaveButton(View view) {
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddBalanceReceiptPresenter.onCompletedSurvey();
            }
        });
    }

    private void initRecyclerView(View view) {
        mQuestionsList = (RecyclerView) view.findViewById(R.id.question_list);
    }

    private void initPresenter() {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IProgramRepository programRepository = new ProgramLocalDataSource();
        IOrganisationUnitRepository organisationUnitRepository = new OrganisationUnitRepository();
        IUserRepository userRepository = new UserAccountDataSource();
        IQuestionRepository questionRepository = new QuestionLocalDataSource();
        IValueRepository valueRepository = new ValueLocalDataSource();

        CreateSurveyUseCase createSurveyUseCase = new CreateSurveyUseCase(mainExecutor,
                asyncExecutor, surveyRepository, programRepository, organisationUnitRepository,
                userRepository);
        GetQuestionsByProgramUseCase getQuestionsByProgramUseCase =
                new GetQuestionsByProgramUseCase(mainExecutor, asyncExecutor, questionRepository);
        SaveSurveyUseCase saveSurveyUseCase = new SaveSurveyUseCase(asyncExecutor, mainExecutor,
                surveyRepository);

        mAddBalanceReceiptPresenter = new AddBalanceReceiptPresenter(createSurveyUseCase,
                getQuestionsByProgramUseCase, valueRepository, saveSurveyUseCase);
        mAddBalanceReceiptPresenter.attachView(this,
                getActivity().getResources().getString(R.string.stock_program_uid), typeSurvey);
    }

    @Override
    public void showQuestions(List<Question> questions, String defValue) {
        mQuestionsList.setAdapter(new AddBalanceReceiptAdapter(questions, this,defValue));
        mQuestionsList.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void closeFragment() {
        ((DashboardActivity) getActivity()).closeStockTableFragment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAddBalanceReceiptPresenter.detachView();
    }

    @Override
    public void onQuestionAnswered(Question question, String answeredValue) {
        mAddBalanceReceiptPresenter.onQuestionAnswerTextChange(question.getUid(), answeredValue);
    }
}
