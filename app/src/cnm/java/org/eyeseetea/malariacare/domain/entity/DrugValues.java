package org.eyeseetea.malariacare.domain.entity;

public class DrugValues {
    private String drugLabel;
    private int received;
    private int usedToday;
    private int expense;

    public DrugValues(String drugLabel, int received, int usedToday, int expense) {
        this.drugLabel = drugLabel;
        this.received = received;
        this.usedToday = usedToday;
        this.expense = expense;
    }

    public String getDrugLabel() {
        return drugLabel;
    }

    public int getReceived() {
        return received;
    }

    public int getUsedToday() {
        return usedToday;
    }

    public int getExpense() {
        return expense;
    }

    public int getTotal() {
        return received - expense;
    }
}
