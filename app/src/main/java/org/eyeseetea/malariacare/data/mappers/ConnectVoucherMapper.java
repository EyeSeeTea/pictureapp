package org.eyeseetea.malariacare.data.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.data.intent.ConnectVoucher;

import java.io.IOException;

public class ConnectVoucherMapper {

    public static ConnectVoucher parseJson(String jsonToSend) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ConnectVoucher connectVoucher = mapper.readValue(jsonToSend, ConnectVoucher.class);
            return connectVoucher;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
