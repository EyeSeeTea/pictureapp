package org.eyeseetea.malariacare.data.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.eyeseetea.malariacare.domain.entity.intent.ConnectVoucher;

import java.io.IOException;

public class ConnectVoucherMapper {

    public static ConnectVoucher parseJson(String jsonToSend) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        ConnectVoucher connectVoucher = mapper.readValue(jsonToSend, ConnectVoucher.class);
        return connectVoucher;
    }
}
