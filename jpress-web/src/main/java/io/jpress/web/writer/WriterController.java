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
package io.jpress.web.writer;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.dfa.DFAUtil;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.model.User;
import io.jpress.model.UserFavorite;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.OptionService;
import io.jpress.service.UserFavoriteService;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/writer")
public class WriterController extends TemplateControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private UserService userService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ArticleCommentService commentService;
    @Inject
    private UserFavoriteService favoriteService;


    public void index() {

        //登陆者
        User master=getLoginedUser();
        //未登录请先登录
        if (master==null){
            render("user_login.html");
            return;
        }
        //将登陆者信息存域
        setAttr("master",master);
        //将搜索框值存域
        String keyword = getEscapeHtmlPara("keyword");
        setAttr("writerKeyword",keyword);
        System.out.println("搜索词-->"+keyword);

        //访问的作者
        User user = getUser();
        User articleAuthor = user.getId() != null
                ? userService.findById(user.getId())
                : null;
        Integer goodNum=0;//点赞数
        Integer fansNum=0;//粉丝数
        Integer followNum=0;
        if(user!=null){
            /**
             * 获取点赞数
             * 1.获取个人所有文章Id
             * 2.遍历查询文章点赞数
             * 3.计算总和
             */
            //获取点赞总数
            List<Article> articles = articleService.paginateInNormal(String.valueOf(user.getId()));
            Columns column;
            for (Article article:articles){
                column=new Columns();
                column.eq("type_id",article.getId());
                column.eq("type","good");
                List<UserFavorite> listByColumns = favoriteService.findListByColumns(column);
                goodNum=goodNum+listByColumns.size();
            }
            /**
             * 获取粉丝数
             * 1.不可能出现被同一个人关注两次的情况
             * 2.所以当type为关注时type_id重复次数就可以认为是粉丝数
             */
            column=new Columns();
            column.eq("type_id",user.getId());
            column.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(column);
            fansNum=listByColumns.size();
            /**
             * 获取关注数
             * 1.不可能出现关注同一个人两次的情况
             * 2.所以当type为关注时user_id重复次数就可以认为是粉丝数
             */
            column=new Columns();
            column.eq("user_id",user.getId());
            column.eq("type","follow");
            List<UserFavorite> listByColumns1 = favoriteService.findListByColumns(column);
            followNum=listByColumns1.size();
            setAttr("goodNum",goodNum);
            setAttr("fansNum",fansNum);
            setAttr("followNum",followNum);

        }
        setAttr("user",user);
        
        render("user_center.html");
    }


    private User getUser() {
        String idOrSlug = getPara(0);
//        System.out.println(idOrSlug);
        return userService.findById(idOrSlug);
    }

    /**
     * 添加关注
     */
    public void addFollow(){
        User user=getLoginedUser();
        String writerId=getPara("writerId");
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setId(null);
        userFavorite.setCreated(new Date());
        userFavorite.setLink("/writer/"+writerId);
        userFavorite.setType("follow");
        userFavorite.setTypeId(String.valueOf(writerId));
        userFavorite.setTypeText("关注");
        userFavorite.setUserId(user.getId());
        favoriteService.save(userFavorite);
        renderOkJson();
    }
    /**
     * 取消关注
     */
    public void delFollow(){
        User user=getLoginedUser();
        //获取作者Id
        String writerId = getPara("writerId");
        //根据文件ID查询收藏表中所对应的数据
        Columns columns = Columns.create().eq("type_id",writerId).eq("user_id",user.getId()).eq("type","follow");
        UserFavorite firstByColumns = favoriteService.findFirstByColumns(columns);
        //然后根据查到数据中的Id进行删除操作
        UserFavorite userFavorite = favoriteService.findById(firstByColumns.getId());

        if (isLoginedUserModel(userFavorite)) {
            favoriteService.delete(userFavorite);
        }
        renderOkJson();
    }


}
