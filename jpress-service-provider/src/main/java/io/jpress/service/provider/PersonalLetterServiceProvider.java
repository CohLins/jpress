package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Permission;
import io.jpress.model.PersonalLetter;
import io.jpress.service.PermissionService;
import io.jpress.service.PersonalLetterService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Bean
public class PersonalLetterServiceProvider extends JbootServiceBase<PersonalLetter> implements PersonalLetterService {
    @Inject
    private PersonalLetterService personalLetterService;
    @Override
    public Object sendLetter(PersonalLetter model) {
        return super.save(model);
    }

    @Override
    public List<PersonalLetter> letterRecordShow(String chatId,String orderBy,Long userId) {
        System.out.println(chatId);
        if(orderBy==null||orderBy==""){
            Columns column=new Columns();
            column.eq("chat_id",chatId);
            List<PersonalLetter> letters = new ArrayList<>();
            List<PersonalLetter> personalLetterPage = DAO.findListByColumns(column, "create_time asc");
            for (PersonalLetter letter:personalLetterPage){
                if (letter.getSendId().equals(userId)&&letter.getSendStatus().equals("normal")||
                        letter.getAcceptId().equals(userId)&&letter.getAcceptStatus().equals("normal")){
                    letters.add(letter);
                }
            }
            return letters;
        }else {
            Columns column=new Columns();
            column.eq("chat_id",chatId);
            List<PersonalLetter> personalLetterPage = DAO.findListByColumns(column, orderBy);
            return personalLetterPage;
        }
    }
    @Override
    public PersonalLetter letterOnly(String chatId,Long sendId,Long acceptId){
        Columns columns = new Columns();
        columns.eq("chat_id",chatId);
        if (sendId!=null){
            columns.eq("send_id",sendId);
        }
        if (acceptId!=null){
            columns.eq("accept_id",acceptId);
        }
        return DAO.findFirstByColumns(columns);
    }
    @Override
    public List<PersonalLetter> letterPersonal(String chatId,String accept) {
        Columns column=new Columns();
        column.eq("chat_id",chatId);
        column.eq("accept_id",accept);
        List<PersonalLetter> personalLetterPage = DAO.findListByColumns(column);
        return personalLetterPage;

    }

    @Override
    public List<PersonalLetter> letterListShow(String acceptId,Integer tag) {
        Columns columns=new Columns();
        columns.eq("accept_id",acceptId);
        List<PersonalLetter> letters = DAO.findListByColumns(columns, "create_time desc");
        if (letters.size()<=0){
            return null;
        }
        //去重 去掉重复的聊天Id
        ArrayList<PersonalLetter> collect = letters.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(o
                -> o.getChatId()))), ArrayList::new));
        //根据获取到的聊天Id 再次根据时间排序获取队列第一条
        List<PersonalLetter> personalLetterList = new ArrayList<>();
        Columns column;
        for (PersonalLetter personalLetter:collect){
            //根据标记查找对应私信列表
//            0:全部信息  1:未读信息  2:已读信息
            switch (tag){
                case 0:
                    column=new Columns();
                    column.eq("chat_id",personalLetter.getChatId());
                    //找到聊天ID内最晚的一条消息
                    List<PersonalLetter> letters1 =DAO.findListByColumns(column,"create_time desc");
                    for (PersonalLetter letter:letters1){
                        if(String.valueOf(letter.getSendId()).equals(acceptId)&&letter.getSendStatus().equals("normal")||
                                String.valueOf(letter.getAcceptId()).equals(acceptId)&&letter.getAcceptStatus().equals("normal") ){
                            column.eq("accept_id",acceptId);
                            column.eq("status","unread");
                            column.eq("accept_status","normal");
                            letter.setPanelMessageNum(DAO.findListByColumns(column).size());
                            personalLetterList.add(letter);
                            break;
                        }
                    }
                    //然后找到聊天ID内对应的我的未读消息数量

                    break;
                case 1:
                    column=new Columns();
                    column.eq("chat_id",personalLetter.getChatId());
                    column.eq("accept_id",acceptId);
                    column.eq("status","unread");
                    column.eq("accept_status","normal");
                    int size = DAO.findListByColumns(column).size();

                    if (size>0){
                        column=new Columns();
                        column.eq("chat_id",personalLetter.getChatId());
                        //找到聊天ID内最晚的一条消息
                        List<PersonalLetter> letters3 =DAO.findListByColumns(column,"create_time desc");
                        for (PersonalLetter personalLetter1:letters3){
                            if(String.valueOf(personalLetter1.getSendId()).equals(acceptId)&&personalLetter1.getSendStatus().equals("normal")||
                                    String.valueOf(personalLetter1.getAcceptId()).equals(acceptId)&&personalLetter1.getAcceptStatus().equals("normal") ) {
                                personalLetter1.setPanelMessageNum(size);
                                personalLetterList.add(personalLetter1);
                                break;
                            }
                        }
                    }
                    break;
                case 2:
                    column=new Columns();
                    column.eq("chat_id",personalLetter.getChatId());
                    column.eq("accept_id",acceptId);
                    column.eq("status","unread");
                    column.eq("accept_status","normal");
                    int num = DAO.findListByColumns(column).size();
                    if (num<=0){
                        column=new Columns();
                        column.eq("chat_id",personalLetter.getChatId());
                        //找到聊天ID内最晚的一条消息
                        List<PersonalLetter> letters2 =DAO.findListByColumns(column,"create_time desc");
                        for (PersonalLetter personalLetter1:letters2){
                            if(String.valueOf(personalLetter1.getSendId()).equals(acceptId)&&personalLetter1.getSendStatus().equals("normal")||
                                    String.valueOf(personalLetter1.getAcceptId()).equals(acceptId)&&personalLetter1.getAcceptStatus().equals("normal") ) {
                                personalLetter1.setPanelMessageNum(num);
                                personalLetterList.add(personalLetter1);
                                break;
                            }
                        }
                    }
                    break;
                default:
                    column=new Columns();
                    column.eq("chat_id",personalLetter.getChatId());
                    //找到聊天ID内最晚的一条消息
                    List<PersonalLetter> personalLetterList1 =DAO.findListByColumns(column,"create_time desc");
                    for (PersonalLetter letter:personalLetterList1){
                        if(String.valueOf(letter.getSendId()).equals(acceptId)&&letter.getSendStatus().equals("normal")||
                                String.valueOf(letter.getAcceptId()).equals(acceptId)&&letter.getAcceptStatus().equals("normal") ){
                            column.eq("accept_id",acceptId);
                            column.eq("status","unread");
                            column.eq("accept_status","normal");
                            letter.setPanelMessageNum(DAO.findListByColumns(column).size());
                            personalLetterList.add(letter);
                            break;
                        }
                    }
                    break;
            }
        }
        return personalLetterList;
    }

    @Override
    public Integer unReadLetter(String acceptId) {
        Columns column=new Columns();
        column.eq("accept_id",acceptId);
        column.eq("status","unread");
        column.eq("accept_status","normal");
        List<PersonalLetter> listByColumns = DAO.findListByColumns(column);
        return listByColumns.size();
    }
    @Override
    public boolean deleteId(Object chatId,Long userId){
        //将聊天Id中 自己的状态设置为dash
        //发送者状态
        Columns column=new Columns();
        column.eq("chat_id",chatId);
        column.eq("send_id",userId);
        List<PersonalLetter> listByColumns = DAO.findListByColumns(column);
        for (PersonalLetter personalLetter:listByColumns){
            personalLetter.setSendStatus("dash");
            personalLetterService.saveOrUpdate(personalLetter);
        }
        //接受者状态
        Columns column1=new Columns();
        column1.eq("chat_id",chatId);
        column1.eq("accept_id",userId);
        List<PersonalLetter> listByColumns1 = DAO.findListByColumns(column1);
        for (PersonalLetter personalLetter:listByColumns1){
            personalLetter.setAcceptStatus("dash");
            personalLetterService.saveOrUpdate(personalLetter);
        }
        return true;
    }
    @Override
    public boolean deleteOnly(Object id,Long userId){
        //将聊天Id中 自己的状态设置为dash
        //发送者状态
        Columns column=new Columns();
        column.eq("id",id);
        column.eq("send_id",userId);
        List<PersonalLetter> listByColumns = DAO.findListByColumns(column);
        if (listByColumns.size()>0){
            for (PersonalLetter personalLetter:listByColumns){
                personalLetter.setSendStatus("dash");
                personalLetterService.saveOrUpdate(personalLetter);
            }
        }
        //接受者状态
        Columns column1=new Columns();
        column1.eq("id",id);
        column1.eq("accept_id",userId);
        List<PersonalLetter> listByColumns1 = DAO.findListByColumns(column1);
        if (listByColumns1.size()>0){
            for (PersonalLetter personalLetter:listByColumns1){
                personalLetter.setAcceptStatus("dash");
                personalLetterService.saveOrUpdate(personalLetter);
            }
        }
        return true;
    }

    @Override
    public boolean firstByChatId(String chatId){
        Columns columns = new Columns();
        columns.eq("chat_id",chatId);
        if(DAO.findListByColumns(columns).size()>0){
            return true;
        }
        return false;
    }
}
