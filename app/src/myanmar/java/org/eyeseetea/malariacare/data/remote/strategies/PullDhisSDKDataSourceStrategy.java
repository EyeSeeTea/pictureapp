package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.categoryoptiongroup.CategoryOptionGroupFilters;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.category.CategoryOption;
import org.hisp.dhis.client.sdk.models.category.CategoryOptionGroup;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class PullDhisSDKDataSourceStrategy implements IPullDhisSDKDataSourceStrategy {
    String mCategoryOptionUID = null;

    public void setEventFilters(EventFilters eventFilters) {
        String categoryOptionUID = getCategoryOptionUIDByCurrentUser();

        if (categoryOptionUID != null) {
            eventFilters.setCategoryCombinationAttribute(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.category_combination));
            eventFilters.setCategoryOptionAttribute(categoryOptionUID);
        }
    }

    @Override
    public void onMetadataSucceed(final IDataSourceCallback callback,
            final List<OrganisationUnit> organisationUnits) {
        CategoryOptionGroupFilters categoryOptionGroupFilters = new CategoryOptionGroupFilters();
        categoryOptionGroupFilters.setCategoryOptionUid(getCategoryOptionUIDByCurrentUser());
        categoryOptionGroupFilters.setCategoryOptionGroupSetUid(
                PreferencesState.getInstance().getContext().getString(
                        R.string.category_option_group_set_uid));
        D2.categoryOptionGroups().pull(categoryOptionGroupFilters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<CategoryOptionGroup>>() {
                    @Override
                    public void call(List<CategoryOptionGroup> categoryOptionGroups) {
                        callback.onSuccess(organisationUnits);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }

    private String getCategoryOptionUIDByCurrentUser() {
        if (mCategoryOptionUID == null) {
            mCategoryOptionUID = SdkQueries.getCategoryOptionUIDByCurrentUser();
        }

        return mCategoryOptionUID;
    }
    @Override
    public void pullMetadata(
            final IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy,
            final IDataSourceCallback<List<OrganisationUnit>> callback){
        Observable.zip(D2.me().organisationUnits().pull(SyncStrategy.NO_DELETE),
                D2.categoryOptions().pull(),
                new Func2<List<OrganisationUnit>, List<CategoryOption>,
                                        List<OrganisationUnit>>() {
                    @Override
                    public List<OrganisationUnit> call(List<OrganisationUnit> organisationUnits,
                            List<CategoryOption> categoryOptions) {
                        return organisationUnits;
                    }
                })
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
}
