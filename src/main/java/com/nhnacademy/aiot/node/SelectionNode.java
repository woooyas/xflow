package com.nhnacademy.aiot.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

import lombok.extern.slf4j.Slf4j;

/*
 * 특정 message를 특정 출력 port로 내보내는 노드입니다.
 */
@Slf4j
public class SelectionNode<T> implements InputNode, OutputNode {
    private static final String INVALIDPORT = "{}: 유효하지 않은 포트를 선택했습니다.{}";

    private final UUID uuid;
    private final Thread thread;
    private final Map<Integer, Pipe> outPipes;
    private final List<Integer> ports;
    private Pipe inPipe;
    private Predicate<T> predicate;

    /**
     * @param predicate 조건식
     * @param outPipeNums 조건식이 true인 메세지를 보낼 포트
     */
    public SelectionNode(Predicate<T> predicate, int... outPipeNums) {
        this.uuid = UUID.randomUUID();
        this.thread = new Thread(this);
        this.predicate = predicate;
        outPipes = new HashMap<>();
        ports = new ArrayList<>();
        for (int i : outPipeNums) {
            ports.add(i);
        }
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    /**
     * 입력한 port가 outPipes에 있는지 확인하고, 조건식이 true인 메세지만 지정한 port로 내보냅니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    private void process() throws InterruptedException {
        setPredicate();
        Message message = inPipe.get();
        if (predicate.test((T) message.get("value"))) {
            for (int port : ports) {
                outPipes.get(port).put(message);
            }
        }
    }

    private void setPredicate() {
        for (int num : ports) {
            if (!outPipes.containsKey(num)) {
                log.error(INVALIDPORT, uuid, num);
                ports.remove(num);
            }
        }
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                process();
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
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
