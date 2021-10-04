package com.yrc.gamecloserservice.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class DeviceDTO {
    String hostname;
    LocalDateTime createdTime;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public DeviceDTO(String hostname) {
        this.hostname = hostname;
        this.createdTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return hostname.equals(deviceDTO.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostname);
    }
}
