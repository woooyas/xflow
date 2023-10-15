package com.nhnacademy.aiot.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일을 검사하는 노드. 조건에 부합하면 파이프에 넣고, 부합하지 않다면 폐기.
 * <p>
 * 조건은 사용자가 지정.
 * <p>
 * running으로 thread의 동작을 제어.
 */
@Slf4j
public class FilterNode implements InputNode, OutputNode {

    private final Map<Integer, Pipe> inputPipe;
    private final Map<Integer, Pipe> outputPipe;
    private final Predicate<Message> predicate;
    // private final Map<Integer, Pipe> connectPipe;

    private Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(false);

    // TODO : 조건을 안줄 경우 기본값으로 GET/Http/1.1을 줘볼 예정.
    // public FilterNode() {
    // this();
    // // predicate = message -> message.startWith("GET/ HTTP/ 1.1");
    // JSONObject jsonMessgage = (JSONObject) message;
    // String keyValue = jsonMessgage.getString("GET");
    // predicate = keyValue;
    // }
    // // 모르겠다.

    /**
     * 필터 노드를 생성.
     * 
     * @param predicate 사용자가 조건을 설정해서 사용.
     *                  <p>
     *                  예시) (message -> message.getInt("key") > 5)
     */
    public FilterNode(Predicate<Message> predicate) {
        this.predicate = predicate;
        inputPipe = new HashMap();
        outputPipe = new HashMap();
    }

    /**
     * 
     * @return 조건(predicate)에 부합하다면 true, 아니면 false
     */
    private boolean checkMessage(Message message) {
        return predicate.test(message);
    }

    /**
     * running을 true로 변경.
     */
    @Override
    public void start() {
        running.set(true);
        thread = new Thread(this);
        thread.start();
        log.trace("Thread start.");
    }

    /**
     * running을 false로 변경.
     */
    @Override
    public void stop() {
        running.set(false);
        thread.interrupt();
        log.trace("Thread stop.");
    }

    /**
     * inPipe에 inputPipe에 들어간 값을 전부 넣고, checkMessage Method로 검사.
     * checkMessage를 사용해서 반환된 값에 따라 동작.
     * 만약 true가 반환된다면 pipe에 message를 넣고, false면 버린다.
     */
    @Override
    public void run() {
        while (running.get()) {
            try {
                for (Pipe inPipe : inputPipe.values()) {
                    Message message = inPipe.get();
                    if (checkMessage(message)) {
                        for (Pipe outPipe : outputPipe.values()) {
                            outPipe.put(message);
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread is interrupted", e);
            }
        }
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        // 노드가 사용할 메세지를 꺼내오는 파이프
        inputPipe.put(port, inPipe);
        log.trace("ConnectIn is successful", port);
    }

    @Override
    public void connectOut(int port, Pipe outPipe) {
        // 노드가 처리한 메세지를 담는 파이프.
        outputPipe.put(port, outPipe);
        log.trace("ConnectOut is successful", port);
    }

}
