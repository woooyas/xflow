package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;
import lombok.extern.slf4j.Slf4j;

/**
 * 소켓에게 HTTP 응답을 보내는 노드입니다.
 * <ul>
 * <li><strong>입 력</strong>
 * <ul>
 * <li>"response" : (String)응답 문자열
 * <li>"socket" : (Socket)소켓 객체
 * </ul>
 */
@Slf4j
public class ResponseNode implements OutputNode {

    private static final String SOCKET = "socket";
    private static final String CONNECTION_FAIL = "소켓 연결에 문제가 발생했습니다.";
    private static final String NODE_STARTED = "요청 노드가 시작되었습니다.";
    private static final String NODE_CLOSED = "요청 노드가 중지되었습니다.";
    private static final String NO_DATA = "메세지에 데이터가 없습니다.";

    private static final int DEFAULT_HANDLER_COUNT = 10;

    private final Thread thread;
    private Pipe inPipe;
    private final ExecutorService handlerPool;

    /**
     * 소켓에게 HTTP 요청을 보내는 노드를 생성합니다.
     * 
     * @param concurrentRequestLimit 동시에 보낼 수 있는 응답의 개수입니다.
     */
    public ResponseNode(int concurrentRequestLimit) {
        thread = new Thread(this);
        handlerPool = Executors.newFixedThreadPool(concurrentRequestLimit);
    }

    /**
     * 소켓에게 HTTP 요청을 보내는 노드를 생성합니다.
     */
    public ResponseNode() {
        this(DEFAULT_HANDLER_COUNT);
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
            submitHandler(inPipe.get());

        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
    }

    private void submitHandler(Message message) {
        handlerPool.submit(() -> {
            try {
                Socket socket = (Socket) message.get(SOCKET);
                socket.getOutputStream().write(message.getString("response").getBytes());
                socket.close();

            } catch (JSONException e) {
                log.error(NO_DATA, e);
            } catch (IOException e) {
                log.error(CONNECTION_FAIL, e);
            }
        });
    }

    private void postprocess() {
        inPipe = null;
        log.debug(NODE_CLOSED);
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        this.inPipe = inPipe;
    }

}
