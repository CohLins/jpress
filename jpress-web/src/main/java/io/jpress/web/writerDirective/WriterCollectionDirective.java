package io.jpress.web.writerDirective;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jboot.web.directive.base.PaginateDirectiveBase;
import io.jpress.JPressOptions;
import io.jpress.commons.directive.DirectveKit;
import io.jpress.model.User;
import io.jpress.model.UserFavorite;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserFavoriteService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@JFinalDirective("writerCollectionPage")
public class WriterCollectionDirective extends JbootDirectiveBase {
    @Inject
    private ArticleService service;
    @Inject
    private UserFavoriteService favoriteService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();
        String keyword=controller.getAttr("writerKeyword");

        int page = controller.getParaToInt(1, 1);
        int pageSize = getParaToInt("pageSize", scope, 10);
        String orderBy = getPara("orderBy", scope, "id desc");
        User user = controller.getAttr("user");


        if (keyword==null||keyword==""){
            // 搜索该用户收藏的文章
            Page<UserFavorite> favoritePage = favoriteService.paginateByUserIdAndType(page, pageSize, user.getId(), "article");
            for (UserFavorite userFavorite:favoritePage.getList()){
                Article byId = service.findById(userFavorite.getTypeId());
                userFavorite.setRead(byId.getViewCount());
                userFavorite.setComment(byId.getCommentCount());
                userFavorite.setStartTime(byId.getCreated());
            }
            scope.setGlobal("favoritePage", favoritePage);
        }else {
            //根据关键词搜索用户收藏的文章
            Page<UserFavorite> favoritePage = favoriteService.paginateByUserIdAndType(page, pageSize, user.getId(), "article",keyword);
            for (UserFavorite userFavorite:favoritePage.getList()){
                Article byId = service.findById(userFavorite.getTypeId());
                userFavorite.setRead(byId.getViewCount());
                userFavorite.setComment(byId.getCommentCount());
                userFavorite.setStartTime(byId.getCreated());
            }
            scope.setGlobal("favoritePage", favoritePage);
        }

        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
    @JFinalDirective("clubPaginate")
    public static class TemplatePaginateDirective extends PaginateDirectiveBase {

        @Override
        protected String getUrl(int pageNumber, Env env, Scope scope, Writer writer) {
            HttpServletRequest request = JbootControllerContext.get().getRequest();
            String url = request.getRequestURI();
            String contextPath = JFinal.me().getContextPath();

            boolean firstGotoIndex = getPara("firstGotoIndex", scope, false);

            if (pageNumber == 1 && firstGotoIndex) {
                return contextPath + "/";
            }

            // 如果当前页面是首页的话
            // 需要改变url的值，因为 上一页或下一页是通过当前的url解析出来的
            if (url.equals(contextPath + "/")) {
                url = contextPath + "/article/category/index"
                        + JPressOptions.getAppUrlSuffix();
            }
            return DirectveKit.replacePageNumber(url, pageNumber);
        }

        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("favoritePage");
        }

    }
}



