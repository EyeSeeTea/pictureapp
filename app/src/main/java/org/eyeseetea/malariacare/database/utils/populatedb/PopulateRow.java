package org.eyeseetea.malariacare.database.utils.populatedb;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.database.model.Answer;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Tab;

import java.util.HashMap;

/**
 * Created by manuel on 11/01/17.
 */

public class PopulateRow {

    static Question populateQuestion(String[] line, HashMap<Long, Header> headerFK,
            HashMap<Long, Answer> answerFK, @Nullable Question question) {
        if (question == null) {
            question = new Question();
        }
        question.setCode(line[1]);
        question.setDe_name(line[2]);
        question.setHelp_text(line[3]);
        question.setForm_name(line[4]);
        question.setUid(line[5]);
        question.setOrder_pos(Integer.valueOf(line[6]));
        question.setNumerator_w(Float.valueOf(line[7]));
        question.setDenominator_w(Float.valueOf(line[8]));
        question.setHeader(headerFK.get(Long.valueOf(line[9])));
        if (!line[10].equals("")) {
            question.setAnswer(answerFK.get(Long.valueOf(line[10])));
        }
        question.setOutput(Integer.valueOf(line[12]));
        question.setTotalQuestions(Integer.valueOf(line[13]));
        question.setVisible(Integer.valueOf(line[14]));
        if (line.length > 15 && !line[15].equals("")) {
            question.setPath((line[15]));
        }
        if (line.length > 16 && !line[16].equals("")) {
            question.setCompulsory(Integer.valueOf(line[16]));
        } else {
            question.setCompulsory(Question.QUESTION_NO_COMPULSORY);
        }
        return question;
    }

    static Answer populateAnswer(String line[], @Nullable Answer answer) {
        if (answer == null) {
            answer = new Answer();
        }
        answer.setName(line[1]);
        return answer;
    }

    static Header populateHeader(String line[], HashMap<Long, Tab> tabsFK,
            @Nullable Header header) {
        if (header == null) {
            header = new Header();
        }
        header.setShort_name(line[1]);
        header.setName(line[2]);
        header.setOrder_pos(Integer.valueOf(line[3]));
        header.setTab(tabsFK.get(Long.valueOf(line[4])));
        return header;
    }

    static Program populateProgram(String[] line, @Nullable Program program) {
        if (program == null) {
            program = new Program();
        }
        program.setUid(line[1]);
        program.setName(line[2]);
        return program;
    }

    static Tab populateTab(String[] line, HashMap<Long, Program> programFK, @Nullable Tab tab) {
        if (tab == null) {
            tab = new Tab();
        }
        tab.setName(line[1]);
        tab.setOrder_pos(Integer.valueOf(line[2]));
        tab.setProgram(programFK.get(Long.valueOf(line[3])));
        tab.setType(Integer.valueOf(line[4]));
        return tab;
    }

}
