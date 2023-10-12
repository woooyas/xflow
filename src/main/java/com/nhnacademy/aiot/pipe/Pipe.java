package com.nhnacademy.aiot.pipe;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import com.nhnacademy.aiot.message.Message;

/**
 * 메세지 대기열 역할을 하는 파이프 클래스입니다.
 */
public class Pipe {

    private final BlockingQueue<Message> queue;

    /**
     * 메세지 대기열을 생성합니다.
     */
    public Pipe() {
        queue = new PriorityBlockingQueue<>();
    }

    /**
     * 대기열에 메세지를 추가합니다.
     * 
     * @param message 추가할 메세지입니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    public void add(Message message) throws InterruptedException {
        queue.put(message);
    }

    /**
     * 대기열에 있는 메세지를 가져옵니다.
     * <p>
     * 가져온 메세지가 만료됐을 경우 다음 메세지를 가져옵니다.
     * 
     * @return 대기열에 있는 메세지 객체를 반환합니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    public Message get() throws InterruptedException {
        Message message;
        while ((message = queue.take()).isExpired()) {
            // 메세지가 만료됐으면 반복
        }
        return message;
    }

}
