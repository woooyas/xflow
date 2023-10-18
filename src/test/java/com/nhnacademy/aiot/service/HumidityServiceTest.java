package com.nhnacademy.aiot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

class HumidityServiceTest {
    @Test
    void 습도_받기_테스트() {
        String regualr = "[{\"time\":\"" + "^[\\d]{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$" + "T"+ 
                    "^(\\d{2}):(\\d{2}):(\\d{2}\\.\\d{3})$Z\",\"value\":"+ "^(\\d+(\\.\\d*)?|\\.\\d+)$" + ",\"24e124126c457594\":\"\"}]";
        try (ServerSocket serverSocket = new ServerSocket(1880)) {
            Pipe pipe = new Pipe();
            HumidityService humidityService = new HumidityService();
            humidityService.execute(pipe);

            Thread thread = new Thread(() -> {
                try (Socket socket = new Socket("localhost", 1880)){
                    InputStream inputStream = socket.getInputStream();

                    byte[] buffer = new byte[1_000];
                    int length = inputStream.read(buffer);
                    
                    String string = new String(buffer, 0, length);
                    assertEquals(regualr, string);
                    

                } catch (IOException ignore) {
                }
                
            });
            thread.start();

            Message message = new Message(60_000);
            message.put("socket", serverSocket.accept());
            message.put("request", "GET /humidity HTTP/1.1");
            pipe.put(message);

            thread.join();

        } catch (IOException ignore) {
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
