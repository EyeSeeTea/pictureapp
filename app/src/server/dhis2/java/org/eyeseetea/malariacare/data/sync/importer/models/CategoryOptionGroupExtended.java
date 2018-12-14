package org.eyeseetea.malariacare.data.sync.importer.models;

import org.eyeseetea.malariacare.data.sync.importer.IConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.VisitableFromSDK;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.CategoryOptionGroupFlow;

public class CategoryOptionGroupExtended implements VisitableFromSDK {
    CategoryOptionGroupFlow mCategoryOptionGroupFlow;

    public CategoryOptionGroupExtended() {
    }

    public CategoryOptionGroupExtended(
            CategoryOptionGroupFlow categoryOptionGroupFlow) {
        mCategoryOptionGroupFlow = categoryOptionGroupFlow;
    }

    @Override
    public void accept(IConvertFromSDKVisitor visitor) {
        visitor.visit(this);
    }

    public String getUid() {
        return mCategoryOptionGroupFlow.getUId();
    }

    public String getName() {
        return mCategoryOptionGroupFlow.getCode();
    }
}
