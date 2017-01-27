package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PullDhisSDKDataSource {

    public void pullMetadata(final IDataSourceCallback<Void> callback) {

        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {

            D2.me().organisationUnits().pull(SyncStrategy.NO_DELETE)
                    .subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<OrganisationUnit>>() {
                        @Override
                        public void call(List<OrganisationUnit> organisationUnits) {
                            callback.onSuccess(null);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    });
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}



