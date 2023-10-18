package com.nhnacademy.aiot.service;

import com.nhnacademy.aiot.node.FileInNode;
import com.nhnacademy.aiot.node.FunctionNode;
import com.nhnacademy.aiot.node.ResponseNode;
import com.nhnacademy.aiot.pipe.Pipe;

public class CommonjsService implements Service {

    @Override
    public void execute(Pipe inPipe) {
        FileInNode fileInNode = new FileInNode("common.js");
        FunctionNode functionNode = new FunctionNode(message -> {
            StringBuilder stringBuilder = new StringBuilder();
            int length = message.length();
            stringBuilder.append("HTTP/1.1 200 OK\r\n");
            stringBuilder.append("Content-Type: application/javascript; charset=utf-8\r\n");
            stringBuilder.append("Content-Length: " + length + "\r\n");
            stringBuilder.append("\r\n");
            stringBuilder.append(message.getString("file"));
            message.put("response", stringBuilder.toString());
            return message;
        });
        ResponseNode responseNode = new ResponseNode();
        Pipe pipe = new Pipe();
        Pipe pipe2 = new Pipe();
        fileInNode.connectIn(0, inPipe);
        fileInNode.connectOut(0, pipe);
        functionNode.connectIn(0, pipe);
        functionNode.connectOut(0, pipe2);
        responseNode.connectIn(0, pipe2);

        fileInNode.start();
        functionNode.start();
        responseNode.start();
    }
    
}
