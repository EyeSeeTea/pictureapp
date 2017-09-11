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
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class PullDhisSDKDataSource {
    private IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy =
            new PullDhisSDKDataSourceStrategy();

    public void pullMetadata(final IDataSourceCallback<List<OrganisationUnit>> callback) {

        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            new PullDhisSDKDataSourceStrategy().pullMetadata(mPullDhisSDKDataSourceStrategy, callback);
        }
    }

    public void pullData(PullFilters pullFilters, List<OrganisationUnit> organisationUnits,
            IDataSourceCallback<List<Event>> callback) {
        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            List<Event> events = new ArrayList<>();

            for (OrganisationUnit organisationUnit : organisationUnits) {
                    if(pullFilters.getDataByOrgUnit() != null && !pullFilters.getDataByOrgUnit().equals("")){
                        if(!pullFilters.getDataByOrgUnit().equals(organisationUnit.getName())){
                            continue;
                        }
                    }
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



