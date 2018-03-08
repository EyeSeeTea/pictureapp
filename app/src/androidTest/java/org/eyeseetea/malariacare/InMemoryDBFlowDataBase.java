package org.eyeseetea.malariacare;

import android.support.test.InstrumentationRegistry;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.junit.rules.ExternalResource;

/*This test Rules creates a new clean instance of the DBFlow database per each @Test method
  that you have, if you change anything in database(insert,delete and update),
  it will be clean up when the next @Test methods runs.
  For instance, if you have the following:

     @Test
     public void test1(){
        QuestionDB question1 = new QuestionDB();
        question1.save(); //After this line id_question
                          // It's going to be equals to 1
     }

     @Test
     public void test2(){
        QuestionDB question2 = new QuestionDB();
        question2.save(); //After this line id_question
                          // it's still equals to 1
                          // The InMemoryDBFlowDataBase cleans up for you.
     }
  */
public class InMemoryDBFlowDataBase extends ExternalResource {

    @Override
    protected void after() {

        FlowManager.reset();

        initDBFlowDB();
    }

    private void initDBFlowDB() {
        DatabaseHolderProviderStrategy databaseStrategy = new DatabaseHolderProviderStrategy();
        FlowConfig flowConfig = new FlowConfig
                .Builder(InstrumentationRegistry.getTargetContext())
                .addDatabaseHolder(databaseStrategy.provide())
                .build();
        FlowManager.init(flowConfig);
    }
}
