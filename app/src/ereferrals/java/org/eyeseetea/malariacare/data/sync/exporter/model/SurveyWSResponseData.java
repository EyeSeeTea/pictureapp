package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SurveyWSResponseData {
    private String voucherEventId;
    private String clientId;
    @JsonProperty("CUIC")
    private String CUIC;
    private String voucherId;
    private String clientEventId;
    private String voucherCode;

    public SurveyWSResponseData() {
    }

    public SurveyWSResponseData(String voucherEventId, String clientId, String CUIC,
            String voucherId, String clientEventId, String voucherCode) {
        this.voucherEventId = voucherEventId;
        this.clientId = clientId;
        this.CUIC = CUIC;
        this.voucherId = voucherId;
        this.clientEventId = clientEventId;
        this.voucherCode = voucherCode;
    }

    public String getVoucherEventId() {
        return voucherEventId;
    }

    public void setVoucherEventId(String voucherEventId) {
        this.voucherEventId = voucherEventId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClinetId(String clientId) {
        this.clientId = clientId;
    }

    public String getCUIC() {
        return CUIC;
    }

    public void setCUIC(String CUIC) {
        this.CUIC = CUIC;
    }

    public String getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(String voucherId) {
        this.voucherId = voucherId;
    }

    public String getClientEventId() {
        return clientEventId;
    }

    public void setClientEventId(String clientEventId) {
        this.clientEventId = clientEventId;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}
