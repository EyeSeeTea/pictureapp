package org.eyeseetea.malariacare.domain.exception;

public class NullImportSummary extends Exception {
        public NullImportSummary() {
            super("The import summary is null (F.E conflict)");
        }
}
