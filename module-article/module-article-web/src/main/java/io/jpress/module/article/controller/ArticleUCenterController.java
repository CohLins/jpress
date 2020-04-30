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
package io.jpress.module.article.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import io.jboot.db.model.Columns;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.dfa.DFAUtil;
import io.jpress.commons.layer.SortKit;
import io.jpress.commons.utils.AliyunOssUtils;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.JsoupUtils;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.model.User;
import io.jpress.model.UserFavorite;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserFavoriteService;
import io.jpress.web.base.UcenterControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping(value = "/ucenter/article", viewPath = "/WEB-INF/views/ucenter/")
public class ArticleUCenterController extends UcenterControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private ArticleCommentService commentService;

    @Inject
    private UserFavoriteService favoriteService;

    @UCenterMenu(text = "文章列表", groupId = "article", order = 0)
    public void index() {

        User loginedUser = getLoginedUser();
        Page<Article> page = articleService._paginateByUserId(getPagePara(), 10, loginedUser.getId());
        setAttr("page", page);

        render("article/article_list.html");
    }

    public void doDel() {

        Long id = getIdPara();
        if (id == null) {
            renderFailJson();
            return;
        }

        Article article = articleService.findById(id);
        if (article == null) {
            renderFailJson();
            return;
        }

        if (notLoginedUserModel(article)) {
            renderJson(Ret.fail().set("message", "非法操作"));
            return;
        }

        renderJson(articleService.deleteById(id) ? OK : FAIL);
    }

    @UCenterMenu(text = "投稿", groupId = "article", order = 1)
    public void write() {

        int articleId = getParaToInt(0, 0);

        Article article = null;
        if (articleId > 0) {

            article = articleService.findById(articleId);
            if (article == null) {
                renderError(404);
                return;
            }

            //用户投稿，不能编辑已经审核通过的文章
            if (article.isNormal()) {
                renderError(404);
                return;
            }

            //不是自己的文章
            if (notLoginedUserModel(article)) {
                renderError(404);
                return;
            }

            setAttr("article", article);
        }

        String editMode = article == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : article.getEditMode();
        setAttr("editMode", JPressConsts.EDIT_MODE_MARKDOWN.equals(editMode)
                ? JPressConsts.EDIT_MODE_MARKDOWN
                : JPressConsts.EDIT_MODE_HTML);


        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        List<ArticleCategory> tags = categoryService.findTagListByArticleId(articleId);//.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
        setAttr("tags", tags);

        Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
        flagCheck(categories, categoryIds);

        render("article/article_write.html");
    }

    private void flagCheck(List<ArticleCategory> categories, Long... checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (ArticleCategory category : categories) {
            for (Long id : checkIds) {
                if (id != null && id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
    }


//    @EmptyValidate({
//            @Form(name = "article.title", message = "标题不能为空"),
//            @Form(name = "article.content", message = "文章内容不能为空")
//    })
    public void doWriteSave() {
        Article article = new Article();

//        article.keep("id", "title", "content", "slug", "edit_mode", "summary", "thumbnail", "meta_keywords", "meta_description");
        article.setUserId(getLoginedUser().getId());

        //附件处理
        UploadFile pdfFile = getFile("pdfFile");
        if(pdfFile!=null){
            File file = pdfFile.getFile();
            //权限判断
            if (!getLoginedUser().isStatusOk()){
                file.delete();
                renderJson(Ret.create("error", Ret.create("message", "当前用户未激活，不允许上传任何文件。")));
                return;
            }
            //格式判断
            if (!AttachmentUtils.isPdf(file)){
                file.delete();
                renderJson(Ret.fail().set("message", "附件只能是PDF格式"));
                return;
            }
            //大小判断
            Integer maxOtherSize = JPressOptions.getAsInt("attachment_other_maxsize", 10);
            int fileSize = Math.round(file.length() / 1024 * 100) / 100;
            if (fileSize > maxOtherSize * 1024) {
                file.delete();
                renderJson(Ret.fail().set("message", "上传文件大小不能超过 " + maxOtherSize + " MB"));
                return;
            }
            //路径处理
            String path = AttachmentUtils.moveFile(pdfFile);
//            System.out.println(path);
            AliyunOssUtils.upload(path, AttachmentUtils.file(path));
            article.setPdfUrl(path.replace("\\", "/"));
        }

        if(getEscapeHtmlPara("article.id")!=null){
            article.setId(Long.valueOf(getEscapeHtmlPara("article.id")));
        }

        article.setTitle(getEscapeHtmlPara("article.title"));
        article.setContent(getPara("article.content"));
//        System.out.println("处理前内容-->"+article.getContent());
        article.setSlug(getEscapeHtmlPara("article.slug"));
        article.setEditMode(getEscapeHtmlPara("article.edit_mode"));
        article.setSummary(getEscapeHtmlPara("article.summary"));
        article.setThumbnail(getEscapeHtmlPara("article.thumbnail"));
        article.setMetaKeywords(getEscapeHtmlPara("article.meta_keywords"));
        article.setMetaDescription(getEscapeHtmlPara("article.meta_description"));

        if(article.getTitle()==null){
            renderJson(Ret.fail().set("message", "标题不能为空"));
            return;
        }
        if(article.getContent()==null){
            renderJson(Ret.fail().set("message", "内容不能为空"));
            return;
        }


        if (article.getId() != null && notLoginedUserModel(article)) {
            renderJson(Ret.fail().set("message", "非法操作"));
            return;
        }

        if (!validateSlug(article)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }

        if (StrUtil.isNotBlank(article.getSlug())) {
            Article slugArticle = articleService.findFirstBySlug(article.getSlug());
            if (slugArticle != null && slugArticle.getId().equals(article.getId()) == false) {
                renderJson(Ret.fail("message", "该slug已经存在"));
                return;
            }
        }

        if (DFAUtil.isContainsSensitiveWords(article.getText())){
            renderJson(Ret.fail().set("message", "投稿内容可能包含非法字符。"));
            return;
        }

        //只保留的基本的html，其他的html比如<script>将会被清除
        if (!article._isMarkdownMode()) {
            String content = JsoupUtils.clean(article.getContent());
//            System.out.println("处理后的内容"+content);
            article.setContent(content);
        }

        article.setUserId(getLoginedUser().getId());
        article.setStatus(Article.STATUS_DRAFT);
        long id = (long) articleService.saveOrUpdate(article);

        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));
        Long[] allIds = ArrayUtils.addAll(categoryIds, tagIds);

        articleService.doUpdateCategorys(id, allIds);

        if (allIds != null && allIds.length > 0) {
            for (Long categoryId : allIds) {
                categoryService.doUpdateArticleCount(categoryId);
            }
        }

        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }

    private boolean validateSlug(Model model) {
        String slug = (String) model.get("slug");
        return slug == null ? true : !slug.contains("-");
    }

    private Long[] getTagIds(String[] tags) {
        if (ArrayUtil.isNullOrEmpty(tags)) {
            return null;
        }

        List<ArticleCategory> categories = categoryService.doNewOrFindByTagString(tags);
        long[] ids = categories.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }


    @UCenterMenu(text = "文章评论", groupId = "comment", order = 0)
    public void comment() {
        Page<ArticleComment> page = commentService._paginateByUserId(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);
        render("article/comment_list.html");
    }


    @UCenterMenu(text = "文章收藏", groupId = "favorite", order = 0)
    public void favorite() {
        Page<UserFavorite> page = favoriteService.paginateByUserIdAndType(getPagePara(), 10, getLoginedUser().getId(), "article");
        setAttr("page", page);
        render("article/article_favorite.html");
    }


    /**
     * 收藏文章
     */
    public void addFavorite(){
        System.out.println(getPara("userId")+"---"+getPara("textId"));
        //根据文章Id查询文章信息
        Article article = articleService.findById(getPara("textId"));
        //然后根据文章信息填充到收藏表中
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setId(null);
        userFavorite.setCreated(new Date());
        userFavorite.setLink("/article/"+article.getId());
        userFavorite.setSummary(article.getSummary());
        userFavorite.setThumbnail(article.getThumbnail());
        userFavorite.setTitle(article.getTitle());
        userFavorite.setType("article");
        userFavorite.setTypeId(String.valueOf(article.getId()));
        userFavorite.setTypeText("文章");
        userFavorite.setUserId(Long.valueOf(getPara("userId")));

        Object save = favoriteService.save(userFavorite);
        renderOkJson();
    }
    /**
     * 文章点赞
     */
    public void addGood(){
        System.out.println(getPara("userId")+"---"+getPara("textId"));
        //根据文章Id查询文章信息
        Article article = articleService.findById(getPara("textId"));
        //然后根据文章信息填充到收藏表中
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setId(null);
        userFavorite.setCreated(new Date());
        userFavorite.setLink("/article/"+article.getId());
        userFavorite.setSummary(article.getSummary());
        userFavorite.setThumbnail(article.getThumbnail());
        userFavorite.setTitle(article.getTitle());
        userFavorite.setType("good");
        userFavorite.setTypeId(String.valueOf(article.getId()));
        userFavorite.setTypeText("文章");
        userFavorite.setUserId(Long.valueOf(getPara("userId")));

        Object save = favoriteService.save(userFavorite);
        renderOkJson();
    }

    /**
     * 取消文章点赞
     */
    public void delGood(){
        //获取文章Id
        String textId = getPara("textId");
        String userId= getPara("userId");
        //根据文件ID查询收藏表中所对应的数据
        Columns columns = Columns.create().eq("type_id",textId).eq("user_id",userId).eq("type","good");
        UserFavorite firstByColumns = favoriteService.findFirstByColumns(columns);
        //然后根据查到数据中的Id进行删除操作
        UserFavorite userFavorite = favoriteService.findById(firstByColumns.getId());

        if (isLoginedUserModel(userFavorite)) {
            favoriteService.delete(userFavorite);
        }
        renderOkJson();
    }

    /**
     * 删除文章自定义
     */
    public void delFavorite(){
        //获取文章Id
        String textId = getPara("textId");
        String userId= getPara("userId");
        //根据文件ID查询收藏表中所对应的数据
        Columns columns = Columns.create().eq("type_id",textId).eq("user_id",userId).eq("type","article");
        UserFavorite firstByColumns = favoriteService.findFirstByColumns(columns);
        //然后根据查到数据中的Id进行删除操作
        UserFavorite userFavorite = favoriteService.findById(firstByColumns.getId());

        if (isLoginedUserModel(userFavorite)) {
            favoriteService.delete(userFavorite);
        }
        renderOkJson();
    }

    /**
     * 删除文章
     */
    public void doDelFavorite() {
        System.out.println(getPara("userId")+"---"+getPara("textId"));
        UserFavorite userFavorite = favoriteService.findById(getPara("textId"));

        if (isLoginedUserModel(userFavorite)) {
            favoriteService.delete(userFavorite);
        }
        renderOkJson();
    }


    public void doCommentDel() {

        ArticleComment comment = commentService.findById(getPara("id"));
        if (comment == null) {
            renderFailJson();
            return;
        }


        if (notLoginedUserModel(comment)) {
            renderFailJson("非法操作");
            return;
        }

        renderJson(commentService.delete(comment) ? OK : FAIL);
    }

}