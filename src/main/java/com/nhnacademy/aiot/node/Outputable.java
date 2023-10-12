package com.nhnacademy.aiot.node;

import com.nhnacademy.aiot.message.Message;

/**
 * 데이터를 출력하는 출력 노드 인터페이스입니다.
 */
public interface Outputable {

    /**
     * 대기열에 메세지를 추가합니다.
     * 
     * @param inputPort 입력 포트 번호입니다.
     * @param message 추가할 메세지입니다.
     * @throws InterruptedException 인터럽트가 발생한 경우
     */
    void add(int inputPort, Message message) throws InterruptedException;

}
