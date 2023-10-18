package com.nhnacademy.aiot.service;

import org.json.JSONArray;
import org.json.JSONObject;

import com.nhnacademy.aiot.node.FunctionNode;
import com.nhnacademy.aiot.node.HttpNode;
import com.nhnacademy.aiot.node.ResponseNode;
import com.nhnacademy.aiot.pipe.Pipe;

public class HumidityService implements Service {
    private static final String HOSTNAME = "ems.nhnacademy.com";
    private static final int PORT = 1880;
    private static final String PATH = "/ep/humidity/24e124126c457594?count=1&st=1696772438&et="
            + (int) (System.currentTimeMillis() / 1000 - 30);
    private static final String RESPONSE = "response";

    @Override
    public void execute(Pipe inPipe) {
        HttpNode httpNode = new HttpNode(HOSTNAME, PORT, PATH);
        FunctionNode functionNode = new FunctionNode(message -> {
            JSONObject jsonObject = new JSONArray(message.getString(RESPONSE)).getJSONObject(0);
            String dateTime = jsonObject.getString("time");
            dateTime= dateTime.substring(0, 10) + " " + dateTime.substring(11, 19);
            double humidity = jsonObject.getDouble("value");
            System.out.println(dateTime);
            JSONObject temp = new JSONObject();
            temp.put("dateTime", dateTime);
            temp.put("humidity", humidity);
            StringBuilder stringBuilder = new StringBuilder();
            int length = message.getString(RESPONSE).length();
            stringBuilder.append("HTTP/1.1 200 OK\r\n");
            stringBuilder.append("Content-Type: application/json; charset=utf-8\r\n");
            stringBuilder.append("Content-Length: " + length + "\r\n");
            stringBuilder.append("\r\n");
            stringBuilder.append(temp.toString());
            message.put(RESPONSE, stringBuilder.toString());
            return message;
        });
        ResponseNode responseNode = new ResponseNode();
        Pipe pipe = new Pipe();
        Pipe pipe2 = new Pipe();

        httpNode.connectIn(0, inPipe);
        httpNode.connectOut(0, pipe);
        functionNode.connectIn(0, pipe);
        functionNode.connectOut(0, pipe2);
        responseNode.connectIn(0, pipe2);

        httpNode.start();
        functionNode.start();
        responseNode.start();
    }
}
