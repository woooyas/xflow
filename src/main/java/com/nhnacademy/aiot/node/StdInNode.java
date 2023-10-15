package com.nhnacademy.aiot.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StdInNode implements InputNode {
    private Message message;
    private Scanner scanner = new Scanner(System.in);
    private Logger log;
    
    private final int LIFETIME = 10000;
    private final Map<Integer, Pipe> outPipes;

    public StdInNode() {
        outPipes = new HashMap<>();
    }

    /**
     * scanner를 message로 바꿉니다.
     * @param scanner 입력받은 문자열입니다.
     */
    void scanToMessage(Scanner scanner) {
        message = new Message(LIFETIME);
        message.append("value", scanner.nextLine());
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
            log.trace(e.toString());
        }
    }

    @Override
    public void start() {
        log.trace("입력 시작");
        run();
    }

    @Override
    public void stop() {
        log.trace("시스템 종료");
        System.exit(0);
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
        outPipes.put(port, outPipe);
    }
    
}
