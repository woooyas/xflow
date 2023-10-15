package com.nhnacademy.aiot.node;

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
    Thread thread;
    
    private final Map<Integer, Pipe> inPipes;  // 값이 나오는 파이프

    /**
     * StdOutNode를 생성합니다.
     */
    public StdOutNode() {
        inPipes = new HashMap<>();
    }

    /**
     * pipe 안의 message들을 꺼냅니다.
     */
    public Message getMessage() {
        for(Pipe pipe : inPipes.values()) {
            try {
                return pipe.get();
            } catch (InterruptedException e) {
                log.trace("message 꺼내기 실패");
                thread.interrupt();
            }
        }
        return null;
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
            /* Message message = getMessage();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
            writer.write(message.getString("value"));
            writer.newLine();
            writer.flush(); */

            Message message = getMessage();
            try {
                OutputStreamWriter outputStream = new OutputStreamWriter(System.out);
                outputStream.write(message.getString("value"+"\n"));
                outputStream.flush();
            } catch (JSONException e) {
                log.trace("JSONException" + e);
                thread.interrupt();
            } catch (IOException e) {
                log.error("IOException" + e);
                thread.interrupt();
            }

        }
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        log.trace("pipe connectIn success");
        inPipes.put(port, inPipe);
    }
    
}
