package io.jpress.service;

import com.jfinal.plugin.activerecord.Model;
import io.jpress.model.PersonalLetter;

import java.util.List;

public interface PersonalLetterService {
    /**
     * 发私信
     */
    public Object sendLetter(PersonalLetter model);
    /**
     *
     */
    public Object saveOrUpdate(PersonalLetter model);
    /**
     * 私信记录展示
     *
     */
    public List<PersonalLetter> letterRecordShow(String chatId,String orderBy,Long userId);
    /**
     * 根据聊天ID和本人查一条数据
     */
    public PersonalLetter letterOnly(String chatId,Long userId,Long acceptId);

    /**
     * 个人收到的所有私信记录
     */
    public List<PersonalLetter> letterPersonal(String chatId,String accept);
    /**
     * 私信列表展示
     */
    public List<PersonalLetter> letterListShow(String acceptId,Integer tag);
    /**
     * 未读私信数量 所有
     */
    public Integer unReadLetter(String acceptId);

    /**
     * 根据聊天ID删除私信列表
     */
    public boolean deleteId(Object chatId,Long userId);
    /**
     * 根据私信ID删除一条私信
     */
    public boolean deleteOnly(Object Id,Long userId);
    /**
     * 根据聊天ID查询是否已存在聊天
     */
    public boolean firstByChatId(String chatId);

}
