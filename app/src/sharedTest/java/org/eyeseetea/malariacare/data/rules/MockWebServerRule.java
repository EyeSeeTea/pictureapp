package org.eyeseetea.malariacare.data.rules;

import static okhttp3.internal.Util.UTC;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.HeaderUtils;
import org.eyeseetea.malariacare.data.file.IFileReader;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class MockWebServerRule implements TestRule {

    private static final int OK_CODE = 200;

    private final IFileReader fileReader;
    private final MockWebServer server;

    public MockWebServerRule(IFileReader fileReader) {
        this.fileReader = fileReader;
        this.server = new MockWebServer();
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                before();

                base.evaluate();

                after();
            }
        };
    }

    private void before() throws IOException {
        server.start();
    }

    private void after() throws IOException {
        server.shutdown();
    }

    public void enqueueMockResponse() throws IOException {
        enqueueMockResponse(OK_CODE);
    }

    public void enqueueMockResponse(int code) throws IOException {
        enqueueMockResponse(code, "{}");
    }

    public void enqueueMockResponse(int code, String response) throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        mockResponse.setBody(response);
        server.enqueue(mockResponse);
    }

    public void enqueueMockResponseFileName(int code, String fileName) throws IOException {
        MockResponse mockResponse = new MockResponse();
        mockResponse.setResponseCode(code);
        mockResponse.setBody(fileReader.getStringFromFile(fileName));
        server.enqueue(mockResponse);
    }

    public void enqueueMockResponse(String fileName) throws IOException {
        MockResponse response = createMockResponse(fileName);
        server.enqueue(response);
    }

    @NonNull
    private MockResponse createMockResponse(String fileName) throws IOException {
        String body = fileReader.getStringFromFile(fileName);
        MockResponse response = new MockResponse();
        response.setResponseCode(OK_CODE);
        response.setBody(body);
        return response;
    }

    public void enqueueMockResponse(String fileName, Date dateHeader)
            throws IOException {
        MockResponse response = createMockResponse(fileName);

        DateFormat rfc1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        rfc1123.setLenient(false);
        rfc1123.setTimeZone(UTC);
        String dateHeaderValue = rfc1123.format(dateHeader);

        response.setHeader(HeaderUtils.DATE, dateHeaderValue);

        server.enqueue(response);
    }

    public String getBaseEndpoint() {
        return server.url("/").toString();
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }
}
