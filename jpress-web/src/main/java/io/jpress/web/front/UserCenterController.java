/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import io.jboot.db.model.Columns;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.FileUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConfig;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.model.PersonalLetter;
import io.jpress.model.User;
import io.jpress.model.UserFavorite;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.*;
import io.jpress.web.base.UcenterControllerBase;
import org.omg.PortableInterceptor.INACTIVE;
import sun.misc.BASE64Decoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping(value = "/ucenter", viewPath = "/WEB-INF/views/ucenter/")
public class UserCenterController extends UcenterControllerBase {

    @Inject
    private UserFavorite favoriteService;
    @Inject
    private UserService userService;

    @Inject
    private UserCartService cartService;

    @Inject
    private UserAddressService addressService;

    @Inject
    private CouponCodeService couponCodeService;

    @Inject
    private CouponService couponService;

    @Inject
    private UserOrderService userOrderService;

    @Inject
    private UserOrderItemService userOrderItemService;
    @Inject
    private ArticleService articleService;
    @Inject
    private PersonalLetterService personalLetterService;

    /**
     * 用户中心首页
     */
    public void index() {
        Integer fansNum=0;//粉丝数
        Long readNum=0L;
        Columns column;
        if(getLoginedUser()!=null) {
            setAttr("centerUser", getLoginedUser());
            /**
             * 获取粉丝数
             * 1.不可能出现被同一个人关注两次的情况
             * 2.所以当type为关注时type_id重复次数就可以认为是粉丝数
             */
            column = new Columns();
            column.eq("type_id", getLoginedUser().getId());
            column.eq("type", "follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(column);
            fansNum = listByColumns.size();
            setAttr("fansNum",fansNum);
            /**
             * 获取阅读数
             * 1.登陆者所有文章访问量总和
             */
            List<Article> articles = articleService.paginateInNormal(String.valueOf(getLoginedUser().getId()));
            for (Article article:articles){
                readNum=readNum+article.getViewCount();
            }
            setAttr("readNum",readNum);
        }
        render("index.html");
    }


    public void info() {
        render("info.html");
    }

    public void doSaveUser() {
        User user = getBean(User.class);
        user.keepUpdateSafe();
        user.setId(getLoginedUser().getId());

        //若用户更新邮件，那么重置邮件状态为：未激活
        if (user.getEmail() != null
                && user.getEmail().equals(getLoginedUser().getEmail()) == false) {
            user.setEmailStatus(null);
        }

        userService.saveOrUpdate(user);
        renderOkJson();
    }


    /**
     * 个人签名
     */
    public void signature() {
        render("signature.html");
    }
    /**
     * 关注和粉丝
     */
    public void follow(){
        String centerName = getEscapeHtmlPara("centerName");
        setAttr("keyValue",centerName);
        //标志参数
        String para = getPara("action");
        Integer fansNum=0;//粉丝数
        Integer followNum=0;//关注数
        Columns column;
        if(getLoginedUser()!=null){
            setAttr("centerUser",getLoginedUser());
            /**
             * 获取粉丝数
             * 1.不可能出现被同一个人关注两次的情况
             * 2.所以当type为关注时type_id重复次数就可以认为是粉丝数
             */
            column=new Columns();
            column.eq("type_id",getLoginedUser().getId());
            column.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(column);
            fansNum=listByColumns.size();
            /**
             * 获取关注数
             * 1.不可能出现关注同一个人两次的情况
             * 2.所以当type为关注时user_id重复次数就可以认为是粉丝数
             */
            column=new Columns();
            column.eq("user_id",getLoginedUser().getId());
            column.eq("type","follow");
            List<UserFavorite> listByColumns1 = favoriteService.findListByColumns(column);
            followNum=listByColumns1.size();
            setAttr("centerFansNum",fansNum);
            setAttr("centerFollowNum",followNum);

        }
        if(para!=null&&para.equals("fans")){
            setAttr("para",para);
            render("fans.html");
        }else {
            setAttr("para","follow");
            render("follow.html");
        }

    }
    /**
     * 关注和粉丝搜索
     */
    public void searchFollow(){
        //标志参数
        String para = getEscapeHtmlPara("para");
        String centerName = getEscapeHtmlPara("centerName");
        setAttr("keyValue",centerName);
        System.out.println("controller-->"+centerName);
        Integer fansNum=0;//粉丝数
        Integer followNum=0;//关注数
        Columns column;
        if(getLoginedUser()!=null){
            setAttr("centerUser",getLoginedUser());
            /**
             * 获取粉丝数
             * 1.不可能出现被同一个人关注两次的情况
             * 2.所以当type为关注时type_id重复次数就可以认为是粉丝数
             */
            column=new Columns();
            column.eq("type_id",getLoginedUser().getId());
            column.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(column);
            fansNum=listByColumns.size();
            /**
             * 获取关注数
             * 1.不可能出现关注同一个人两次的情况
             * 2.所以当type为关注时user_id重复次数就可以认为是粉丝数
             */
            column=new Columns();
            column.eq("user_id",getLoginedUser().getId());
            column.eq("type","follow");
            List<UserFavorite> listByColumns1 = favoriteService.findListByColumns(column);
            followNum=listByColumns1.size();
            setAttr("centerFansNum",fansNum);
            setAttr("centerFollowNum",followNum);
            setAttr("para",para);
        }
        if(para.equals("fans")){
            render("fans.html");
        }else {
            render("follow.html");
        }
    }


    /**
     * 我的私信
     */
    public void myLetter(){
        //查询聊天列表
        List<PersonalLetter> letters = personalLetterService.letterListShow(String.valueOf(getLoginedUser().getId()),0);
        if(letters!=null&&letters.size()>0){
            for (PersonalLetter letter:letters){
                if (letter.getSendId()==getLoginedUser().getId()){
                    User byId = userService.findById(letter.getAcceptId());
                    letter.setSendName(byId.getNickname());
                    letter.setSendImg(byId.getAvatar());
                }else {
                    User byId = userService.findById(letter.getSendId());
                    letter.setSendName(byId.getNickname());
                    letter.setSendImg(byId.getAvatar());
                }

            }
        }
        //插入未读消息数量
        setAttr("unread",personalLetterService.unReadLetter(String.valueOf(getLoginedUser().getId())));
        setAttr("letterList",letters);
        render("user_email.html");
    }
    /**
     * 我的私信筛选
     */
    public void myLetterChoose(){
        String tagText = getPara("tag");
        setAttr("tag",tagText);
        Integer tag=0;
        if(tagText!=null){
            switch (tagText.trim()){
                case "全部信息":
                    tag=0;
                    break;
                case "未读信息":
                    tag=1;
                    break;
                case "已读信息":
                    tag=2;
                    break;
                default:
                    tag=0;
                    break;
            }
        }
        //查询聊天列表
        List<PersonalLetter> letters = personalLetterService.letterListShow(String.valueOf(getLoginedUser().getId()),tag);
        for (PersonalLetter letter:letters){
            User byId = userService.findById(letter.getSendId());
            letter.setSendName(byId.getNickname());
            letter.setSendImg(byId.getAvatar());
        }
        //插入未读消息数量
        setAttr("unread",personalLetterService.unReadLetter(String.valueOf(getLoginedUser().getId())));
        setAttr("letterList",letters);
        render("user_email_choose.html");
    }
    /**
     * 私信列表删除
     */
    public void delPanelLetter(){
        String charIds = getPara("charIds");
        if(charIds==null||charIds==""){
            return;
        }
        String[] id=charIds.split(";");
        for(String chatId:id){
            personalLetterService.deleteId(chatId,getLoginedUser().getId());
        }
        renderOkJson();
    }
    /**
     * 私信标记已读
     */
    public void setRead(){
        String charIds = getPara("charIds");
        if(charIds==null||charIds==""){
            return;
        }
        String[] id=charIds.split(";");
        for (String chatId:id){
            System.out.println(chatId);
            List<PersonalLetter> letters = personalLetterService.letterPersonal(chatId, String.valueOf(getLoginedUser().getId()));
            for (PersonalLetter letter:letters){
                letter.setStatus("read");
                personalLetterService.saveOrUpdate(letter);
            }
        }
        renderOkJson();
    }
    /**
     * 私信详细页
     */
    public void letterDetail(){
        String chatId = getPara("chat");
        String acceptId=getPara("acceptId");
        if (chatId==null&&acceptId==null){
            render("404.html");
            return;
        }
        if (chatId==null){
            chatId=createChatId(Long.valueOf(acceptId));
        }
        //判断聊天是否存在
        chatJudge(chatId,acceptId);
        //根据聊天Id 然后将登陆者ID代入查询 需要分别代入发送者和接收者两个身份
        PersonalLetter personalLetter = personalLetterService.letterOnly(chatId, getLoginedUser().getId(), null);
        if (personalLetter!=null){
            User byId = userService.findById(personalLetter.getAcceptId());
            setAttr("acceptUser",byId);
        }else {
            PersonalLetter personalLetter1 = personalLetterService.letterOnly(chatId, null, getLoginedUser().getId());
            if (personalLetter1==null){
                render("404.html");
                return;
            }
            User byId = userService.findById(personalLetter1.getSendId());
            setAttr("acceptUser",byId);
        }
        List<PersonalLetter> letters = personalLetterService.letterRecordShow(chatId, null,getLoginedUser().getId());
        if(letters.size()<=0){
            setAttr("startTime",new Date());
            setAttr("letterRecord",letters);
            render("email_details.html");
            return;
        }
        setAttr("startTime",letters.get(0).getCreateTime());
        for (PersonalLetter letter:letters){
//            System.out.println(letter.getCreateTime());
            User byId = userService.findById(letter.getSendId());
            letter.setSendImg(byId.getAvatar());
            letter.setSendName(byId.getNickname());

        }
        setAttr("letterRecord",letters);

        //读取列表完成后更新已读状态
        UpdateStatus(letters);
        render("email_details.html");
    }
    /**
     * 删除一条私信
     */
    public void delOnlyLetter(){
        String id = getPara("id");
        personalLetterService.deleteOnly(id,getLoginedUser().getId());
        renderOkJson();
    }
    /**
     * 发送一条私信
     */
    public void sendLetter(){
        String conter=getPara("content");
        String img=getPara("img");
        String acceptId=getPara("acceptId");
        PersonalLetter personalLetter=new PersonalLetter();
        personalLetter.setId(null);
        //比较id大小生成聊天Id
        personalLetter.setChatId(createChatId(Long.valueOf(acceptId)));
        personalLetter.setSendId(getLoginedUser().getId());
        personalLetter.setAcceptId(Long.valueOf(acceptId));
        personalLetter.setStatus("unread");
        personalLetter.setSendStatus("normal");
        personalLetter.setAcceptStatus("normal");
        personalLetter.setCreateTime(new Date());
        if (conter!=null){
            personalLetter.setContent(conter);
        }
        if(img!=null){
            String attachmentRoot = JPressConfig.me.getAttachmentRootOrWebRoot();
            String s = AttachmentUtils.chatPic(".jpg");
            File file = new File(s);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            personalLetter.setPicUrl(FileUtil.removePrefix(new File(s).getAbsolutePath(), attachmentRoot));
            //图片处理
            GenerateImage(img,s);
        }
        personalLetterService.saveOrUpdate(personalLetter);
        renderOkJson();
    }



    /**
     * 头像设置
     */
    public void avatar() {
        render("avatar.html");
    }


    /**
     * 账号密码
     */
    public void pwd() {
        render("pwd.html");
    }

    public void bind() {
        render("bind.html");
    }


    @EmptyValidate({
            @Form(name = "oldPwd", message = "旧不能为空"),
            @Form(name = "newPwd", message = "新密码不能为空"),
            @Form(name = "confirmPwd", message = "确认密码不能为空")
    })
    public void doUpdatePwd(String oldPwd, String newPwd, String confirmPwd) {

        User user = getLoginedUser();

        if (userService.doValidateUserPwd(user, oldPwd).isFail()) {
            renderJson(Ret.fail().set("message", "密码错误"));
            return;
        }

        if (newPwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次出入密码不一致"));
            return;
        }

        String salt = user.getSalt();
        String hashedPass = HashKit.sha256(salt + newPwd);

        user.setPassword(hashedPass);
        userService.update(user);

        renderOkJson();
    }


    @EmptyValidate({
            @Form(name = "path", message = "请先选择图片")
    })
    public void doSaveAvatar(String path, int x, int y, int w, int h) {

        String attachmentRoot = JPressConfig.me.getAttachmentRootOrWebRoot();

        String oldPath = attachmentRoot + path;

        //先进行图片缩放，保证图片和html的图片显示大小一致
        String zoomPath = AttachmentUtils.newAttachemnetFile(FileUtil.getSuffix(path)).getAbsolutePath();
        ImageUtils.zoom(500, oldPath, zoomPath); //500的值必须和 html图片的max-width值一样

        //进行剪切
        String newAvatarPath = AttachmentUtils.newAttachemnetFile(FileUtil.getSuffix(path)).getAbsolutePath();
        ImageUtils.crop(zoomPath, newAvatarPath, x, y, w, h);

        String newPath = FileUtil.removePrefix(newAvatarPath, attachmentRoot).replace("\\", "/");

        AliyunOssUtils.upload(newPath, new File(newAvatarPath));

        User loginedUser = getLoginedUser();
        loginedUser.setAvatar(newPath);
        userService.saveOrUpdate(loginedUser);
        renderOkJson();
    }

    /**
     * 退出登录
     */
    public void logout() {
        CookieUtil.remove(this, JPressConsts.COOKIE_UID);
        redirect("/");
    }


    /**
     * 退出登录，这个方法存在只是为了兼容之前的模板
     */
    @Deprecated
    public void doLogout() {
        logout();
    }


    public void chatJudge(String chatId,String acceptId){
        User byId = userService.findById(acceptId);
        if (byId==null){
            render("404.html");
            return;
        }
        if (!personalLetterService.firstByChatId(chatId)){
            PersonalLetter personalLetter=new PersonalLetter();
            personalLetter.setId(null);
            personalLetter.setChatId(chatId);
            personalLetter.setSendId(getLoginedUser().getId());
            personalLetter.setAcceptId(Long.valueOf(acceptId));
            personalLetter.setContent("[打了招呼]");
            personalLetter.setStatus("unread");
            personalLetter.setSendStatus("normal");
            personalLetter.setAcceptStatus("normal");
            personalLetter.setCreateTime(new Date());
            personalLetterService.saveOrUpdate(personalLetter);
        }
    }

    public void UpdateStatus(List<PersonalLetter> personalLetters){
        for (PersonalLetter personalLetter:personalLetters){
            if(personalLetter.getAcceptId().equals(getLoginedUser().getId())){
                personalLetter.setStatus("read");
                personalLetterService.saveOrUpdate(personalLetter);
            }
        }
    }
    public String createChatId(Long acceptId){
        Long id = getLoginedUser().getId();
        String s = (id > acceptId) ? acceptId + "_" + id : id + "_" + acceptId;
        System.out.println(s);
        return s;
    }


    //base64字符串转化成图片
    public static boolean GenerateImage(String imgStr,String name)
    {   //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try
        {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i)
            {
                if(b[i]<0)
                {//调整异常数据
                    b[i]+=256;
                }
            }
            //生成jpeg图片
            OutputStream out = new FileOutputStream(name);
            out.write(b);
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
