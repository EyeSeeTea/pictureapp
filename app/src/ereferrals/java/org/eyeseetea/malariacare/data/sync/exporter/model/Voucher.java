package org.eyeseetea.malariacare.data.sync.exporter.model;

public class Voucher {
    private static String TYPE_PAPER = "paper";

    private String type;
    private String id;

    public Voucher(String id) {
        type = TYPE_PAPER;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
