package com.nhnacademy.aiot.node;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StdOutNode implements OutputNode{
    private Thread thread;
    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

    
    private final Map<Integer, Pipe> inPipes;  // 값이 나오는 파이프

    /**
     * StdOutNode를 생성합니다.
     */
    public StdOutNode() {
        inPipes = new HashMap<>();
        thread = new Thread(this);
    }

    /**
     * pipe 안의 message들을 꺼냅니다.
     * @throws InterruptedException  pipe에서 message를 못 꺼냈을 경우 생기는 exception입니다.
     * @throws IOException  sysOutMessage에서 던진 Exception입니다.
     */
    public void getMessage() throws InterruptedException, IOException {
        for(Pipe pipe : inPipes.values()) {
            if(!pipe.isEmpty()) {
                sysOutMessage(pipe.get());
            }
        }
    }

    /**
     * pipe에서 꺼낸 message를 표준출력합니다.
     * @param message pipe에서 꺼낸 message입니다.
     * @throws IOException 표준출력할 data가 없을 경우 생기는 문제입니다.
     */
    public void sysOutMessage(Message message) throws IOException {
        writer.write(message.getString("value"));
        writer.newLine();
        writer.flush();
    }

    @Override
    public void start() {
        log.trace("StdOutNode start");
        thread.start();
    }

    @Override
    public void stop() {
        log.trace("StdOutNode stop");
        thread.interrupt();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                getMessage();
            } catch (InterruptedException e) {
                log.error("InterruptedException", e);
            } catch (IOException e) {
                log.error("message System.out fail",e);
            }
            
        }
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        log.trace("pipe connectIn success");
        inPipes.put(port, inPipe);
    }
    
}
