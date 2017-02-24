package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.strategies.IPullDhisSDKDataSourceStrategy;
import org.eyeseetea.malariacare.data.remote.strategies.PullDhisSDKDataSourceStrategy;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.attribute.Attribute;
import org.hisp.dhis.client.sdk.models.category.CategoryOption;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class PullDhisSDKDataSource {
    private IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy =
            new PullDhisSDKDataSourceStrategy();

    public void pullMetadata(final IDataSourceCallback<List<OrganisationUnit>> callback) {

        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {

            Observable.zip(D2.me().organisationUnits().pull(SyncStrategy.NO_DELETE),
                    D2.attributes().pull(), D2.categoryOptions().pull(),
                    new Func3<List<OrganisationUnit>, List<Attribute>, List<CategoryOption>,
                            List<OrganisationUnit>>() {
                        @Override
                        public List<OrganisationUnit> call(List<OrganisationUnit> organisationUnits,
                                List<Attribute> attributes, List<CategoryOption> categoryOptions) {
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

    public void pullData(PullFilters pullFilters, List<OrganisationUnit> organisationUnits,
            IDataSourceCallback<List<Event>> callback) {
        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            try {
                List<Event> events = new ArrayList<>();

                for (OrganisationUnit organisationUnit : organisationUnits) {
                    Scheduler pullEventsThread = Schedulers.newThread();

                    EventFilters eventFilters = new EventFilters();

                    eventFilters.setStartDate(pullFilters.getStartDate());
                    eventFilters.setEndDate(pullFilters.getEndDate());
                    eventFilters.setMaxEvents(pullFilters.getMaxEvents());

                    eventFilters.setOrganisationUnitUId(organisationUnit.getUId());

                    mPullDhisSDKDataSourceStrategy.setEventFilters(eventFilters);

                    List<Event> eventsByOrgUnit = D2.events().pull(eventFilters)
                            .subscribeOn(pullEventsThread)
                            .observeOn(pullEventsThread)
                            .toBlocking()
                            .single();

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



