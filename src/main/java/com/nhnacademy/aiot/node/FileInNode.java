package com.nhnacademy.aiot.node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;
import lombok.extern.slf4j.Slf4j;

/**
 * 파일을 읽는 노드입니다.
 * <ul>
 * <li><strong>입 력</strong>
 * <ul>
 * <li>메세지 객체
 * </ul>
 * <li><strong>출 력</strong>
 * <ul>
 * <li>"file" : (String)파일 내용 문자열
 * </ul>
 * </ul>
 */
@Slf4j
public class FileInNode implements InputNode, OutputNode {

    private static final String FILE_IO_EXCEPTION = "파일 입출력 중 문제가 발생했습니다.";
    private static final String FILE_NOT_FOUND = "파일을 찾을 수 없습니다.";
    private static final String NODE_STARTED = "파일 입력 노드가 시작되었습니다.";
    private static final String NODE_STOPPED = "파일 입력 노드가 종료되었습니다.";

    private final Thread thread;
    private Pipe inPipe;
    private final Map<Integer, Pipe> outPipes;
    private final File file;

    public FileInNode(String fileName) {
        thread = new Thread(this, "FileInNode");
        outPipes = new HashMap<>();
        file = new File(fileName);
    }

    @Override
    public void start() {
        thread.start();
    }

    @Override
    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        preprocess();
        while (!Thread.interrupted()) {
            process();
        }
        postprocess();
    }

    private void preprocess() {
        log.debug(NODE_STARTED);
    }

    private void process() {
        try {
            Message message = inPipe.get();
            FileReader fileReader = new FileReader(file);
            char[] data = new char[(int) file.length()];
            fileReader.read(data);
            fileReader.close();
            message.put("file", new String(data));
            putMessageInPipes(message);
        } catch (InterruptedException ignore) {
            thread.interrupt();
        } catch (FileNotFoundException e) {
            log.error(FILE_NOT_FOUND, e);
        } catch (IOException e) {
            log.error(FILE_IO_EXCEPTION, e);
        }
    }

    private void putMessageInPipes(Message message) throws InterruptedException {
        for (Pipe pipe : outPipes.values()) {
            pipe.put(message);
        }
    }

    private void postprocess() {
        inPipe = null;
        outPipes.clear();
        log.debug(NODE_STOPPED);
    }

    @Override
    public void connectIn(int port, Pipe inPipe) {
        this.inPipe = inPipe;
    }

    @Override
    public void connectOut(int port, Pipe outPipe) {
        outPipes.put(port, outPipe);
    }

}
