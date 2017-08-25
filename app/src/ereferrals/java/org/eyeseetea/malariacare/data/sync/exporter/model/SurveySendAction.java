package org.eyeseetea.malariacare.data.sync.exporter.model;

import java.util.ArrayList;
import java.util.List;

public class SurveySendAction {
    private String type;
    private String actionId;
    private List<AttributeValueWS> dataValues;
    private Voucher voucher;

    public SurveySendAction() {
        dataValues = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionId() {
        return actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public List<AttributeValueWS> getDataValues() {
        return dataValues;
    }

    public void setDataValues(
            List<AttributeValueWS> dataValues) {
        this.dataValues = dataValues;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }
}
