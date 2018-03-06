package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.util.Date;

public class Configuration {


    public static class CountryVersion {
        private String uid;
        private String country;
        private int version;
        private Date lastUpdate;
        private String reference;
        private String name;

        public CountryVersion(String uid, String country, int version, Date lastUpdate,
                String reference, String name) {

            this.uid = (required(uid, "UID is required"));
            this.country = (required(country, "country is required"));
            this.version = (required(version, "version is required"));
            this.lastUpdate = (required(lastUpdate, "version is required"));
            this.reference = (required(reference, "reference is required"));
            this.name = name;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getUid() {
            return uid;
        }


        public String getCountry() {
            return country;
        }

        public int getVersion() {
            return version;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public String getReference() {
            return reference;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "CountryVersion{" +
                    "uid='" + uid + '\'' +
                    ", country='" + country + '\'' +
                    ", version=" + version +
                    ", lastUpdate=" + lastUpdate +
                    ", reference='" + reference + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }


        public static final class Builder {
            private String uid;
            private String country;
            private int version;
            private Date lastUpdate;
            private String reference;
            private String name;

            private Builder() {
            }

            public Builder uid(String val) {
                uid = val;
                return this;
            }

            public Builder country(String val) {
                country = val;
                return this;
            }

            public Builder version(int val) {
                version = val;
                return this;
            }

            public Builder lastUpdate(Date val) {
                lastUpdate = val;
                return this;
            }

            public Builder reference(String val) {
                reference = val;
                return this;
            }

            public Builder name(String val) {
                name = val;
                return this;
            }

            public CountryVersion build() {
                return new CountryVersion(
                        this.uid,
                        this.country,
                        this.version,
                        this.lastUpdate,
                        this.reference,
                        this.name
                );
            }
        }
    }

}
