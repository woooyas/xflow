package com.nhnacademy.aiot.node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;
import lombok.extern.slf4j.Slf4j;

/**
 * 소켓에서 HTTP 요청을 받는 노드입니다.
 * <ul>
 * <li><strong>입 력</strong>
 * <ul>
 * <li>"socket" : (Socket)소켓 객체
 * </ul>
 * <li><strong>출 력</strong>
 * <ul>
 * <li>"request" : (String)요청 문자열
 * <li>"socket" : (Socket)소켓 객체
 * </ul>
 * </ul>
 */
@Slf4j
public class RequestNode implements InputNode, OutputNode {

    private static final String REQUEST = "request";
    private static final String SOCKET = "socket";
    private static final String CONNECTION_FAIL = "소켓 연결에 문제가 발생했습니다.";
    private static final String NO_SOCKET = "메세지에 소켓이 없습니다.";
    private static final String NODE_STARTED = "요청 노드가 시작되었습니다.";
    private static final String NODE_CLOSED = "요청 노드가 중지되었습니다.";

    private static final int DEFAULT_HANDLER_COUNT = 10;

    private final Thread thread;
    private Pipe inPipe;
    private final Map<Integer, Pipe> outPipes;
    private final ExecutorService handlerPool;

    /**
     * 소켓에서 HTTP 요청을 받는 노드를 생성합니다.
     * 
     * @param concurrentRequestLimit 동시에 받아올 수 있는 요청의 개수입니다.
     */
    public RequestNode(int concurrentRequestLimit) {
        thread = new Thread(this, "RequestNode");
        outPipes = new HashMap<>();
        handlerPool = Executors.newFixedThreadPool(concurrentRequestLimit);
    }

    /**
     * 소켓에서 HTTP 요청을 받는 노드를 생성합니다.
     */
    public RequestNode() {
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

        } catch (JSONException e) {
            log.error(NO_SOCKET, e);
        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
    }

    private void submitHandler(Message message) {
        handlerPool.submit(() -> {
            try {
                message.put(REQUEST, new BufferedReader(new InputStreamReader( //
                        ((Socket) message.get(SOCKET)).getInputStream())).readLine());
                putMessageInPipes(message);

            } catch (IOException e) {
                log.error(CONNECTION_FAIL, e);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void putMessageInPipes(Message message) throws InterruptedException {
        for (Pipe pipe : outPipes.values()) {
            pipe.put(message);
        }
    }

    private void postprocess() {
        inPipe = null;
        outPipes.clear();
        handlerPool.shutdown();
        log.debug(NODE_CLOSED);
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        this.inPipe = inPipe;
    }

    @Override
    public void connectOut(int port, Pipe outPipe) {
        outPipes.put(port, outPipe);
    }

}
