package org.eyeseetea.malariacare.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.core.common.network.ApiMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class FileReader {
    private File getFile(String filename) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
        return new File(resource.getPath());
    }

    public String getStringFromFile(String filename) throws IOException {
        FileInputStream inputStream = new FileInputStream(getFile(filename));
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static Object getApiMessageFromJson(String jsonFile) throws IOException {
        String json = new FileReader().getStringFromFile(
                jsonFile);
        ObjectMapper mapper = new ObjectMapper();
        try {
            return (mapper.readValue(json, ApiMessage.class)).getResponse().getImportSummaries().get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
