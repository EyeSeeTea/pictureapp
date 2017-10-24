package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class OuTreeMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView {
    private CustomTextView header;
    private Spinner spinnerProvince, spinnerDistrict, spinnerCommune, spinnerVillage;
    private QuestionDB mQuestionDB;
    private boolean optionSetFromSavedValue = false;
    private List<OptionDB> mOptionDBs;
    private Context mContext;


    public OuTreeMultiQuestionView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.multi_question_dropdown_tree, this);
        header = (CustomTextView) findViewById(R.id.row_header_text);
        spinnerProvince = (Spinner) findViewById(R.id.spinner_province);
        spinnerDistrict = (Spinner) findViewById(R.id.spinner_district);
        spinnerCommune = (Spinner) findViewById(R.id.spinner_commune);
        spinnerVillage = (Spinner) findViewById(R.id.spinner_village);
        mContext = context;

        setSpinnerOnItemSelectedListener(spinnerProvince, spinnerDistrict,
                context.getString(R.string.district));
        setSpinnerOnItemSelectedListener(spinnerDistrict, spinnerCommune,
                context.getString(R.string.commune));
        setSpinnerOnItemSelectedListener(spinnerCommune, spinnerVillage,
                context.getString(R.string.village));

        spinnerVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                    long l) {
                if (!optionSetFromSavedValue) {
                    if (position > 0) {
                        OptionDB optionDB = (OptionDB) adapterView.getItemAtPosition(position);
                        notifyAnswerChanged(optionDB);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(ValueDB valueDB) {
        optionSetFromSavedValue = true;
        if (valueDB == null || valueDB.getValue() == null) {
            optionSetFromSavedValue = false;
            return;
        }
        OptionDB villageOption = valueDB.getOptionDB();
        OptionDB communeOption = OptionDB.findById(villageOption.getId_parent_fk());
        OptionDB districtOption = OptionDB.findById(communeOption.getId_parent_fk());
        OptionDB provinceOption = OptionDB.findById(districtOption.getId_parent_fk());
        setValueToSpinner(spinnerProvince, provinceOption);
        setOptionsWithParent(spinnerDistrict, provinceOption.getId_option(),
                mContext.getString(R.string.district));
        setValueToSpinner(spinnerDistrict, districtOption);
        setOptionsWithParent(spinnerCommune, districtOption.getId_option(),
                mContext.getString(R.string.commune));
        setValueToSpinner(spinnerCommune, communeOption);
        setOptionsWithParent(spinnerVillage, communeOption.getId_option(),
                mContext.getString(R.string.village));
        setValueToSpinner(spinnerVillage, villageOption);
    }

    private void setValueToSpinner(Spinner spinner, OptionDB optionSelected) {
        for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
            OptionDB optionDB = (OptionDB) spinner.getItemAtPosition(i);
            if (optionDB.equals(optionSelected)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        mOptionDBs = optionDBs;
        setOptionsWithParent(spinnerProvince, null, mContext.getString(R.string.province));
    }

    @Override
    public void setQuestionDB(QuestionDB questionDB) {
        mQuestionDB = questionDB;
    }


    @Override
    public void setEnabled(boolean enabled) {
        spinnerProvince.setEnabled(enabled);
        spinnerDistrict.setEnabled(enabled);
        spinnerCommune.setEnabled(enabled);
        spinnerVillage.setEnabled(enabled);
    }

    private List<OptionDB> getOptionsWithParent(Long parent, List<OptionDB> optionsFrom) {
        List<OptionDB> optionsWithParent = new ArrayList<>();
        for (OptionDB optionDB : optionsFrom) {
            if ((parent == null && optionDB.getId_parent_fk() == null)
                    || (optionDB.getId_parent_fk() != null && optionDB.getId_parent_fk().equals(
                    parent))) {
                optionsWithParent.add(optionDB);
            }
        }
        return optionsWithParent;
    }

    private void setOptionsWithParent(Spinner spinner, Long parent, String hintText) {
        List<OptionDB> optionsWithParent = getOptionsWithParent(parent, mOptionDBs);
        optionsWithParent.add(0, new OptionDB(hintText, null, 0f, null));
        spinner.setAdapter(
                new OptionArrayAdapter(getContext(), optionsWithParent));
    }

    private void setSpinnerOnItemSelectedListener(Spinner parent,
            final Spinner child, final String defaultText) {
        parent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                    long l) {
                if (!optionSetFromSavedValue) {
                    OptionDB optionDB = (OptionDB) adapterView.getItemAtPosition(position);
                    setOptionsWithParent(child, optionDB.getId_option(), defaultText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
