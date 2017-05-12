package org.eyeseetea.malariacare.domain.boundary.repositories;

public enum ReadPolicy {
    CACHE,
    REMOTE,
    READ_ALL;

    public boolean useCache() {
        return this == CACHE || this == READ_ALL;
    }

    public boolean useRemote() {
        return this == REMOTE || this == READ_ALL;
    }
}