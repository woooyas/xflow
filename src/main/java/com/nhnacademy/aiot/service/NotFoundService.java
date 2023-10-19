package com.nhnacademy.aiot.service;

import com.nhnacademy.aiot.pipe.Pipe;

/**
 * 요청에 대한 서비스가 없을 경우 실행되는 서비스입니다.
 */
public class NotFoundService implements Service {

    @Override
    public void execute(Pipe inPipe) {
        //
    }

}
