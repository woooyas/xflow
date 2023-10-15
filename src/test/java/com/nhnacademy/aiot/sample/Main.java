package com.nhnacademy.aiot.sample;

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

        inNode.connectOut(0, pipe);
        inNode2.connectOut(0, pipe);
        outNode.connectIn(0, pipe);
        outNode2.connectIn(0, pipe);

        inNode.start();
        inNode2.start();
        outNode.start();
        outNode2.start();
    }

}
