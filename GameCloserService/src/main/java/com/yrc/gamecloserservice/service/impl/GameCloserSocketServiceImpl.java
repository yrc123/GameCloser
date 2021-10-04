package com.yrc.gamecloserservice.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yrc.gamecloserservice.config.GameCloserSocketConfig;
import com.yrc.gamecloserservice.pojo.ProcessResultDTO;
import com.yrc.gamecloserservice.service.GameCloserSocketService;


@Service
public class GameCloserSocketServiceImpl implements GameCloserSocketService {
    private Logger logger = LoggerFactory.getLogger(GameCloserSocketServiceImpl.class);
    private ServerSocket serverSocket;
    private final Map<String, Socket> socketMap;
    private final List<ProcessResultDTO> results;
    private ThreadPoolExecutor threadPool;
    @Autowired
    public GameCloserSocketServiceImpl(GameCloserSocketConfig config, ThreadPoolExecutor threadPool){
        this.socketMap = new ConcurrentHashMap<>();
        this.threadPool = threadPool;
        this.results = new ArrayList<>();
        try {
            this.serverSocket = new ServerSocket(config.getPort(),100, config.getAddress());
            //监听Socket连接
            Thread listener = new Thread(this::SocketListener);
            listener.setDaemon(true);
            listener.setName("SocketListener");
            threadPool.execute(listener);
            logger.info("socket服务开启成功: {}:{}", config.getAddress(), config.getPort());
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
                logger.info("连接到socket：{}", ipAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendCloseGameMessage(String gameName, String hostname){
        // for (String key : socketMap.keySet()) {
        //     Socket socket = socketMap.get(key);
        //     String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
        //     if (StringUtils.equals(ipAddress, hostname)){
        //         threadPool.submit(() -> doSendCloseGameMessage(socket, gameName));
        //     }
        // }
        if(socketMap.containsKey(hostname)){
            Future<Integer> resultCode = threadPool.submit(()
                    -> doSendCloseGameMessage(socketMap.get(hostname), gameName));
            results.add(new ProcessResultDTO(hostname, resultCode));
        }
    }

    @Override
    public List<String> listSockets() {
        return new ArrayList<>(socketMap.keySet());
    }

    @Override
    public List<ProcessResultDTO> listResults() {
        return results;
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
