package com.yrc.gamecloserservice.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yrc.gamecloserservice.config.GameCloserSocketConfig;

@Service
public class GameCloserSocketService {
    private Logger logger = LoggerFactory.getLogger(GameCloserSocketService.class);
    private ServerSocket serverSocket;
    private Map<String, Socket> socketMap;
    @Autowired
    public GameCloserSocketService(GameCloserSocketConfig config){
        this.socketMap = new HashMap<>();
        try {
            this.serverSocket = new ServerSocket(config.getPort());
            Thread listener = new Thread(this::SocketListener);
            listener.setDaemon(true);
            listener.setName("SocketListener");
            listener.start();
            logger.info("socket服务开启成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void SocketListener(){
        try {
            while (true){
                Socket socket = this.serverSocket.accept();
                String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
                socketMap.put(ipAddress, socket);
                logger.info("连接到socke：{}", ipAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendCloseGameMessage(String gameName){
        for (String key : socketMap.keySet()) {
            Socket socket = socketMap.get(key);
            String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
            if(socket.isClosed()){
                socketMap.remove(key);
            }else{
                try {
                    Writer writer = new OutputStreamWriter(socket.getOutputStream());
                    writer.write(new String(gameName.getBytes(), StandardCharsets.UTF_8));
                    writer.flush();
                    writer.close();
                    logger.info("发送进程名 {} 到 {}", gameName, ipAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
