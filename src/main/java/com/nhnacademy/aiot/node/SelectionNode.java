package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;

import com.nhnacademy.aiot.consumer.SelectionNodeConsumer;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

/*
 * 특정 message를 특정 출력 port로 내보내는 클래스입니다.
 */
public class SelectionNode extends Node implements Inputable, Outputable {
    private final Map<Integer, Target> targets;
    private final Pipe pipe;
    private final SelectionNodeConsumer consumer;

    /**
     * @param name 스레드 이름
     * @param consumer 조건식이 담긴 Consumer 인스턴스
     */
    public SelectionNode(String name, SelectionNodeConsumer consumer) {
        super(name);
        this.consumer = consumer;
        targets = new HashMap<>();
        pipe = new Pipe();
    }

    @Override
    public void run() {
        try {
            consumer.consume(pipe.get(), this);
        } catch (InterruptedException ignore) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void connect(int outputPort, Target target) {
        targets.put(outputPort, target);
    }

    /**
     * 특정 target으로 특정 메세지를 보냅니다.
     * @param index 보내고 싶은 target과 연결된 outputPort의 번호
     * @param message 보낼 메세지
     * @throws InterruptedException 인터럽트가 발생할 경우
     */
    public void send(int index, Message message) throws InterruptedException {
        targets.get(index).add(message);
    }

    /**
     * 연결된 모든 target에게 메세지를 보냅니다.
     * @param message 보낼 메세지
     * @throws InterruptedException 인터럽트가 발생할 경우
     */
    public void send(Message message) throws InterruptedException {
        for (Target target : targets.values()) {
            target.add(message);
        }
    }

    @Override
    public void add(int inputPort, Message message) throws InterruptedException {
        pipe.add(message);
    }
}
