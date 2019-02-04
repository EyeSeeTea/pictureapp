package org.eyeseetea.malariacare.data.sync.exporter.model;

public class ApiAvailable {
    private boolean available;
    private  String msg;

    public ApiAvailable() {
    }

    public ApiAvailable(boolean available, String msg) {
        this.available = available;
        this.msg = msg;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
