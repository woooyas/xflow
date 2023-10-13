package com.nhnacademy.aiot.node;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

/*
 * Flow에서 처리된 결과를 socket으로 내보내는 클래스입니다.
 */
public class SocketOutNode extends Node implements Outputable {
    private Socket socket;
    private final Pipe pipe;

    /**
     * 소켓으로 데이터를 보내는 노드를 생성합니다.
     * @param name 노드의 이름
     * @param socket 데이터를 보낼 소켓
     */
    protected SocketOutNode(String name, Socket socket) {
        super(name);
        this.socket = socket;
        pipe = new Pipe();
    }

    @Override
    public void run() {
        process();
    }

    /**
     * 메세지를 받아 byte 배열로 변하고, 소켓으로 데이터를 보냅니다.
     */
    private void process() {
        try (BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream())) {
            byte[] byteData = pipe.get().getString("value").getBytes();
            writer.write(byteData);
            writer.flush();
        } catch (IOException ignore) {
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void add(int inputPort, Message message) throws InterruptedException {
        pipe.add(message);
    }

}
