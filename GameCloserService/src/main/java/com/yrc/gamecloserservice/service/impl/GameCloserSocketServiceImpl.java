package com.yrc.gamecloserservice.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrc.gamecloserservice.config.GameCloserSocketConfig;
import com.yrc.gamecloserservice.service.GameCloserSocketService;

@Service
public class GameCloserSocketServiceImpl implements GameCloserSocketService {
    private Logger logger = LoggerFactory.getLogger(GameCloserSocketServiceImpl.class);
    private ServerSocket serverSocket;
    private final Map<String, Socket> socketMap;
    private ThreadPoolExecutor threadPool;
    @Autowired
    public GameCloserSocketServiceImpl(GameCloserSocketConfig config, ThreadPoolExecutor threadPool){
        this.socketMap = new ConcurrentHashMap<>();
        this.threadPool = threadPool;
        try {
            this.serverSocket = new ServerSocket(config.getPort());
            //监听Socket连接
            Thread listener = new Thread(this::SocketListener);
            listener.setDaemon(true);
            listener.setName("SocketListener");
            threadPool.execute(listener);
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
    public void sendCloseGameMessage(String gameName){
        for (String key : socketMap.keySet()) {
            Socket socket = socketMap.get(key);
            String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
            threadPool.submit(() -> doSendCloseGameMessage(socket, gameName));
        }
    }
    private Integer doSendCloseGameMessage(Socket socket, String message){
        String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
        Integer resultCode = 0;
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            Reader reader = new InputStreamReader(socket.getInputStream());
            writer.write(new String(message.getBytes(), StandardCharsets.UTF_8));
            writer.flush();
            logger.info("发送进程名 {} 到 {}", message, ipAddress);

            BufferedReader br = new BufferedReader(reader);
            String result = br.readLine();
            logger.info("{} 执行 {} 结果：{}",ipAddress, message, result);
            resultCode = Integer.valueOf(result);
        } catch (IOException e) {
            logger.info("io错误，将关闭Socket");
            socketMap.remove(ipAddress);
            resultCode = -2;
        }
        return resultCode;
    }
}
