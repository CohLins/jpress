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
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleService;

import javax.servlet.http.HttpServletRequest;

@JFinalDirective("writerVideoPage")
public class WriterVideoDirective extends JbootDirectiveBase {
    @Inject
    private ArticleService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        int page = controller.getParaToInt(1, 1);
        int pageSize = getParaToInt("pageSize", scope, 10);
        String orderBy = getPara("orderBy", scope, "id desc");
        User user = controller.getAttr("user");
        String keyword = controller.getAttr("writerKeyword");
        // 可以指定当前的分类ID
        if(keyword==null||keyword==""){
            Page<Article> articlePage =service.paginateInNormal(page, pageSize, orderBy, String.valueOf(user.getId()));
            scope.setGlobal("videoPage", articlePage);
        }else {
            Page<Article> articlePage =service.paginateInNormal(page, pageSize, orderBy, String.valueOf(user.getId()),keyword);
            scope.setGlobal("videoPage", articlePage);
        }

        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
    @JFinalDirective("videoPaginate")
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
            return (Page<?>) scope.get("videoPage");
        }

    }
}



