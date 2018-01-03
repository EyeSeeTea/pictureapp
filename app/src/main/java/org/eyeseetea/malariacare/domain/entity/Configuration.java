package org.eyeseetea.malariacare.domain.entity;


import java.util.Date;

public class Configuration {


    public static class CountryVersion {
        private String uid;
        private String country;
        private int version;
        private Date lastUpdate;
        private String reference;

        private CountryVersion(Builder builder) {
            setUid(builder.uid);
            setCountry(builder.country);
            setVersion(builder.version);
            setLastUpdate(builder.lastUpdate);
            setReference(builder.reference);
        }

        public static Builder newBuilder() {
            return new Builder();
        }


        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CountryVersion that = (CountryVersion) o;

            if (version != that.version) return false;
            if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
            if (country != null ? !country.equals(that.country) : that.country != null)
                return false;
            return lastUpdate != null ? lastUpdate.equals(that.lastUpdate)
                    : that.lastUpdate == null;
        }

        @Override
        public int hashCode() {
            int result = uid != null ? uid.hashCode() : 0;
            result = 31 * result + (country != null ? country.hashCode() : 0);
            result = 31 * result + version;
            result = 31 * result + (lastUpdate != null ? lastUpdate.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "CountryVersion{" +
                    "uid='" + uid + '\'' +
                    ", country='" + country + '\'' +
                    ", version=" + version +
                    ", lastUpdate=" + lastUpdate +
                    '}';
        }

        public static final class Builder {
            private String uid;
            private String country;
            private int version;
            private Date lastUpdate;
            private String reference;

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
                reference = reference;
                return this;
            }

            public CountryVersion build() {
                return new CountryVersion(this);
            }
        }
    }

}
