package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SurveySendAction {
    private String type;
    private String actionId;
    private List<AttributeValueWS> dataValues;
    private Voucher voucher;
    private String program;
    @JsonProperty("EventDateTime")
    private String eventDateTime;

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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }
}
