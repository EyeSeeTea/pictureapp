package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.eyeseetea.malariacare.database.AppDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(databaseName = AppDatabase.NAME)
public class CompositeScore extends SugarRecord<CompositeScore> {

    private static final String LIST_BY_PROGRAM_SQL="select distinct cs.* from composite_score cs left join question q on q.composite_score=cs.id "+
            "left join header h on q.header=h.id "+
            "left join tab t on h.tab=t.id "+
            "left join program p on t.program=p.id where p.id=?";

    @Column
    @PrimaryKey(autoincrement = true)
    long id_composite_score;

    @Column
    String code;

    @Column
    String label;

    @Column
    String uid;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_parent",
            columnType = Long.class,
            foreignColumnName = "id_composite_score")},
            saveForeignKeyModel = false)
    CompositeScore compositeScore;

    @Ignore
    List<CompositeScore> _compositeScoreChildren;

    @Ignore
    List<Question> _questions;

    public CompositeScore() {
    }

    public CompositeScore(String code, String label, CompositeScore compositeScore) {
        this.code = code;
        this.label = label;
        this.compositeScore = compositeScore;
    }

    public CompositeScore(String code, String label, String uid, CompositeScore compositeScore) {
        this.code = code;
        this.label = label;
        this.uid = uid;
        this.compositeScore = compositeScore;
    }


    public long getId_composite_score() {
        return id_composite_score;
    }

    public void setId_composite_score(long id_composite_score) {
        this.id_composite_score = id_composite_score;
    }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public CompositeScore getComposite_score() {
        return compositeScore;
    }

    public void setCompositeScore(CompositeScore compositeScore) {
        this.compositeScore = compositeScore;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean hasParent(){
        return getComposite_score() != null;
    }

    public List<CompositeScore> getCompositeScoreChildren() {
        return null;
        //TODO
//        if (this._compositeScoreChildren == null){
//            this._compositeScoreChildren = new Select()
//                    .from(CompositeScore.class)
//                    .where(Condition.column(CompositeScore$Table.COMPOSITESCORE_ID_PARENT).eq(this.getId_composite_score()))
//                    .orderBy(CompositeScore$Table.ORDER_POS)
//                    .queryList();
//            //this.compositeScoreChildren = CompositeScore.find(CompositeScore.class, "composite_score = ?", String.valueOf(this.getId()));
//        }
//        return this._compositeScoreChildren;
    }


    public List<Question> getQuestions(){
        return null;
        //TODO
//        //if (questions == null) {
//        _questions = new Select()
//                .from(Question.class)
//                .where(Condition.column(Question$Table.COMPOSITESCORE_ID_COMPOSITE_SCORE).eq(this.getId_composite_score()))
//                .orderBy(true, Question$Table.ORDER_POS)
//                .queryList();
//        //}
//        return _questions;
    }

    /**
     * Select all composite score that belongs to a program
     * @param program Program whose composite scores are searched.
     * @return
     */
    public static List<CompositeScore> listAllByProgram(Program program){
        if(program==null || program.getId_program()==null){
            return new ArrayList<>();
        }
        return null;
        //TODO
    }

    public static List<CompositeScore> listParentCompositeScores(CompositeScore compositeScore){
        ArrayList<CompositeScore> parentScores= new ArrayList<CompositeScore>();
        if(compositeScore==null || !compositeScore.hasParent()){
            return parentScores;
        }
        CompositeScore currentScore=compositeScore;
        while(currentScore!=null && currentScore.hasParent()){
            currentScore=currentScore.getComposite_score();
            parentScores.add(currentScore);
        }
        return parentScores;
    }

    public boolean hasChildren(){
        return !getCompositeScoreChildren().isEmpty();
    }

    @Override
    public String toString() {
        return "CompositeScore{" +
                "id_composite_score=" + id_composite_score +
                ", code='" + code + '\'' +
                ", label='" + label + '\'' +
                ", uid='" + uid + '\'' +
                ", compositeScore=" + compositeScore +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeScore that = (CompositeScore) o;

        if (id_composite_score != that.id_composite_score) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        return !(compositeScore != null ? !compositeScore.equals(that.compositeScore) : that.compositeScore != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_composite_score ^ (id_composite_score >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (compositeScore != null ? compositeScore.hashCode() : 0);
        return result;
    }
}
