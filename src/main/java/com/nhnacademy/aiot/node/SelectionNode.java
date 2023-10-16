package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

import lombok.extern.slf4j.Slf4j;

/*
 * 특정 message를 특정 출력 port로 내보내는 노드입니다.
 */
@Slf4j
public class SelectionNode implements InputNode, OutputNode {
    private static final String INVALID_PORT = "{}: 유효하지 않은 포트를 선택했습니다.({})";
    private static final String NODE_STARTED = "{}: Selection Node가 시작되었습니다.";
    private static final String NODE_STOPPED = "{}: Selection Node가 종료되었습니다.";
    private static final String SEND_MESSAGE = "{}: {}번 파이프로 {}를 보냈습니다.";

    private final UUID uuid;
    private final Thread thread;
    private final Map<Integer, Pipe> outPipes;
    private final Function<Message, Integer> function;
    private Pipe inPipe;

    /**
     * @param predicate   조건식
     * @param outPipeNums 조건식이 true인 메세지를 보낼 포트
     */
    public SelectionNode(Function<Message, Integer> function) {
        this.uuid = UUID.randomUUID();
        this.thread = new Thread(this, "SelectionNode(" + getUuid() + ")");
        this.function = function;
        outPipes = new HashMap<>();
    }

    public UUID getUuid() {
        return uuid;
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
        while (!Thread.currentThread().isInterrupted()) {
            process();
        }
        postprocess();
    }

    private void preprocess() {
        log.debug(NODE_STARTED, getUuid());
    }

    private void process() {
        try {
            Message message = inPipe.get();
            sendMessage(message, function.apply(message));
            log.debug(SEND_MESSAGE, getUuid(), function.apply(message), message.get("value"));
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    private void postprocess() {
        log.debug(NODE_STOPPED, getUuid());
    }

    private void sendMessage(Message message, int port) throws InterruptedException {
        if (outPipes.get(port) == null) {
            log.debug(INVALID_PORT, getUuid(), port);
        } else {
            outPipes.get(port).put(message);
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
