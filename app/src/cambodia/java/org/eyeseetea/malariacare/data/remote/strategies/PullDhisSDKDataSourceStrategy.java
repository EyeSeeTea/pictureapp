package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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
