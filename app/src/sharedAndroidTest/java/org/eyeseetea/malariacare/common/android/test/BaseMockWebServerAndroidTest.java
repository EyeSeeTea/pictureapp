package org.eyeseetea.malariacare.common.android.test;


import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.eyeseetea.malariacare.common.BaseMockWebServerTest;
import org.junit.Before;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.mockwebserver.MockResponse;

public class BaseMockWebServerAndroidTest extends BaseMockWebServerTest {

    @SuppressWarnings("WeakerAccess")
    protected Context context;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context = InstrumentationRegistry.getContext();
    }


    public static String readFileContentFromAssets(Context context, String filename)
            throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(filename)));

        StringBuilder sb = new StringBuilder();
        String mLine = reader.readLine();
        while (mLine != null) {
            sb.append(mLine);
            mLine = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    @Override
    protected void enqueueResponse(String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        String fileContent = readFileContentFromAssets(context, fileName);
        mockResponse.setBody(fileContent);
        server.enqueue(mockResponse);
    }
}
