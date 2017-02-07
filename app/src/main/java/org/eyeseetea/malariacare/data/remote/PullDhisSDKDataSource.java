package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.attribute.Attribute;
import org.hisp.dhis.client.sdk.models.attribute.AttributeValue;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class PullDhisSDKDataSource {

    public void pullMetadata(final IDataSourceCallback<List<OrganisationUnit>> callback) {

        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {

            Observable.zip(D2.me().organisationUnits().pull(SyncStrategy.NO_DELETE),
                    D2.attributes().pull(),
                    new Func2<List<OrganisationUnit>, List<Attribute>, List<OrganisationUnit>>() {
                        @Override
                        public List<OrganisationUnit> call(List<OrganisationUnit> organisationUnits,
                                List<Attribute> attributes) {
                            return organisationUnits;
                        }
                    })
                    .subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<OrganisationUnit>>() {
                        @Override
                        public void call(List<OrganisationUnit> organisationUnits) {
                            callback.onSuccess(organisationUnits);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    });

        }
    }

    public void pullData(List<OrganisationUnit> organisationUnits,
            IDataSourceCallback<List<Event>> callback) {
        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            try {

                //GwFkNOXaQcq;nTrd5CQKjxd
                //https://data.scpr-mm-mal.org/api/events
                // .json?orgUnit=rV8AX9l6RUs&attributeCc=GwFkNOXaQcq&attributeCos=nTrd5CQKjxd

                List<Attribute> attributes = D2.attributes().list().toBlocking().single();
                List<AttributeValue> userAttributeValues =
                        D2.me().account().get().toBlocking().single().getAttributeValues();

                List<Event> events = new ArrayList<>();

                for (OrganisationUnit organisationUnit : organisationUnits) {
                    Scheduler pullEventsThread = Schedulers.newThread();
                    List<Event> eventsByOrgUnit = D2.events().pull(
                            organisationUnit.getUId(), "").subscribeOn(pullEventsThread)
                            .observeOn(pullEventsThread).toBlocking().single();

                    events.addAll(eventsByOrgUnit);
                }

                callback.onSuccess(events);
            } catch (Exception e) {
                callback.onError(e);
            }
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



