package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

public class FilterNode extends Node implements Inputable, Outputable {
    private final Predicate<Message> predicate;
    private final Pipe pipe;
    private final Map<Integer, Target> targets;

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

    private boolean checkMessage(Message message) {
        return predicate.test(message);
    }

    @Override // 필터 노드가 해야할거, 필터에서 걸러지지 않았다면? 실행할 파트
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Message message = pipe.get();
                if (checkMessage(message)) {
                    for (Target target : targets.values()) {
                        target.add(message);
                    }
                }
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 메세지를 pipe에 추가하는 구문
     */
    @Override
    public void add(int inputPort, Message message) throws InterruptedException {
        if (checkMessage(message)) {
            pipe.add(message);
        }
    }

    /**
     * 다음 노드와 연결.
     * 
     * @param outputPort 메세지를 받을 노드
     * @param target     메세지를 받을 노드의 포트 번호.
     */
    @Override
    public void connect(int outputPort, Target target) {
        targets.put(outputPort, target);
    }
}
