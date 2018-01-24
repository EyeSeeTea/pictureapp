package org.eyeseetea.malariacare.data.sync.importer;


import com.raizlabs.android.dbflow.annotation.NotNull;

public interface IConverterVisitor<T, E> {

    @NotNull
    E visit(@NotNull T entity);

}
