package com.yrc.gamecloserservice.service.impl;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.stream.Collectors;
import javax.el.BeanNameELResolver;
import javax.naming.ldap.SortKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
    private final Map<DeviceDTO, Integer> keepAliveMap;
    private final Map<String, ProcessResultDTO> resultMap;
    private final ThreadPoolExecutor threadPool;

    @Autowired
    public GameCloserSocketServiceImpl(GameCloserSocketConfig config, ThreadPoolExecutor threadPool){
        this.socketMap = new ConcurrentHashMap<>();
        this.keepAliveMap = new ConcurrentHashMap<>();
        this.threadPool = threadPool;
        this.resultMap = Collections.synchronizedMap(new LinkedHashMap<>());
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
                String ipAddress = getHostnameBySocket(socket);
                addSocket(socket);
                logger.info("连接到socket：{}", ipAddress);
                threadPool.execute(() -> receiveMessage(socket, true));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Boolean sendCloseGameMessage(String gameName, String hostname){
        DeviceDTO device = new DeviceDTO(hostname);
        if(socketMap.containsKey(device)){
            SendDTO message = SendDTO.closeGameObject(UUID.randomUUID().toString(), hostname, gameName);
            ProcessResultDTO result = new ProcessResultDTO();
            BeanUtils.copyProperties(message, result);
            threadPool.execute(() -> sendMessage(socketMap.get(device),message));
            resultMap.put(message.getGuid(), result);
            return true;
        }else {
            return false;
        }
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void keepAlive(){
        if(socketMap.size() > 0){
            logger.debug("开始发送心跳包");
            for (Map.Entry<DeviceDTO, Socket> entry : socketMap.entrySet()) {
                if(isClosed(entry.getValue())){
                    logger.info("无法连接到 {} ，将关闭连接", entry.getValue());
                    try {
                        entry.getValue().close();
                    } catch (IOException ex) {
                        logger.info("关闭失败");
                    }
                    removeSocket(entry.getValue());
                }
            }
            socketMap.forEach((key, value) ->{
                    threadPool.execute(() -> sendMessage(
                            value,
                            SendDTO.keepAliveObject(key.getHostname()),
                            true
                    ));
                    Integer count = keepAliveMap.get(key);
                    keepAliveMap.put(key, count+1);
                }
            );
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
                removeSocket(value);
            }
        });
        return new ArrayList<>(socketMap.keySet());
    }

    @Override
    public List<ProcessResultDTO> listResults() {
        return resultMap.values().stream()
                .sorted(Comparator.comparing(ProcessResultDTO::getLocalDateTime).reversed())
                .collect(Collectors.toList());
    }

    private void sendMessage(Socket socket, SendDTO message){
        sendMessage(socket, message, true);
    }
    private void sendMessage(Socket socket, SendDTO message, Boolean printLog){
        String ipAddress = getHostnameBySocket(socket);
        String sendMessage = JacksonUtils.serialize(message);
        try {
            Writer writer = new OutputStreamWriter(socket.getOutputStream());
            writer.write(new String(sendMessage.getBytes(), StandardCharsets.UTF_8));
            writer.flush();
            if(Boolean.TRUE.equals(printLog)){
                if(message.getType().equals(SendDTO.Type.EXEC)){
                    logger.info("发送进程名 {} 到 {}", message.getGameName(), ipAddress);
                }else if(message.getType().equals(SendDTO.Type.KEEY_ALIVE)){
                    logger.debug("发送心跳包 到 {}", ipAddress);
                }
            }

        } catch (IOException e) {
            logger.info("io错误，将关闭{}", socket);
            try {
                socket.close();
            } catch (IOException ex) {
                logger.info("关闭失败");
            }
            removeSocket(socket);
        }
    }
    private void addSocket(Socket socket){
        DeviceDTO device = new DeviceDTO(getHostnameBySocket(socket));
        socketMap.put(device, socket);
        keepAliveMap.put(device, 0);
    }
    private void removeSocket(Socket socket){
        socketMap.remove(new DeviceDTO(getHostnameBySocket(socket)));
        keepAliveMap.remove(new DeviceDTO(getHostnameBySocket(socket)));
    }
    private void receiveMessage(Socket socket,Boolean printLog){
        logger.debug("{} 开始接收", socket);
        while (true){
            try {
                Reader reader = new InputStreamReader(socket.getInputStream());
                BufferedReader br = new BufferedReader(reader);
                String result = br.readLine();

                SendDTO message = JacksonUtils.deserialize(result, SendDTO.class);
                if(message.getType().equals(SendDTO.Type.KEEY_ALIVE)){
                    keepAliveMap.replace(new DeviceDTO(message.getHostname()), 0);
                    logger.debug("重置过期次数：{} 为 {}", message.getHostname(), 0);
                }else if (message.getType().equals(SendDTO.Type.EXEC)){

                }else if (message.getType().equals(SendDTO.Type.RESULT)){
                    ProcessResultDTO resultDTO = new ProcessResultDTO();
                    BeanUtils.copyProperties(message, resultDTO);
                    if(Boolean.TRUE.equals(printLog)){
                        logger.info("{} 执行 {} 结果：{}",resultDTO.getHostname(), resultDTO.getGameName(), resultDTO.getResultCode());
                    }
                    if (resultMap.containsKey(resultDTO.getGuid())){
                        resultMap.replace(resultDTO.getGuid(), resultDTO);
                    } else {
                        logger.info("不在记录中的执行结果：{}", resultDTO);
                    }
                }
            } catch (IOException e) {
                logger.info("io错误，将关闭{}", socket);
                try {
                    socket.close();
                } catch (IOException ex) {
                    logger.info("关闭失败");
                }
                removeSocket(socket);
                break;
            }
        }
    }
    private Boolean isClosed(Socket socket){
        return keepAliveMap.get(new DeviceDTO(getHostnameBySocket(socket))) >= 2;
    }
    private String getHostnameBySocket(Socket socket){
        return socket.getRemoteSocketAddress().toString().substring(1);
    }
}
