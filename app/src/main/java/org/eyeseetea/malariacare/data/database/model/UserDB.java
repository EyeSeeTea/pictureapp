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

package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.Date;
import java.util.List;

@Table(database = AppDatabase.class, name = "User")
public class UserDB extends BaseModel {

    private static final String DUMMY_USER = "mUserDB";
    public static final String ATTRIBUTE_USER_CLOSE_DATE = "USER_CLOSE_DATE";
    public static final String ATTRIBUTE_USER_ANNOUNCEMENT = "USER_ANNOUNCEMENT";

    @Column
    @PrimaryKey(autoincrement = true)
    long id_user;
    @Column
    String uid_user;
    @Column
    String name;
    @Column
    long partner_fk;
    @Column
    long supervisor_fk;
    @Column
    String announcement;
    @Column
    Date close_date;
    @Column
    Date last_updated;
    @Column
    boolean canAddSurveys;


    /**
     * List of surveys of this mUserDB
     */
    List<SurveyDB> mSurveyDBs;

    public UserDB() {
    }

    public UserDB(String uid, String name) {
        this.uid_user = uid;
        this.name = name;
    }

    public UserDB(long id_user, String uid, String name, long organisation, long supervisor,
            List<SurveyDB> surveyDBs) {
        this.id_user = id_user;
        this.uid_user = uid;
        this.name = name;
        this.partner_fk = organisation;
        this.supervisor_fk = supervisor;
        this.mSurveyDBs = surveyDBs;
    }

    public static UserDB getLoggedUser() {
        // for the moment we return just the first entry assuming there will be only one entry,
        // but in the future we will have to tag the logged mUserDB
        List<UserDB> userDBs = new Select().from(UserDB.class).queryList();
        if (userDBs != null && userDBs.size() != 0) {
            return userDBs.get(0);
        }
        return null;
    }

    public static void insertLoggedUser(UserDB user) {
        UserDB userDBDB = UserDB.getUserFromDB(user);

        if (userDBDB == null) {
            user.save();
        } else {
            userDBDB.setCanAddSurveys(user.canAddSurveys());
            userDBDB.save();
            System.out.println("UserDB already saved" + user.toString());
        }
    }

    public static UserDB getUserFromDB(UserDB userDB) {
        if (userDB.getUid() == null) {
            return  null;
        }
        List<UserDB> userdb = new Select().from(UserDB.class).queryList();
        for (int i = userdb.size() - 1; i >= 0; i--) {
            if (userdb.get(i).getUid() != null && userdb.get(i).getUid().equals(userDB.getUid())
                    && userdb.get(i).getName().equals(
                    userDB.getName())) {
                return userdb.get(i);
            }
        }
        return null;
    }

    public static UserDB createDummyUser() {
        UserDB dummyUserDB = new UserDB(DUMMY_USER, DUMMY_USER);

        UserDB userdb = UserDB.getUserFromDB(dummyUserDB);

        if (userdb != null) {
            dummyUserDB = userdb;
        } else {
            dummyUserDB.save();
        }

        return dummyUserDB;
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public String getUid() {
        return uid_user;
    }

    public void setUid(String uid) {
        this.uid_user = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    public Date getCloseDate() {
        return close_date;
    }

    public void setCloseDate(Date close_date) {
        this.close_date = close_date;
    }

    public Date getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public boolean canAddSurveys() {
        return canAddSurveys;
    }

    public void setCanAddSurveys(boolean canAddSurveys) {
        this.canAddSurveys = canAddSurveys;
    }

    public List<SurveyDB> getSurveyDBs() {
        if (mSurveyDBs == null) {
            mSurveyDBs = new Select()
                    .from(SurveyDB.class)
                    .where(SurveyDB_Table.id_user_fk
                            .eq(this.getId_user())).queryList();
        }
        return mSurveyDBs;
    }

    public Long getOrganisationId() {
        return partner_fk;
    }

    public long getOrganisation() {
        if (partner_fk == 0) {
            UserDB userDB = new Select()
                    .from(UserDB.class)
                    .where(UserDB_Table.name.is(name))
                    .querySingle();
            partner_fk = userDB.getOrganisation();
        }

        return partner_fk;
    }

    public void setOrganisation(long organisation) {
        this.partner_fk = organisation;
    }

    public long getSupervisor() {
        return supervisor_fk;
    }

    public void setSupervisor(long supervisor) {
        this.supervisor_fk = supervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDB userDB = (UserDB) o;

        if (id_user != userDB.id_user) return false;
        if (partner_fk != userDB.partner_fk) return false;
        if (supervisor_fk != userDB.supervisor_fk) return false;
        if (uid_user != null ? !uid_user.equals(userDB.uid_user) : userDB.uid_user != null) {
            return false;
        }
        if (name != null ? !name.equals(userDB.name) : userDB.name != null) return false;
        if (announcement != null ? !announcement.equals(userDB.announcement)
                : userDB.announcement != null) {
            return false;
        }
        if (close_date != null ? !close_date.equals(userDB.close_date)
                : userDB.close_date != null) {
            return false;
        }
        return last_updated != null ? last_updated.equals(userDB.last_updated)
                : userDB.last_updated == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_user ^ (id_user >>> 32));
        result = 31 * result + (uid_user != null ? uid_user.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (partner_fk ^ (partner_fk >>> 32));
        result = 31 * result + (int) (supervisor_fk ^ (supervisor_fk >>> 32));
        result = 31 * result + (announcement != null ? announcement.hashCode() : 0);
        result = 31 * result + (close_date != null ? close_date.hashCode() : 0);
        result = 31 * result + (last_updated != null ? last_updated.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDB{" +
                "id_user_fk=" + id_user +
                ", uid='" + uid_user + '\'' +
                ", name='" + name + '\'' +
                ", mPartnerDB=" + partner_fk +
                ", supervisor=" + supervisor_fk +
                ", surveys=" + mSurveyDBs +
                ", announcement=" + announcement +
                ", close_date=" + close_date +
                ", last_updated=" + last_updated +
                '}';
    }

    public UserDB(long id_user, String uid_user, String name, long organisation_fk,
            long supervisor_fk,
            String announcement, Date close_date, Date last_updated,
            List<SurveyDB> surveyDBs) {
        this.id_user = id_user;
        this.uid_user = uid_user;
        this.name = name;
        this.partner_fk = organisation_fk;
        this.supervisor_fk = supervisor_fk;
        this.announcement = announcement;
        this.close_date = close_date;
        this.last_updated = last_updated;
        this.mSurveyDBs = surveyDBs;
    }
}
