package org.eyeseetea.malariacare;

        import android.content.Context;
        import android.support.test.InstrumentationRegistry;

        import org.eyeseetea.malariacare.data.file.IFileReader;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.nio.charset.StandardCharsets;

public final class AssetsFileReader implements IFileReader {


    @Override
    public String getStringFromFile(String filename) throws IOException {
        Context testContext = InstrumentationRegistry.getInstrumentation().getContext();
        InputStream inputStream = testContext.getAssets().open(filename);

        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}