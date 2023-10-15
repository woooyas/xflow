package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StdInNode implements InputNode {
    private Message message;
    private Scanner scanner = new Scanner(System.in);
    private Thread thread;
    
    private static final int LIFETIME = 10_000;
    private final Map<Integer, Pipe> outPipes;

    public StdInNode() {
        outPipes = new HashMap<>();
        thread = new Thread();
    }

    /**
     * scammer를 message로 바꿉니다.
     * @param scanner stream에 있는 data를 받아옵니다.
     */
    void scanToMessage(Scanner scanner) {
        message = new Message(LIFETIME);
        message.put("value", scanner.nextLine());
    }

    /**
     * pipe에 메세지를 넣습니다.
     * @param pipe 메세지를 넣은 공간입니다.
     */
    void putMessage(Pipe pipe) {
        try {
            pipe.put(message);
        } catch (InterruptedException e) {
            log.trace("message 넣기 실패");
            thread.interrupt();
        }
    }

    @Override
    public void start() {
        log.trace("입력 시작");
        thread.start();
    }

    @Override
    public void stop() {
        log.trace("StdInNode 종료");
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            scanToMessage(scanner);
            for(Pipe pipe: outPipes.values()) {
                putMessage(pipe);
            }
        }
    }

    @Override
    public void connectOut(int port, Pipe outPipe) {
        log.trace("pipe 연결");
        outPipes.put(port, outPipe);
    }
    
}
