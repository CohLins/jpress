package io.jpress.commons.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.List;

public class E3Result implements Serializable{

	 // 定义jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    // 响应业务状态
    private Integer status;

    // 响应消息
    private String msg;
    //响应数据长度
    private Integer total;

    // 响应中的数据
    private Object data;

    public static E3Result build(Integer status, String msg, Object data) {
        return new E3Result(status, msg, data);
    }
    public static E3Result ok(Integer total,Object data) {
        return new E3Result(200,"OK",total,data);
    }
    public static E3Result ok(Object data) {
        return new E3Result(data);
    }

    public static E3Result ok() {
        return new E3Result(null);
    }

    public E3Result() {

    }

    public static E3Result build(Integer status, String msg) {
        return new E3Result(status, msg, null);
    }

    public E3Result(Integer status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    public E3Result(Integer status, String msg, Integer total, Object data) {
        this.status = status;
        this.msg = msg;
        this.total=total;
        this.data = data;
    }

    public E3Result(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

//    public Boolean isOK() {
//        return this.status == 200;
//    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    /**
     * 灏唈son缁撴灉闆嗚浆鍖栦负TaotaoResult瀵硅薄
     * 
     * @param jsonData json鏁版嵁
     * @param clazz TaotaoResult涓殑object绫诲瀷
     * @return
     */
    public static E3Result formatToPojo(String jsonData, Class<?> clazz) {
        try {
            if (clazz == null) {
                return MAPPER.readValue(jsonData, E3Result.class);
            }
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (clazz != null) {
                if (data.isObject()) {
                    obj = MAPPER.readValue(data.traverse(), clazz);
                } else if (data.isTextual()) {
                    obj = MAPPER.readValue(data.asText(), clazz);
                }
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 娌℃湁object瀵硅薄鐨勮浆鍖�
     * 
     * @param json
     * @return
     */
    public static E3Result format(String json) {
        try {
            return MAPPER.readValue(json, E3Result.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Object鏄泦鍚堣浆鍖�
     * 
     * @param jsonData json鏁版嵁
     * @param clazz 闆嗗悎涓殑绫诲瀷
     * @return
     */
    public static E3Result formatToList(String jsonData, Class<?> clazz) {
        try {
            JsonNode jsonNode = MAPPER.readTree(jsonData);
            JsonNode data = jsonNode.get("data");
            Object obj = null;
            if (data.isArray() && data.size() > 0) {
                obj = MAPPER.readValue(data.traverse(),
                        MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            }
            return build(jsonNode.get("status").intValue(), jsonNode.get("msg").asText(), obj);
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
