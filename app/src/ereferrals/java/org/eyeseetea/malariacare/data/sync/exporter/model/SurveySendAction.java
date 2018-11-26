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
    private String sourceAddedDateTime;
    private Coordinate coordinate;
    private float accuracy;

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

    public String getSourceAddedDateTime() {
        return sourceAddedDateTime;
    }

    public void setSourceAddedDateTime(String sourceAddedDateTime) {
        this.sourceAddedDateTime = sourceAddedDateTime;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }
}
