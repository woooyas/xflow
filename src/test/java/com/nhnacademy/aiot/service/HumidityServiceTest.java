package com.nhnacademy.aiot.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.nhnacademy.aiot.message.Message;
import com.nhnacademy.aiot.pipe.Pipe;

class HumidityServiceTest {

    String string;

    @AfterEach
    void 초기화() {
        string = "";
    }

    @Test
    void 습도_받기_테스트() {
        try (ServerSocket serverSocket = new ServerSocket(1880)) {
            Pipe pipe = new Pipe();
            HumidityService humidityService = new HumidityService();
            humidityService.execute(pipe);

            Thread thread = new Thread(() -> {
                try (Socket socket = new Socket("localhost", 1880)) {
                    InputStream inputStream = socket.getInputStream();

                    byte[] buffer = new byte[10_000];
                    int length = inputStream.read(buffer);

                    string = new String(buffer, 0, length);

                } catch (IOException ignore) {
                }

            });
            thread.start();

            Message message = new Message(60_000);
            message.put("socket", serverSocket.accept());
            message.put("request", "GET /humidity HTTP/1.1");
            pipe.put(message);

            thread.join();
            assertTrue(string.matches("HTTP/1\\.1 200 OK\r\n" //
                    + "Content-Type: application/json; charset=utf-8\r\n" //
                    + "Content-Length: .*" + "\r\n\r\n" +
                    "\\{\"dateTime\":\".*\",\"humidity\":.*\\}"));

        } catch (IOException ignore) {
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}
