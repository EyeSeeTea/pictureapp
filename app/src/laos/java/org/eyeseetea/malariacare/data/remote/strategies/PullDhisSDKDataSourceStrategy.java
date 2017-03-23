package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public class PullDhisSDKDataSourceStrategy implements IPullDhisSDKDataSourceStrategy {
    public void setEventFilters(EventFilters eventFilters) {

    }

    @Override
    public void pullMetadata(
            final IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy,
            final IDataSourceCallback<List<OrganisationUnit>> callback) {
        D2.me().organisationUnits().pull(SyncStrategy.NO_DELETE)
                .subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<OrganisationUnit>>() {
                    @Override
                    public void call(List<OrganisationUnit> organisationUnits) {
                        mPullDhisSDKDataSourceStrategy.onMetadataSucceed(callback,
                                organisationUnits);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }

    @Override
    public void onMetadataSucceed(IDataSourceCallback callback,
            List<OrganisationUnit> organisationUnits) {
        callback.onSuccess(organisationUnits);
    }
}
