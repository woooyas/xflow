package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

public class FilterNode extends Node implements Inputable, Outputable {
    private final Predicate<Message> predicate;
    private final Pipe pipe;
    Map<Integer, Target> targets;

    /**
     * 필터 노드를 생성.
     * 
     * @param name
     * @param predicate 조건식. 사용자가 조건을 설정해서 사용합니다.
     *                  <p>
     *                  예시. (message -> message.getInt("key") < 5)
     */
    public FilterNode(String name, Predicate<Message> predicate) {
        super(name);
        this.predicate = predicate;
        pipe = new Pipe();
        targets = new HashMap<>();
    }

    boolean messageCheck(Message message) {
        return predicate.test(message);
    }

    public void filter(Message message) throws InterruptedException {
        if (messageCheck(message)) {
            pipe.add(message);
        }
    }

    @Override // 필터 노드가 해야할거, 필터에서 걸러지지 않았다면? 실행할 파트
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message message = pipe.get();
                if (messageCheck(message)) {
                    for (Target target : targets.values()) {
                        target.add(message);
                    }
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override // 메세지를 던지는거
    public void add(int inputPort, Message message) throws InterruptedException {
        pipe.add(message);
    }

    @Override // 다음 노드와 연결.
    public void connect(int outputPort, Target target) {
        targets.put(outputPort, target);
    }

    public static void main(String[] args) throws InterruptedException {
        FilterNode filterNode = new FilterNode("1234", message -> message.getInt("value") < 5);
        Message message = new Message(100);
        message.append("value", 4);
        filterNode.messageCheck(message);
    }
}
