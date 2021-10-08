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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.yrc.gamecloserservice.config.GameCloserSocketConfig;
import com.yrc.gamecloserservice.pojo.DeviceDTO;
import com.yrc.gamecloserservice.pojo.ProcessResultDTO;
import com.yrc.gamecloserservice.pojo.SendDTO;
import com.yrc.gamecloserservice.service.GameCloserSocketService;
import com.yrc.gamecloserservice.util.JacksonUtils;

@Service
public class GameCloserSocketServiceImpl implements GameCloserSocketService {
    private ServerSocket serverSocket;
    private final Logger logger = LoggerFactory.getLogger(GameCloserSocketServiceImpl.class);
    private final Map<DeviceDTO, Socket> socketMap;
    private final List<ProcessResultDTO> results;
    private final ThreadPoolExecutor threadPool;
    private final static String KEEY_ALIVE_WORD = "关注嘉然，顿顿解馋";
    @Value("${debug}")
    private Boolean debug;

    @Autowired
    public GameCloserSocketServiceImpl(GameCloserSocketConfig config, ThreadPoolExecutor threadPool){
        this.socketMap = new ConcurrentHashMap<>();
        this.threadPool = threadPool;
        this.results = new ArrayList<>();
        try {
            this.serverSocket = new ServerSocket(config.getPort(),100, config.getAddress());
            //监听Socket连接
            threadPool.execute(this::socketListener);
            logger.info("socket服务开启成功: {}:{}", config.getAddress(), config.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void socketListener(){
        try {
            while (true){
                Socket socket = this.serverSocket.accept();
                socket.setSoTimeout(5000);
                String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
                socketMap.put(new DeviceDTO(ipAddress), socket);
                logger.info("连接到socket：{}", ipAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Boolean sendCloseGameMessage(String gameName, String hostname){
        DeviceDTO device = new DeviceDTO(hostname);
        if(socketMap.containsKey(device)){
            Future<Integer> resultCode = threadPool.submit(()
                    -> doSendCloseGameMessage(socketMap.get(device), gameName));
            synchronized (results){
                results.add(new ProcessResultDTO(hostname, gameName, resultCode));
            }
            return true;
        }else {
            return false;
        }
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void keepAlive(){
        if(socketMap.size() > 0){
            logger.debug("开始发送心跳包");
            socketMap.forEach((key, value) ->
                    threadPool.submit(() -> doSendCloseGameMessage(
                            value,
                            Objects.requireNonNull(JacksonUtils.serialize(SendDTO.keepAliveObject())),
                            debug)));
        }
    }

    @Override
    public List<DeviceDTO> listSockets() {
        socketMap.forEach((key, value) -> {
            if (Boolean.TRUE.equals(isClosed(value))) {
                logger.info("无法连接到 {} ，将关闭连接", value);
                try {
                    value.close();
                } catch (IOException ex) {
                    logger.info("关闭失败");
                }
                socketMap.remove(key);
            }
        });
        return new ArrayList<>(socketMap.keySet());
    }

    @Override
    public List<ProcessResultDTO> listResults() {
        return results;
    }

    private Integer doSendCloseGameMessage(Socket socket, String message){
        return doSendCloseGameMessage(socket, message, true);
    }
    private Integer doSendCloseGameMessage(Socket socket, String message, Boolean printLog){
        String ipAddress = socket.getRemoteSocketAddress().toString().substring(1);
        Integer resultCode = 0;
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            Reader reader = new InputStreamReader(socket.getInputStream());
            writer.write(new String(message.getBytes(), StandardCharsets.UTF_8));
            writer.flush();
            if(Boolean.TRUE.equals(printLog)){
                logger.info("发送进程名 {} 到 {}", message, ipAddress);
            }

            BufferedReader br = new BufferedReader(reader);
            String result = br.readLine();
            if(Boolean.TRUE.equals(printLog)){
                logger.info("{} 执行 {} 结果：{}",ipAddress, message, result);
            }
            resultCode = Integer.valueOf(result);
        } catch (IOException e) {
            logger.info("io错误，将关闭{}", socket);
            try {
                socket.close();
            } catch (IOException ex) {
                logger.info("关闭失败");
            }
            socketMap.remove(new DeviceDTO(ipAddress));
            resultCode = -2;
        }
        return resultCode;
    }
    private Boolean isClosed(Socket socket){
        try {
            socket.sendUrgentData(0);
        } catch (IOException e) {
            return true;
        }
        return false;
    }
    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }
}
