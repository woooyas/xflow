package com.nhnacademy.aiot.node;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.nhnacademy.aiot.http.HTTPMessages;
import com.nhnacademy.aiot.message.Message;

/**
 * 소켓에서 데이터를 받는 노드 클래스입니다.
 */
public class SocketInNode extends Node implements Inputable {

    private static final int DEFAULT_LIFE_TIME = 60_000;
    private static final String DISCONNECTED = "요청을 받는 중 연결이 끊어졌습니다.";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Socket socket;
    private final int bufferSize;
    private final Map<Integer, Target> targets;

    /**
     * 소켓에서 데이터를 받는 노드를 생성합니다.
     * 
     * @param name 노드의 이름입니다.
     * @param socket 데이터를 받아올 소켓입니다.
     */
    public SocketInNode(String name, Socket socket, int bufferSize) {
        super(name);
        this.socket = socket;
        this.bufferSize = bufferSize;
        targets = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            Message message = new Message(DEFAULT_LIFE_TIME);
            message.append("value", new String(getHTTPRequest()));
            targets.values() //
                    .stream() //
                    .forEach(target -> sendMessage(target, message));
        } catch (IOException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private byte[] getHTTPRequest() throws IOException {
        InputStream inputStream = socket.getInputStream();
        byte[] byteData = new byte[0];
        byte[] buffer = new byte[bufferSize];
        int readBytesLength = -1;
        boolean isDone = false;

        while (!isDone && (readBytesLength = inputStream.read(buffer)) != -1) {
            byte[] temp = Arrays.copyOf(byteData, byteData.length + readBytesLength);
            System.arraycopy(buffer, 0, temp, byteData.length, readBytesLength);
            byteData = temp;
            isDone = isCompleted(byteData);
        }

        if (readBytesLength == -1) {
            throw new IOException(DISCONNECTED);
        }

        return byteData;
    }

    private boolean isCompleted(byte[] byteData) {
        if (!HTTPMessages.hasHeaders(byteData)) {
            return false;
        }
        if (!HTTPMessages.hasHeader(byteData, CONTENT_TYPE)) {
            return true;
        }
        return HTTPMessages.getBody(byteData).length() == Integer
                .parseInt(HTTPMessages.getHeaderValue(byteData, CONTENT_LENGTH));
    }

    private void sendMessage(Target target, Message message) {
        try {
            target.add(message);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void connect(int outputPort, Target target) {
        targets.put(outputPort, target);
    }

}
