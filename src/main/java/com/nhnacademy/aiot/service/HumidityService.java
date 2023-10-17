package com.nhnacademy.aiot.service;

import com.nhnacademy.aiot.node.FunctionNode;
import com.nhnacademy.aiot.node.HttpNode;
import com.nhnacademy.aiot.node.ResponseNode;
import com.nhnacademy.aiot.pipe.Pipe;

public class HumidityService implements Service {
    private static final String HOSTNAME = "ems.nhnacademy.com";
    private static final int PORT = 1880;
    private static final String PATH = "/ep/humidity/24e124126c457594?count=1&st=1696772438&et="
            + (int) (System.currentTimeMillis() / 1000 - 30);

    @Override
    public void execute(Pipe inPipe) {
        HttpNode httpNode = new HttpNode(HOSTNAME, PORT, PATH);
        FunctionNode functionNode = new FunctionNode(message -> {
            StringBuilder stringBuilder = new StringBuilder();
            int length = message.length();
            stringBuilder.append("HTTP/1.1 200 OK\r\n");
            stringBuilder.append("Content-Type: text/html; charset=utf-8\r\n");
            stringBuilder.append("Content-Length: " + length + "\r\n");
            stringBuilder.append("\r\n");
            stringBuilder.append(message.getString("response"));
            message.put("response", stringBuilder.toString());
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
