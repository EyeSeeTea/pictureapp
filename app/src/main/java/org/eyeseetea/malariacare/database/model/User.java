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

package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class User extends BaseModel {

    private static final String DUMMY_USER = "user";

    @Column
    @PrimaryKey(autoincrement = true)
    long id_user;
    @Column
    String uid;
    @Column
    String name;
    @Column
    long organisation;
    @Column
    long supervisor;

    /**
     * List of surveys of this user
     */
    List<Survey> surveys;

    public User() {
    }

    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public User(long id_user, String uid, String name, long organisation, long supervisor,
            List<Survey> surveys) {
        this.id_user = id_user;
        this.uid = uid;
        this.name = name;
        this.organisation = organisation;
        this.supervisor = supervisor;
        this.surveys = surveys;
    }

    public static User getLoggedUser() {
        // for the moment we return just the first entry assuming there will be only one entry,
        // but in the future we will have to tag the logged user
        List<User> users = new Select().all().from(User.class).queryList();
        if (users != null && users.size() != 0) {
            return users.get(0);
        }
        return null;
    }

    public static void insertLoggedUser(User user) {
        User userDB = User.getUserFromDB(user);

        if (userDB == null) {
            user.save();
        }
    }

    public static User getUserFromDB(User user) {
        List<User> userdb = new Select().from(User.class).queryList();
        for (int i = userdb.size() - 1; i >= 0; i--) {
            if ((userdb.get(i).getUid().equals(user.getUid())) && (userdb.get(i).getName().equals(
                    user.getName()))) {
                return userdb.get(i);
            }
        }
        return null;
    }

    public static User createDummyUser() {
        User dummyUser = new User(DUMMY_USER, DUMMY_USER);

        User userdb = User.getUserFromDB(dummyUser);

        if (userdb != null) {
            dummyUser = userdb;
        } else {
            dummyUser.save();
        }

        return dummyUser;
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Survey> getSurveys() {
        if (surveys == null) {
            surveys = new Select()
                    .from(Survey.class)
                    .where(Condition.column(Survey$Table.ID_USER)
                            .eq(this.getId_user())).queryList();
        }
        return surveys;
    }

    public long getOrganisation() {
        return organisation;
    }

    public void setOrganisation(long organisation) {
        this.organisation = organisation;
    }

    public long getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(long supervisor) {
        this.supervisor = supervisor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id_user != user.id_user) return false;
        if (organisation != user.organisation) return false;
        if (supervisor != user.supervisor) return false;
        if (uid != null ? !uid.equals(user.uid) : user.uid != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        return surveys != null ? surveys.equals(user.surveys) : user.surveys == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_user ^ (id_user >>> 32));
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (int) (organisation ^ (organisation >>> 32));
        result = 31 * result + (int) (supervisor ^ (supervisor >>> 32));
        result = 31 * result + (surveys != null ? surveys.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "User{" +
                "id_user=" + id_user +
                ", uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", organisation=" + organisation +
                ", supervisor=" + supervisor +
                ", surveys=" + surveys +
                '}';
    }
}
