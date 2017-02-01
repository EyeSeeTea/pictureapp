package org.eyeseetea.malariacare.domain.entity;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.List;

public class ValidationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    Object mockObject1 = new Object();
    Object mockObject2 = new Object();
    Object mockObject3 = new Object();

    public final static String ERROR_ZERO = "Error object zero";
    public final static String ERROR_ONE = "Error object one";
    public final static String ERROR_TWO = "Error object two";

    @Before
    public void setup() {
        Validation validation = Validation.init();

        validation.addInput(mockObject1);
        validation.addInput(mockObject2);
        validation.addInput(mockObject3);
    }

    @Test
    public void addAndRecoveryFirstError() {
        Validation.getInstance().addinvalidInput(mockObject1, ERROR_ZERO);
        List<Object> activeInputs = Validation.getInstance().getActiveInputs();
        HashMap<Object, String> invalidInputs = Validation.getInstance().getInvalidInputs();
        for (Object activeInput : activeInputs) {
            if (invalidInputs.containsKey(activeInput)) {
                assertTrue(invalidInputs.get(activeInput).equals(ERROR_ZERO));
                assertFalse(invalidInputs.get(activeInput).equals(ERROR_ONE));
                assertFalse(invalidInputs.get(activeInput).equals(ERROR_TWO));
            }
        }
        assertTrue(Validation.getInstance().hasErrors());
    }

    @Test
    public void addAndRecoverySecondError() {
        Validation.getInstance().addinvalidInput(mockObject2, ERROR_ONE);
        List<Object> activeInputs = Validation.getInstance().getActiveInputs();
        HashMap<Object, String> invalidInputs = Validation.getInstance().getInvalidInputs();
        for (Object activeInput : activeInputs) {
            if (invalidInputs.containsKey(activeInput)) {
                assertTrue(invalidInputs.get(activeInput).equals(ERROR_ONE));
                assertFalse(invalidInputs.get(activeInput).equals(ERROR_ZERO));
                assertFalse(invalidInputs.get(activeInput).equals(ERROR_TWO));
            }
        }
        assertTrue(Validation.getInstance().hasErrors());
    }

    @Test
    public void addAndRecoveryThirdError() {
        Validation.getInstance().addinvalidInput(mockObject3, ERROR_TWO);
        List<Object> activeInputs = Validation.getInstance().getActiveInputs();
        HashMap<Object, String> invalidInputs = Validation.getInstance().getInvalidInputs();
        for (Object activeInput : activeInputs) {
            if (invalidInputs.containsKey(activeInput)) {
                assertTrue(invalidInputs.get(activeInput).equals(ERROR_TWO));
                assertFalse(invalidInputs.get(activeInput).equals(ERROR_ZERO));
                assertFalse(invalidInputs.get(activeInput).equals(ERROR_ONE));
            }
        }
        assertTrue(Validation.getInstance().hasErrors());
    }

    @Test
    public void addAndRecoveryAllErrors() {
        Validation.getInstance().addinvalidInput(mockObject1, ERROR_ZERO);
        Validation.getInstance().addinvalidInput(mockObject2, ERROR_ONE);
        Validation.getInstance().addinvalidInput(mockObject3, ERROR_TWO);
        List<Object> activeInputs = Validation.getInstance().getActiveInputs();
        HashMap<Object, String> invalidInputs = Validation.getInstance().getInvalidInputs();
        int numberOfErrors = 0;

        assertTrue(invalidInputs.get(mockObject1).equals(ERROR_ZERO));
        assertFalse(invalidInputs.get(mockObject1).equals(ERROR_ONE));
        assertFalse(invalidInputs.get(mockObject1).equals(ERROR_TWO));

        assertTrue(invalidInputs.get(mockObject2).equals(ERROR_ONE));
        assertFalse(invalidInputs.get(mockObject2).equals(ERROR_ZERO));
        assertFalse(invalidInputs.get(mockObject2).equals(ERROR_TWO));

        assertTrue(invalidInputs.get(mockObject3).equals(ERROR_TWO));
        assertFalse(invalidInputs.get(mockObject3).equals(ERROR_ZERO));
        assertFalse(invalidInputs.get(mockObject3).equals(ERROR_ONE));
        for (Object activeInput : activeInputs) {
            if (invalidInputs.containsKey(activeInput)) {
                if (activeInput.equals(mockObject1)) {
                    numberOfErrors++;
                } else if (activeInput.equals(mockObject2)) {
                    numberOfErrors++;
                } else if (activeInput.equals(mockObject3)) {
                    numberOfErrors++;
                }
            }
        }
        assertTrue(numberOfErrors == 3);
        assertTrue(Validation.getInstance().hasErrors());
    }

    @Test
    public void addAndRecoveryRemovedErrors() {
        addAndRecoveryAllErrors();
        Validation.getInstance().removeInputError(mockObject1);
        Validation.getInstance().removeInputError(mockObject2);
        Validation.getInstance().removeInputError(mockObject3);
        assertFalse(Validation.getInstance().hasErrors());
    }

}
