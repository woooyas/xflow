package com.nhnacademy.aiot.pipe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import com.nhnacademy.aiot.message.Message;

/**
 * 노드 간 메세지를 전달하는 파이프입니다.
 */
public class Pipe {

    private final BlockingQueue<Message> queue;

    /**
     * 파이프를 생성합니다.
     */
    public Pipe() {
        this.queue = new PriorityBlockingQueue<>();
    }

    /**
     * 메세지를 파이프에 넣습니다.
     * <p>
     * 메세지를 넣을 수 없을 경우 기다립니다.
     * 
     * @param message 파이프에 넣을 메세지입니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    public void put(Message message) throws InterruptedException {
        queue.put(message);
    }

    /**
     * 파이프에서 메세지를 꺼냅니다.
     * <p>
     * 꺼내올 메세지가 없을 경우 기다립니다.
     * 
     * @return 파이프에서 꺼낸 메세지를 반환합니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    public Message get() throws InterruptedException {
        Message message = queue.take();
        while (message.isExpired()) {
            message = queue.take();
        }
        return message;
    }

    /**
     * 파이프가 비었는지 확인합니다.
     * 
     * @return 파이프가 비었으면 <code>true</code>를 반환합니다.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
