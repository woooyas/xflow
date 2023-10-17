package com.nhnacademy.aiot;

import java.io.IOException;
import java.net.http.HttpClient;
import com.nhnacademy.aiot.exception.NoMessageException;
import com.nhnacademy.aiot.node.FileInNode;
import com.nhnacademy.aiot.node.FunctionNode;
import com.nhnacademy.aiot.node.HttpNode;
import com.nhnacademy.aiot.node.RequestNode;
import com.nhnacademy.aiot.node.ResponseNode;
import com.nhnacademy.aiot.node.ServerNode;
import com.nhnacademy.aiot.pipe.Pipe;

@SuppressWarnings("squid:S2187")
public class MainTest {

    public static void main(String[] args) throws IOException {
        ServerNode serverNode = new ServerNode();
        RequestNode requestNode = new RequestNode();

        Pipe pipe = new Pipe();
        serverNode.connectOut(0, pipe);
        requestNode.connectIn(0, pipe);

        serverNode.start();
        requestNode.start();

        startIndexService(requestNode);
        startFileService(requestNode);
        startTemporatureApi(requestNode);

        System.out.println("실행");
        while (!Thread.interrupted()) {
        }
    }

    private static void startIndexService(RequestNode requestNode) throws IOException {
        FunctionNode functionNode1 = new FunctionNode(message -> {
            if (message.getString("request").contains("GET / HTTP/1.1")) {
                return message;
            }
            throw new NoMessageException();
        });
        FileInNode fileInNode = new FileInNode("index.html");
        FunctionNode functionNode2 = new FunctionNode(message -> {
            String body = message.getString("file");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("HTTP/1.1 200 OK\r\n");
            stringBuilder.append("Content-Type: text/html; charset=utf-8\r\n");
            stringBuilder.append("Content-Length: " + body.length() + "\r\n");
            stringBuilder.append("\r\n");
            stringBuilder.append(body);
            message.put("response", stringBuilder.toString());
            return message;
        });
        ResponseNode responseNode = new ResponseNode();

        Pipe pipe1 = new Pipe();
        requestNode.connectOut(0, pipe1);
        functionNode1.connectIn(0, pipe1);
        Pipe pipe2 = new Pipe();
        functionNode1.connectOut(0, pipe2);
        fileInNode.connectIn(0, pipe2);
        Pipe pipe3 = new Pipe();
        fileInNode.connectOut(0, pipe3);
        functionNode2.connectIn(0, pipe3);
        Pipe pipe4 = new Pipe();
        functionNode2.connectOut(0, pipe4);
        responseNode.connectIn(0, pipe4);

        functionNode1.start();
        fileInNode.start();
        functionNode2.start();
        responseNode.start();
    }

    private static void startFileService(RequestNode requestNode) {
        FunctionNode functionNode1 = new FunctionNode(message -> {
            if (message.getString("request").contains("GET /common.js HTTP/1.1")) {
                return message;
            }
            throw new NoMessageException();

        });
        FileInNode fileInNode = new FileInNode("common.js");
        FunctionNode functionNode2 = new FunctionNode(message -> {
            String body = message.getString("file");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("HTTP/1.1 200 OK\r\n");
            stringBuilder.append("Content-Type: application/javascript\r\n");
            stringBuilder.append("Content-Length: " + body.length() + "\r\n");
            stringBuilder.append("\r\n");
            stringBuilder.append(body);
            message.put("response", stringBuilder.toString());
            message.put("value", stringBuilder.toString());
            return message;
        });
        ResponseNode responseNode = new ResponseNode();

        Pipe pipe1 = new Pipe();
        requestNode.connectOut(1, pipe1);
        functionNode1.connectIn(0, pipe1);
        Pipe pipe2 = new Pipe();
        functionNode1.connectOut(0, pipe2);
        fileInNode.connectIn(0, pipe2);
        Pipe pipe3 = new Pipe();
        fileInNode.connectOut(0, pipe3);
        functionNode2.connectIn(0, pipe3);
        Pipe pipe4 = new Pipe();
        functionNode2.connectOut(0, pipe4);
        responseNode.connectIn(0, pipe4);

        functionNode1.start();
        fileInNode.start();
        functionNode2.start();
        responseNode.start();
    }

    private static void startTemporatureApi(RequestNode requestNode) {
        FunctionNode functionNode1 = new FunctionNode(message -> {
            if (message.getString("request").contains("GET /temporature HTTP/1.1")) {
                return message;
            }
            throw new NoMessageException();
        });
        HttpNode httpNode = new HttpNode("ems.nhnacademy.com", 1880,
                "/ep/temperature/24e124126c457594?count=1&st=1696772438&et="
                        + (int) (System.currentTimeMillis() / 1000 - 30));

        Pipe pipe1 = new Pipe();
        requestNode.connectOut(2, pipe1);
        functionNode1.connectIn(0, pipe1);

        functionNode1.start();
    }
}
