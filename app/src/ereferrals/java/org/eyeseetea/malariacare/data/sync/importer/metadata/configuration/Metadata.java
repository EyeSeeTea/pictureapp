package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;

import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Metadata {
    List<Question> questions;
    HashMap<String, List<Option>> options;

    public Metadata() {
        questions = new ArrayList<>();
        options = new HashMap<>();
    }

    public void addQuestion(Question question, List<Option> questionOptions){
        questions.add(question);
        options.put(question.getCode(), questionOptions);

    }

    public List<Question> getQuestions() {
        return questions;
    }

    public HashMap<String, List<Option>> getOptions() {
        return options;
    }

    public List<Option> getOptionsByQuestion(String code){
        return options.get(code);
    }
}
