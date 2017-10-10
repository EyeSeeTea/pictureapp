package org.eyeseetea.malariacare.data.sync.importer.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.eyeseetea.malariacare.data.sync.importer.IConvertFromApiVisitor;
import org.eyeseetea.malariacare.data.sync.importer.IVisitableFromApi;

public class OrgUnitTree implements IVisitableFromApi {
    @JsonProperty("Code_Prov_T")
    private String Code_Prov_T;
    @JsonProperty("Name_Prov_E")
    private String Name_Prov_E;
    @JsonProperty("Code_Dist_T")
    private long Code_Dist_T;
    @JsonProperty("Name_Dist_E")
    private String Name_Dist_E;
    @JsonProperty("Code_Comm_T")
    private long Code_Comm_T;
    @JsonProperty("Name_Comm_E")
    private String Name_Comm_E;
    @JsonProperty("Code_Vill_T")
    private long Code_Vill_T;
    @JsonProperty("Name_Vill_E")
    private String Name_Vill_E;
    @JsonProperty("Lat")
    private double Lat;
    @JsonProperty("long")
    private double lng;


    public OrgUnitTree() {
    }

    public OrgUnitTree(String code_Prov_T, String name_Prov_E, int code_Dist_T,
            String name_Dist_E, int code_Comm_T, String name_Comm_E, int code_Vill_T,
            String name_Vill_E, double lat, double lng) {
        Code_Prov_T = code_Prov_T;
        Name_Prov_E = name_Prov_E;
        Code_Dist_T = code_Dist_T;
        Name_Dist_E = name_Dist_E;
        Code_Comm_T = code_Comm_T;
        Name_Comm_E = name_Comm_E;
        Code_Vill_T = code_Vill_T;
        Name_Vill_E = name_Vill_E;
        Lat = lat;
        this.lng = lng;
    }

    public String getCode_Prov_T() {
        return Code_Prov_T;
    }

    public void setCode_Prov_T(String code_Prov_T) {
        Code_Prov_T = code_Prov_T;
    }

    public String getName_Prov_E() {
        return Name_Prov_E;
    }

    public void setName_Prov_E(String name_Prov_E) {
        Name_Prov_E = name_Prov_E;
    }

    public long getCode_Dist_T() {
        return Code_Dist_T;
    }

    public void setCode_Dist_T(long code_Dist_T) {
        Code_Dist_T = code_Dist_T;
    }

    public String getName_Dist_E() {
        return Name_Dist_E;
    }

    public void setName_Dist_E(String name_Dist_E) {
        Name_Dist_E = name_Dist_E;
    }

    public long getCode_Comm_T() {
        return Code_Comm_T;
    }

    public void setCode_Comm_T(long code_Comm_T) {
        Code_Comm_T = code_Comm_T;
    }

    public String getName_Comm_E() {
        return Name_Comm_E;
    }

    public void setName_Comm_E(String name_Comm_E) {
        Name_Comm_E = name_Comm_E;
    }

    public long getCode_Vill_T() {
        return Code_Vill_T;
    }

    public void setCode_Vill_T(long code_Vill_T) {
        Code_Vill_T = code_Vill_T;
    }

    public String getName_Vill_E() {
        return Name_Vill_E;
    }

    public void setName_Vill_E(String name_Vill_E) {
        Name_Vill_E = name_Vill_E;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public void accept(IConvertFromApiVisitor iConvertFromApiVisitor) {
        iConvertFromApiVisitor.visit(this);
    }
}
