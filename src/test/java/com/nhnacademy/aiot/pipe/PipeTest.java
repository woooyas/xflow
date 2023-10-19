package com.nhnacademy.aiot.pipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.nhnacademy.aiot.message.Message;

class PipeTest {

    @Test
    void 메세지를_넣은_순서대로_메세지를_꺼낸다() throws InterruptedException {
        // given
        Pipe pipe = new Pipe();

        for (int i = 0; i < 10; i++) {
            Message message = new Message(i, 1_000);
            message.put("value", i);
            pipe.put(message);
        }

        for (int i = 9; i >= 0; i--) {
            // when
            Message message = pipe.get();

            // then
            assertEquals(i, message.getInt("value"));
        }
    }

    @Test
    @SuppressWarnings("squid:S2925")
    void 메세지가_만료됐으면_다음_유효한_메세지를_꺼낸다() throws InterruptedException {
        // given
        Pipe pipe = new Pipe();
        Message expiredMessage = new Message(0);
        Message nextMessage = new Message(1_000);

        pipe.put(expiredMessage);
        pipe.put(nextMessage);

        Thread.sleep(1);

        // when
        // then
        assertEquals(nextMessage, pipe.get());
    }

    @Test
    void 파이프가_비었으면_true를_반환한다() {
        // given
        Pipe pipe = new Pipe();

        // when
        // then
        assertTrue(pipe.isEmpty());
    }

}
