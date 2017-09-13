package org.eyeseetea.malariacare.data.sync.exporter.model;

public class Voucher {
    public static String TYPE_PAPER = "paper";
    public static String TYPE_PHONE = "phone";

    private String type;
    private String id;

    public Voucher(String id, boolean isPhone) {
        type = isPhone ? TYPE_PHONE : TYPE_PAPER;
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
