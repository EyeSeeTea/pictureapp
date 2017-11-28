package org.eyeseetea.malariacare.domain.boundary.converters;


import com.raizlabs.android.dbflow.annotation.NotNull;

public interface IConverter<T, E> {

    @NotNull
    E convert(@NotNull T domainModel);
}
