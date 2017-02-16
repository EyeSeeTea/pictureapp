package org.eyeseetea.malariacare.data.remote;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.attribute.Attribute;
import org.hisp.dhis.client.sdk.models.attribute.AttributeValue;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class PullDhisSDKDataSource {

    public static final String USER_CATEGORY_COMBINATION_CATEGORY_OPTION_ATT_CODE =
            "USER_CC_CO_VOL";



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

    public void pullData(PullFilters pullFilters, List<OrganisationUnit> organisationUnits,
            IDataSourceCallback<List<Event>> callback) {
        boolean isNetworkAvailable = isNetworkAvailable();

        if (!isNetworkAvailable) {
            callback.onError(new NetworkException());
        } else {
            try {

                AttributeValue compositeUserAttributeValue = getCompositeUserAttributeValue();

                List<Event> events = new ArrayList<>();

                for (OrganisationUnit organisationUnit : organisationUnits) {
                    Scheduler pullEventsThread = Schedulers.newThread();


                    EventFilters eventFilters = new EventFilters();

                    eventFilters.setStartDate(pullFilters.getStartDate());
                    eventFilters.setEndDate(pullFilters.getEndDate());
                    eventFilters.setMaxEvents(pullFilters.getMaxEvents());

                    eventFilters.setOrganisationUnitUId(organisationUnit.getUId());

                    if (compositeUserAttributeValue != null){
                        String[] userAttributes = compositeUserAttributeValue.getValue().split(";");

                        eventFilters.setCategoryCombinationAttribute(userAttributes[0]);
                        eventFilters.setCategoryOptionAttribute(userAttributes[1]);
                    }

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

    private AttributeValue getCompositeUserAttributeValue() {
        Attribute compositeUserAttribute = null;
        AttributeValue compositeUserAttributeValue = null;

        List<Attribute> attributes = D2.attributes().list().toBlocking().single();

        if (attributes == null || attributes.size() == 0) {
            return null;
        }

        for (Attribute attribute : attributes) {
            if (attribute.getCode().equals(USER_CATEGORY_COMBINATION_CATEGORY_OPTION_ATT_CODE)) {
                compositeUserAttribute = attribute;
            }
        }

        if (compositeUserAttribute == null) {
            return null;
        }

        List<AttributeValue> userAttributeValues =
                D2.me().account().get().toBlocking().single().getAttributeValues();

        if (userAttributeValues == null || userAttributeValues.size() == 0) {
            return null;
        }

        for (AttributeValue attributeValue : userAttributeValues) {
            if (attributeValue.getAttributeUId().equals(compositeUserAttribute.getUId())) {
                compositeUserAttributeValue = attributeValue;
            }
        }

        return compositeUserAttributeValue;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}



