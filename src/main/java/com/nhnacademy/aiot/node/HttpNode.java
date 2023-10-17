package com.nhnacademy.aiot.node;

import java.io.IOException;
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
 * HTTP 요청을 보내고 응답을 받는 노드입니다.
 * <ul>
 * <li><strong>입 력</strong>
 * <ul>
 * <li>메세지 객체
 * </ul>
 * <li><strong>출 력</strong>
 * <ul>
 * <li>"response" : (String)응답 내용 문자열
 * </ul>
 * </ul>
 */
@Slf4j
public class HttpNode implements InputNode, OutputNode {

    private static final String CONNECTION_FAIL = "소켓 연결에 문제가 발생했습니다.";
    private static final String NO_SOCKET = "메세지에 소켓이 없습니다.";
    private static final String NODE_STARTED = "HTTP 노드가 시작되었습니다.";
    private static final String NODE_STOPPED = "HTTP 노드가 종료되었습니다.";

    private static final String CRLF = "\r\n\r\n";
    private static final String VERSION = " HTTP/1.1";
    private static final String METHOD = "GET ";
    private static final String RESPONSE = "response";
    private static final int BUFFER_SIZE = 10_000;
    private static final int DEFAULT_HANDLER_COUNT = 10;

    private final Thread thread;
    private Pipe inPipe;
    private final Map<Integer, Pipe> outPipes;
    private final ExecutorService handlerPool;
    private final String hostname;
    private final int port;
    private final String path;

    /**
     * HTTP 요청을 보내고 응답을 받는 노드를 생성합니다.
     * 
     * @param concurrentRequestLimit 동시에 받아올 수 있는 메세지의 개수입니다.
     * @param hostname 접속할 서버의 주소입니다.
     * @param port 접속할 서버의 포트 번호입니다.
     * @param path 서버에서 받아올 리소스입니다.
     */
    public HttpNode(int concurrentRequestLimit, String hostname, int port, String path) {
        thread = new Thread(this, "HttpNode");
        outPipes = new HashMap<>();
        handlerPool = Executors.newFixedThreadPool(concurrentRequestLimit);
        this.hostname = hostname;
        this.port = port;
        this.path = path;
    }

    /**
     * HTTP 요청을 보내고 응답을 받는 노드를 생성합니다.
     * 
     * @param hostname 접속할 서버의 주소입니다.
     * @param port 접속할 서버의 포트 번호입니다.
     * @param path 서버에서 받아올 리소스입니다.
     */
    public HttpNode(String hostname, int port, String path) {
        this(DEFAULT_HANDLER_COUNT, hostname, port, path);
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
                Socket socket = new Socket(hostname, port);
                socket.getOutputStream().write((METHOD + path + VERSION + CRLF).getBytes());
                byte[] buffer = new byte[BUFFER_SIZE];
                int length = socket.getInputStream().read(buffer);
                socket.close();

                message.put(RESPONSE, new String(buffer, 0, length).split(CRLF)[1]);
                putMessageInPipes(message);

            } catch (JSONException e) {
                log.error(NO_SOCKET, e);
            } catch (IOException e) {
                log.error(CONNECTION_FAIL, e);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        });
    }

    private void postprocess() {
        inPipe = null;
        outPipes.clear();
        log.debug(NODE_STOPPED);
    }

    private void putMessageInPipes(Message message) throws InterruptedException {
        for (Pipe pipe : outPipes.values()) {
            pipe.put(message);
        }
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
