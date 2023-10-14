package com.nhnacademy.aiot.message;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class MessageTest {

    @Test
    @SuppressWarnings("squid:S2925")
    void 설정한_유효시간이_지나면_메세지가_만료된다() throws InterruptedException {
        // given
        Message message = new Message(0);

        Thread.sleep(1);

        // when
        // then
        assertTrue(message.isExpired());
    }

}
