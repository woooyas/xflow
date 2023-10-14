package com.nhnacademy.aiot.test;

import com.nhnacademy.aiot.node.InputNode;
import com.nhnacademy.aiot.node.OutputNode;
import com.nhnacademy.aiot.pipe.Pipe;

class Main {

    public static void main(String[] args) {
        Pipe pipe = new Pipe();
        InputNode inNode = new InNode();
        InputNode inNode2 = new InNode();
        OutputNode outNode = new OutNode();
        OutputNode outNode2 = new OutNode();

        inNode.connectIn(0, pipe);
        inNode2.connectIn(1, pipe);
        outNode.connectOut(0, pipe);
        outNode2.connectOut(1, pipe);

        inNode.start();
        inNode2.start();
        outNode.start();
        outNode2.start();
    }

}
