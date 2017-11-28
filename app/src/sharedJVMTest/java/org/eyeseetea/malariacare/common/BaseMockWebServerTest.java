package org.eyeseetea.malariacare.common;


import org.junit.After;
import org.junit.Before;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class BaseMockWebServerTest {


    protected MockWebServer server;

    @Before
    public void setUp() throws Exception {
        this.server = new MockWebServer();
        this.server.start();

    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    protected void enqueueResponse(String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        String fileContent = getContentFrom(fileName);
        mockResponse.setBody(fileContent);
        server.enqueue(mockResponse);
    }

    protected void enqueue404ResponseCode() {
        server.enqueue(new MockResponse().setResponseCode(404));
    }

    protected void enqueueMalformedJson() {
        server.enqueue(new MockResponse().setBody("{malformedJson}"));
    }

    private String getContentFrom(String fileName) throws IOException {
        return new FileReader().getStringFromFile(getClass(), fileName);
    }
}
