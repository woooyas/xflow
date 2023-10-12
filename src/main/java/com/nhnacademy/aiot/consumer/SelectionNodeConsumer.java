package com.nhnacademy.aiot.consumer;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.node.SelectionNode;

@FunctionalInterface
public interface SelectionNodeConsumer {
    
    void consume(Message message, SelectionNode selectionNode);

}