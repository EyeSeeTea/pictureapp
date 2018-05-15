package org.eyeseetea.malariacare.domain.boundary;

public interface IStylePreferencesRepository {

    enum ListStyle { GRID, LIST }
    
    ListStyle getListStyle();

    void saveListStyle(ListStyle listStyle);
}
