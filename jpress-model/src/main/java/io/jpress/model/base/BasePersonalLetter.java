package io.jpress.model.base;

import com.jfinal.plugin.activerecord.IBean;
import io.jboot.db.model.JbootModel;

@SuppressWarnings("serial")
public abstract class BasePersonalLetter<M extends BasePersonalLetter<M>> extends JbootModel<M> implements IBean {

    private static final long serialVersionUID = 1L;

    /**
     * ID主键
     */
    public void setId(java.lang.Long id) {
        set("id", id);
    }

    /**
     * ID主键
     */
    public java.lang.Long getId() {
        return getLong("id");
    }

    /**
     * 聊天的id
     */
    public void setChatId(java.lang.String chatId) {
        set("chat_id", chatId);
    }

    /**
     * 聊天的id
     */
    public java.lang.String getChatId() {
        return getStr("chat_id");
    }

    /**
     * 发送者Id
     */
    public void setSendId(java.lang.Long sendId) {
        set("send_id", sendId);
    }

    /**
     * 发送者ID
     */
    public java.lang.Long getSendId() {
        return getLong("send_id");
    }
    /**
     * 接受者Id
     */
    public void setAcceptId(java.lang.Long acceptId) {
        set("accept_id", acceptId);
    }

    /**
     * 接受者ID
     */
    public java.lang.Long getAcceptId() {
        return getLong("accept_id");
    }
    /**
     * 内容
     */
    public void setContent(java.lang.String content) {
        set("content", content);
    }

    /**
     * 内容
     */
    public java.lang.String getContent() {
        return getStr("content");
    }

    /**
     * 内容
     */
    public void setPicUrl(java.lang.String picUrl) {
        set("pic_url", picUrl);
    }

    /**
     * 内容
     */
    public java.lang.String getPicUrl() {
        return getStr("pic_url");
    }


    /**
     * 状态
     */
    public void setStatus(java.lang.String status) {
        set("status", status);
    }

    /**
     * 状态
     */
    public java.lang.String getStatus() {
        return getStr("status");
    }

    /**
     * 发送者状态
     */
    public void setSendStatus(java.lang.String sendStatus) {
        set("send_status", sendStatus);
    }

    /**
     * 发送者状态
     */
    public java.lang.String getSendStatus() {
        return getStr("send_status");
    }
    /**
     * 接受者状态
     */
    public void setAcceptStatus(java.lang.String acceptStatus) {
        set("accept_status", acceptStatus);
    }

    /**
     * 接受者状态
     */
    public java.lang.String getAcceptStatus() {
        return getStr("accept_status");
    }

    /**
     * 时间
     */
    public void setCreateTime(java.util.Date createTime) {
        set("create_time", createTime);
    }

    /**
     * 时间
     */
    public java.util.Date getCreateTime() {
        return getDate("create_time");
    }
}
