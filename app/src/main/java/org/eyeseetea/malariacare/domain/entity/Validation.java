package org.eyeseetea.malariacare.domain.entity;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Validation {
    private static Validation instance;
    private static List<Object> activeInputs;
    private static HashMap<Object, String> invalidInputs;

    public Validation() {
        activeInputs = new ArrayList<>();
        invalidInputs = new HashMap<>();
    }

    public static Validation init() {
        instance = new Validation();
        return instance;
    }

    public static Validation getInstance() {
        if (instance == null) {
            instance = init();
        }
        return instance;
    }

    public static boolean hasErrors() {
        if (invalidInputs.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static void showErrors() {
        for (Object activeInput : activeInputs) {
            if (invalidInputs.containsKey(activeInput)) {
                String error = invalidInputs.get(activeInput);
                ((TextView) activeInput).setError(error);
            }
        }
    }

    public void removeInputError(Object view) {
        invalidInputs.remove(view);
    }

    public void addInput(Object view) {
        activeInputs.add(view);
    }

    public void addinvalidInput(Object view, String error) {
        invalidInputs.put(view, error);
    }

    public List<Object> getActiveInputs() {
        return activeInputs;
    }

    public HashMap<Object, String> getInvalidInputs() {
        return invalidInputs;
    }
}

