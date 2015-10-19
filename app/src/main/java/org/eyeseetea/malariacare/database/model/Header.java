package org.eyeseetea.malariacare.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class Header extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_header;

    @Column
    String short_name;

    @Column
    String name;

    @Column
    Integer order_pos;

    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_tab",
            columnType = Long.class,
            foreignColumnName = "id_tab")},
            saveForeignKeyModel = false)
    Tab tab;

    List<Question> questions;

    public Header() {
    }

    public Header(String short_name, String name, Integer order_pos, Integer master, Tab tab) {
        this.short_name = short_name;
        this.name = name;
        this.order_pos = order_pos;
        this.tab = tab;
    }

    public Long getId_header() {
        return id_header;
    }

    public void setId_header(Long id_header) {
        this.id_header = id_header;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrder_pos() {
        return order_pos;
    }

    public void setOrder_pos(Integer order_pos) {
        this.order_pos = order_pos;
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    //TODO: to enable lazy loading, here we need to set Method.SAVE and Method.DELETE and use the .toModel() to specify when do we want to load the models
    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "questions")
    public List<Question> getQuestions(){
        if (this.questions == null){
            this.questions = new Select().from(Question.class)
                    .where(Condition.column(Question$Table.HEADER_ID_HEADER).eq(this.getId_header()))
                    .orderBy(Question$Table.ORDER_POS).queryList();
        }
        return questions;
    }

    /**
     * getNumber Of Question Parents Header
     * @return
     */
    public long getNumberOfQuestionParents() {
        return new Select().count().from(Question.class)
                .where(Condition.column(Question$Table.HEADER_ID_HEADER).eq(getId_header()))
                .and(Condition.column(Question$Table.QUESTION_ID_PARENT).isNull()).count();
    }

    //FIXME We need to add the new release of sugar orm as it adds supports for null
//    public List<Question> getParentQuestions(){
//        if (this._parentQuestions == null){
//            this._parentQuestions = Select.from(Question.class)
//                    .where(Condition.prop("header").eq(String.valueOf(this.getId())),
//                            Condition.prop("question").isNull)
//                    .orderBy("orderpos").list();
//        }
//        return _parentQuestions;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (name != null ? !name.equals(header.name) : header.name != null) return false;
        if (order_pos != null ? !order_pos.equals(header.order_pos) : header.order_pos != null)
            return false;
        if (short_name != null ? !short_name.equals(header.short_name) : header.short_name != null)
            return false;
        if (tab != null ? !tab.equals(header.tab) : header.tab != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = short_name != null ? short_name.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (order_pos != null ? order_pos.hashCode() : 0);
        result = 31 * result + (tab != null ? tab.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Header{" +
                "id='" + id_header + '\'' +
                ", short_name='" + short_name + '\'' +
                ", name='" + name + '\'' +
                ", order_pos=" + order_pos +
                ", tab=" + tab +
                '}';
    }
}
