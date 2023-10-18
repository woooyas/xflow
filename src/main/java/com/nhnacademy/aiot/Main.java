package com.nhnacademy.aiot;

import java.io.IOException;
import com.nhnacademy.aiot.node.RequestNode;
import com.nhnacademy.aiot.node.SelectionNode;
import com.nhnacademy.aiot.node.ServerNode;
import com.nhnacademy.aiot.pipe.Pipe;
import com.nhnacademy.aiot.service.CommonjsService;
import com.nhnacademy.aiot.service.HumidityService;
import com.nhnacademy.aiot.service.IndexService;
import com.nhnacademy.aiot.service.TemperatureService;

/**
 * 애플리케이션을 시작하는 클래스입니다.
 */
public class Main {

    private static final String REQUEST = "request";

    /**
     * 애플리케이션을 시작합니다.
     * 
     * @param args 커맨드라인 인자입니다.
     * @throws IOException 서버 노드 생성 중 문제가 발생한 경우
     */
    public static void main(String[] args) throws IOException {
        ServerNode serverNode = new ServerNode();
        RequestNode requestNode = new RequestNode();
        SelectionNode selectionNode = new SelectionNode(message -> {
            String request = message.getString(REQUEST);

            if (request.equals("GET / HTTP/1.1")) {
                return 0;
            }
            if (request.equals("GET /common.js HTTP/1.1")) {
                return 1;
            }
            if (request.equals("GET /temperature HTTP/1.1")) {
                return 2;
            }
            if (request.equals("GET /humidity HTTP/1.1")) {
                return 3;
            }
            return 4;
        });

        Pipe pipe1 = new Pipe();
        Pipe pipe2 = new Pipe();
        Pipe pipe3 = new Pipe();
        Pipe pipe4 = new Pipe();
        Pipe pipe5 = new Pipe();
        Pipe pipe6 = new Pipe();
        Pipe pipe7 = new Pipe();

        serverNode.connectOut(0, pipe1);
        requestNode.connectIn(0, pipe1);
        requestNode.connectOut(0, pipe2);
        selectionNode.connectIn(0, pipe2);
        selectionNode.connectOut(0, pipe3);
        selectionNode.connectOut(1, pipe4);
        selectionNode.connectOut(2, pipe5);
        selectionNode.connectOut(3, pipe6);
        selectionNode.connectOut(4, pipe7);

        serverNode.start();
        requestNode.start();
        selectionNode.start();

        new IndexService().execute(pipe3);
        new CommonjsService().execute(pipe4);
        new TemperatureService().execute(pipe5);
        new HumidityService().execute(pipe6);
    }

}
