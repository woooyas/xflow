package com.nhnacademy.aiot.node;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

class HttpNodeTest {

    private static final int CURRENT_TIME = (int) (System.currentTimeMillis() / 1000 - 30);
    private static final String PATH = "/ep/temperature/24e124126c457594?count=1&st=1696772438&et=";
    private static final int PORT = 1880;
    private static final String HOSTNAME = "ems.nhnacademy.com";

    @Test
    void API서버에_요청하면_데이터를_받아온다() {
        Pipe inPipe = new Pipe();
        Pipe outPipe = new Pipe();
        HttpNode httpNode = new HttpNode(HOSTNAME, PORT, PATH + CURRENT_TIME);
        httpNode.connectIn(0, inPipe);
        httpNode.connectOut(0, outPipe);
        httpNode.start();

        double temporature = 0.0;
        try {
            inPipe.put(new Message(60_000));
            temporature = new JSONArray(outPipe.get().getString("response")).getJSONObject(0)
                    .getDouble("value");
        } catch (InterruptedException ignore) {
        }
        httpNode.stop();

        assertTrue(String.valueOf(temporature).matches("\\d+.?\\d*"));
    }
}
