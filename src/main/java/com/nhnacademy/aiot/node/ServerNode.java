package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;
import lombok.extern.slf4j.Slf4j;

/**
 * 클라이언트의 접속을 받는 노드입니다.
 * <li><strong>출 력</strong>
 * <ul>
 * <li>"socket" : (Socket)소켓 객체
 * </ul>
 * </ul>
 */
@Slf4j
public class ServerNode implements InputNode {

    private static final String CONNECT_FAILED = "통신에 문제가 발생했습니다.";
    private static final String NODE_STARTED = "서버 노드를 시작합니다.";
    private static final String NODE_STOPPED = "서버 노드를 종료합니다.";

    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_LIFE_TIME = 60_000;

    private final Thread thread;
    private final Map<Integer, Pipe> outPipes;
    private final ServerSocket serverSocket;

    /**
     * 서버 노드를 생성합니다.
     * 
     * @param port 서버 포트 번호입니다.
     * @throws IOException 서버 소켓에 문제가 발생한 경우
     */
    public ServerNode(int port) throws IOException {
        thread = new Thread(this);
        outPipes = new HashMap<>();
        serverSocket = new ServerSocket(port);
    }

    /**
     * <strong>80</strong>번 포트를 사용하는 서버 노드를 생성합니다.
     * 
     * @throws IOException 서버 소켓에 문제가 발생한 경우
     */
    public ServerNode() throws IOException {
        this(DEFAULT_PORT);
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        preprocess();
        while (!Thread.interrupted()) {
            process();
        }
        postprocess();
    }

    private void preprocess() {
        log.debug(NODE_STARTED);
    }

    private void process() {
        try {
            Message message = new Message(DEFAULT_LIFE_TIME);
            message.put("socket", serverSocket.accept());
            putMessageInPipes(message);

        } catch (IOException e) {
            log.error(CONNECT_FAILED, e);
        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
    }

    private void postprocess() {
        outPipes.clear();
        log.debug(NODE_STOPPED);
    }

    private void putMessageInPipes(Message message) throws InterruptedException {
        for (Pipe pipe : outPipes.values()) {
            pipe.put(message);
        }
    }

    @Override
    public void connectOut(int port, Pipe outPipe) {
        outPipes.put(port, outPipe);
    }

}
