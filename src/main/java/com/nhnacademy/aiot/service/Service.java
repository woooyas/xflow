package com.nhnacademy.aiot.service;

import com.nhnacademy.aiot.pipe.Pipe;

/**
 * 서비스 인터페이스입니다.
 */
public interface Service {

    /**
     * 서비스를 실행합니다.
     * 
     * @param inPipe 메세지가 들어오는 파이프입니다.
     */
    void execute(Pipe inPipe);

}
