/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.sync.importer.models;

import org.eyeseetea.malariacare.data.sync.importer.IConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.VisitableFromSDK;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.UserAccountFlow;

/**
 * Created by arrizabalaga on 6/11/15.
 */
public class UserAccountExtended implements VisitableFromSDK {
    UserAccountFlow userAccount;

    public UserAccountExtended() {
    }

    public UserAccountExtended(UserAccountFlow userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public void accept(IConvertFromSDKVisitor visitor) {
        visitor.visit(this);
    }

    public UserAccountFlow getUserAccount() {
        return userAccount;
    }

    public String getUid() {
        return userAccount.getUId();
    }

    public String getName() {
        return userAccount.getName();
    }
}
