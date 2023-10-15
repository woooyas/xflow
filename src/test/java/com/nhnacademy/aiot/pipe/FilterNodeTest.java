package com.nhnacademy.aiot.pipe;

import static org.junit.jupiter.api.Assertions.*;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.node.FilterNode;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class FilterNodeTest {

    @Test
    public void testFilterNode() throws InterruptedException {
        // 조건: Message의 문자열 길이가 5보다 큰 메시지만 전달하는 필터 노드를 생성
        Predicate<Message> condition = message -> message.toString().length() > 5;
        FilterNode filterNode = new FilterNode(condition);

        // 입력 파이프와 출력 파이프 생성
        Pipe inputPipe = new Pipe();
        Pipe outputPipe = new Pipe();

        // 입력 파이프와 출력 파이프를 필터 노드에 연결
        filterNode.connectIn(0, inputPipe);
        filterNode.connectOut(0, outputPipe);

        // 필터 노드 실행
        filterNode.start();

        // 메시지 전송
        inputPipe.put(new Message(1000));

        Message message = outputPipe.get();

        assertTrue(!message.isEmpty());

        // 필터 노드 중지
        filterNode.stop();
    }
}