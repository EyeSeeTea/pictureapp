package org.eyeseetea.malariacare.data.sync.exporter.model;

import java.util.ArrayList;
import java.util.List;

public class SurveySendAction {
    private String type;
    private String actionId;
    private List<AttributeValueWS> attributeValues;

    public SurveySendAction() {
        attributeValues = new ArrayList<>();
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

    public List<AttributeValueWS> getAttributeValues() {
        return attributeValues;
    }

    public void setAttributeValues(
            List<AttributeValueWS> attributeValues) {
        this.attributeValues = attributeValues;
    }
}
