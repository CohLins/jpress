package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BasePersonalLetter;

@Table(tableName = "personal_letter", primaryKey = "id")
public class PersonalLetter extends BasePersonalLetter<PersonalLetter>{
    private  String LETTER_UNREAD_STATUS="unread";
    private  String LETTER_READ_STATUS="read";
    //面板消息数量
    private Integer panelMessageNum;
    private String sendName;
    private String acceptName;
    private String sendImg;
    private String acceptImg;


    public Integer getPanelMessageNum() {
        return panelMessageNum;
    }

    public void setPanelMessageNum(Integer panelMessageNum) {
        this.panelMessageNum = panelMessageNum;
    }

    public String getSendName() {
        return sendName;
    }

    public void setSendName(String sendName) {
        this.sendName = sendName;
    }

    public String getAcceptName() {
        return acceptName;
    }

    public void setAcceptName(String acceptName) {
        this.acceptName = acceptName;
    }

    public String getSendImg() {
        return sendImg;
    }

    public void setSendImg(String sendImg) {
        this.sendImg = sendImg;
    }

    public String getAcceptImg() {
        return acceptImg;
    }

    public void setAcceptImg(String acceptImg) {
        this.acceptImg = acceptImg;
    }
}
