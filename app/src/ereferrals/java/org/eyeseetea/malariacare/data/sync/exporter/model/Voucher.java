package org.eyeseetea.malariacare.data.sync.exporter.model;

public class Voucher {
    public static String TYPE_PAPER = "paper";
    public static String TYPE_PHONE = "electronic";
    public static String TYPE_NO_VOUCHER = "novoucher";

    private String type;
    private String id;

    public Voucher(String id, String type) {
        this.type = type;
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
