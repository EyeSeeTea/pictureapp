package org.eyeseetea.malariacare.data.sync.importer.models;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.FailedItemFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.FailedItemFlow_Table;

/**
 * Created by idelcano on 29/12/16.
 */

public class FailedItemExtended {

    private final static String TAG = ".DataValueExtended";
    private final static String REGEXP_FACTOR = ".*\\[([0-9]*)\\]";

    FailedItemFlow mFailedItemFlow;

    public FailedItemExtended() {
        mFailedItemFlow = new FailedItemFlow();
    }

    public FailedItemExtended(FailedItemFlow failedItemFlow) {
        this.mFailedItemFlow = failedItemFlow;
    }

    /**
     * Checks whether the given event contains errors in SDK FailedItem table or has been
     * successful.
     * If not return null, it is becouse this item had a conflict.
     */
    public static FailedItemFlow hasConflict(long localId) {
        return new Select()
                .from(FailedItemFlow.class)
                .where(FailedItemFlow_Table.itemId
                        .is(localId)).querySingle();
    }

    public String getErrorMessage() {
        return mFailedItemFlow.getErrorMessage();
    }
}
