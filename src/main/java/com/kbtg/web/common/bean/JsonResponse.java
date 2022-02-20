package com.kbtg.web.common.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class JsonResponse<T> implements Serializable {

    private Map<String, T> data;

    public JsonResponse() {
        this.data = new HashMap<>();
    }

    public JsonResponse(Map<String, T> data) {
        this.data = data;
    }

    public T get(String key) {
        return this.data.get(key);
    }

    public void put(String key, T value) {
        this.data.put(key, value);
    }

    public void putSuccess() {
        this.data.put("status", (T) "ok");
    }

    public void putError(String errorCode, String errorDescription) {
        this.data.put("status", (T) "error");
        this.data.put("error_code", (T) errorCode);
        this.data.put("error_description", (T) errorDescription);
    }

    public void putErrorJsonFormat(String errorDescription) {
        this.putError("JSON_ERROR", "กรุณาตรวจสอบ format JSON : " + errorDescription);
    }

    public void putErrorInternalServer(String errorDescription) {
        this.putError("INTERNAL_SERVER_ERROR", errorDescription);
    }
}
