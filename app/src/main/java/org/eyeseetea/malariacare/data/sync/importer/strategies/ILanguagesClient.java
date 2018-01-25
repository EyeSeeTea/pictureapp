package org.eyeseetea.malariacare.data.sync.importer.strategies;


import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Language;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.Term;

import java.util.List;

public interface ILanguagesClient {
    List<Language> getLanguages() throws Exception;

    List<Term> getTranslationBy(String language) throws Exception;
}
