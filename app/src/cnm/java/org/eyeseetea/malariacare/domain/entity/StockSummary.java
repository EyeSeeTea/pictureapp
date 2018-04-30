package org.eyeseetea.malariacare.domain.entity;

public class StockSummary {
    private String stockName;
    private int received;
    private int usedToday;
    private int expense;

    public StockSummary(String stockName, int received, int usedToday, int expense) {
        this.stockName = stockName;
        this.received = received;
        this.usedToday = usedToday;
        this.expense = expense;
    }

    public String getStockName() {
        return stockName;
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
