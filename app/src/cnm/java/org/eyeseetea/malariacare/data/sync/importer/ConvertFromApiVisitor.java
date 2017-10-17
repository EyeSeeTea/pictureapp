package org.eyeseetea.malariacare.data.sync.importer;

import android.util.Log;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ConvertFromApiVisitor implements IConvertFromApiVisitor {
    @Override
    public void visit(List<OrgUnitTree> orgUnitTrees) {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOutput(
                Constants.DROPDOWN_LIST_OU_TREE);
        QuestionDB questionDB = questionDBs.get(0);
        List<OptionDB> provinceOptions = new ArrayList<>();
        List<OptionDB> districtOptions = new ArrayList<>();
        List<OptionDB> communeOptions = new ArrayList<>();
        List<Model> villageOptions = new ArrayList<>();
        List<Model> villageOptionAttributes = new ArrayList<>();
        for (OrgUnitTree orgUnitTree : orgUnitTrees) {
            if (orgUnitTree.getName_Prov_E() != null
                    && !orgUnitTree.getName_Prov_E().isEmpty()) {
                OptionDB optionProb = new OptionDB(orgUnitTree.getName_Prov_E(),
                        orgUnitTree.getName_Prov_E(), 0f, questionDB.getAnswerDB());
                if (!provinceOptions.contains(optionProb)) {
                    provinceOptions.add(optionProb);
                    optionProb.save();
                } else {
                    optionProb = provinceOptions.get(provinceOptions.indexOf(optionProb));
                }

                if (orgUnitTree.getName_Dist_E() != null
                        && !orgUnitTree.getName_Dist_E().isEmpty()) {
                    OptionDB optionDist = new OptionDB(orgUnitTree.getName_Dist_E(),
                            orgUnitTree.getName_Dist_E() + "", 0f, questionDB.getAnswerDB());
                    optionDist.setId_parent_fk(optionProb.getId_option());
                    if (!districtOptions.contains(optionDist)) {
                        districtOptions.add(optionDist);
                        optionDist.save();
                    } else {
                        optionDist = districtOptions.get(districtOptions.indexOf(optionDist));
                    }

                    if (orgUnitTree.getName_Comm_E() != null
                            && !orgUnitTree.getName_Comm_E().isEmpty()) {
                        OptionDB optionComm = new OptionDB(orgUnitTree.getName_Comm_E(),
                                orgUnitTree.getName_Comm_E() + "", 0f,
                                questionDB.getAnswerDB());
                        optionComm.setId_parent_fk(optionDist.getId_option());
                        if (!communeOptions.contains(optionComm)) {
                            communeOptions.add(optionComm);
                            optionComm.save();
                        } else {
                            optionComm = communeOptions.get(communeOptions.indexOf(optionComm));
                        }

                        if (orgUnitTree.getName_Vill_E() != null
                                && !orgUnitTree.getName_Vill_E().isEmpty()) {
                            OptionDB optionVill = new OptionDB(orgUnitTree.getName_Vill_E(),
                                    orgUnitTree.getName_Vill_E() + "", 0f,
                                    questionDB.getAnswerDB());
                            optionVill.setId_parent_fk(optionComm.getId_option());
                            OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
                            optionAttributeDB.setPath(
                                    "[" + orgUnitTree.getLat() + "," + orgUnitTree.getLng()
                                            + "]");
                            villageOptionAttributes.add(optionAttributeDB);
                            villageOptions.add(optionVill);
                        }
                    }
                }
            }
        }
        Log.d("SAVING LIST", "saving optrionstree list");
        saveBatch(villageOptionAttributes);
        int i=0;
        for (Model villageOption : villageOptions) {
                ((OptionDB) villageOption).setOptionAttributeDB(
                        (OptionAttributeDB) villageOptionAttributes.get(i));
            i++;
        }
        saveBatch(villageOptions);
//TODO Not saving relations because dynamicTabAdapter not work well with to many relations. Do it
// when refactor dynamic tab adapter.
//        generateRelations(villageOptions, questionDB);
    }

    private void generateRelations(List<Model> villageOptions, QuestionDB questionDB) {
        List<QuestionDB> hiddenQuestions = QuestionDB.getAllQuestionsWithOutput(
                Constants.HIDDEN);
        QuestionDB hiddenQuestion = null;
        for (QuestionDB question : hiddenQuestions) {
            if (question.getHeaderDB().getTabDB().equals(
                    questionDB.getHeaderDB().getTabDB())) {
                hiddenQuestion = question;
            }
        }
        if (hiddenQuestion != null) {
            List<Model> questionRelations = new ArrayList<>();
            List<Model> matches = new ArrayList<>();
            List<Model> questionOptions = new ArrayList<>();
            for (int i = 0; i < villageOptions.size(); i++) {
                QuestionRelationDB questionRelationDB = new QuestionRelationDB(hiddenQuestion,
                        QuestionRelationDB.MATCH_WITH_OPTION_ATTRIBUTE);
                questionRelations.add(questionRelationDB);
            }
            saveBatch(questionRelations);
            for (Model questionRelation : questionRelations) {
                MatchDB match = new MatchDB((QuestionRelationDB) questionRelation);
                matches.add(match);
            }
            saveBatch(matches);
            int i = 0;
            for (Model match : matches) {
                QuestionOptionDB questionOptionDB = new QuestionOptionDB(
                        ((OptionDB) villageOptions.get(i)), questionDB, (MatchDB) match);
                questionOptions.add(questionOptionDB);
                i++;
            }
            saveBatch(questionOptions);
        }
    }


    private static void saveBatch(final List<Model> insertModels) {
//Save questions in batch

        DatabaseDefinition databaseDefinition =
                FlowManager.getDatabase(AppDatabase.class);
        databaseDefinition.executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (Model model : insertModels) {
                    model.insert();
                }
            }
        });
    }

}
