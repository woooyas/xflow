package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자의 명령을 수행하는 노드입니다.
 * <ul>
 * <li><strong>입 력</strong>
 * <ul>
 * <li>사용자 정의
 * </ul>
 * <li><strong>출 력</strong>
 * <ul>
 * <li>사용자 정의
 * </ul>
 * </ul>
 */
@Slf4j
public class FunctionNode implements InputNode, OutputNode {

    private static final String NODE_STARTED = "함수 노드가 시작되었습니다.";
    private static final String NODE_STOPPED = "함수 노드가 종료되었습니다.";

    private final Thread thread;
    private Pipe inPipe;
    private final Map<Integer, Pipe> outPipes;
    private final UnaryOperator<Message> function;

    /**
     * 함수 노드를 생성합니다.
     * 
     * @param function 사용자 정의 함수입니다.
     */
    public FunctionNode(UnaryOperator<Message> function) {
        thread = new Thread(this);
        outPipes = new HashMap<>();
        this.function = function;
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
            putMessageInPipes(function.apply(inPipe.get()));

        } catch (InterruptedException ignore) {
            thread.interrupt();
        }
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
