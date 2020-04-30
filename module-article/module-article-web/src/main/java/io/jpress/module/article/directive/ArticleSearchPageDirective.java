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
package io.jpress.module.article.directive;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.JbootPaginateDirective;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.User;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("articleSearchPage")
public class ArticleSearchPageDirective extends JbootDirectiveBase {
    @Inject
    private ArticleCategoryService articleCategoryService;
    @Inject
    private ArticleService articleService;
    @Inject
    private UserService userService;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Controller controller = JbootControllerContext.get();

        String keyword = controller.getAttr("keyword");
        String type=controller.getAttr("value");
        int page = controller.getAttr("page");
        int pageSize = getParaToInt("pageSize", scope, 10);
        String orderBy = getPara("orderBy", scope, "id desc");

        // 1.根据类别查询类别Id
        ArticleCategory firstByTitle = articleCategoryService.findFirstByTitle(type);

        if ( firstByTitle != null) {
            Long  categoryId = firstByTitle.getId();
            System.out.println("类别Id"+categoryId);
            //2.分类ID不为空时，则加入ID查询相对应类别的
            Page<Article> dataPage = StrUtil.isNotBlank(keyword)
                    ? articleService.searchByTypeKeyword(page, pageSize, categoryId, orderBy,keyword)
                    : null;
            if (dataPage.getList().size()>0){
                //按标题查
                if (dataPage != null) {
                    scope.setGlobal("articlePage", dataPage);
                }
            }else {
                //按作者查
                User byUsernameOrEmail = userService.findByUsernameOrEmail(keyword);
                if (byUsernameOrEmail!=null){
                    Page<Article> articlePage =articleService.searchByTypeUserId(page, pageSize, categoryId, orderBy,byUsernameOrEmail.getId());
                    System.out.println(articlePage);
                    if (articlePage!=null){
                        scope.setGlobal("articlePage", articlePage);
                    }
                }else{
                    if (dataPage != null) {
                        scope.setGlobal("articlePage", dataPage);
                    }
                }
            }
        }else {
            //3.分类Id为空则查询全部
            Page<Article> dataPage = StrUtil.isNotBlank(keyword)
                    ? articleService.search(keyword, page, pageSize)
                    : null;
            if (dataPage.getList().size()>0){
                //按标题查
                if (dataPage != null) {
                    scope.setGlobal("articlePage", dataPage);
                }
            } else {
                //按作者查
                User byUsernameOrEmail = userService.findByUsernameOrEmail(keyword);
                if (byUsernameOrEmail!=null){
                    Page<Article> articlePage =articleService.searchByUserId(byUsernameOrEmail.getId(), page, pageSize);
                    System.out.println(articlePage);
                    if (articlePage!=null){
                        scope.setGlobal("articlePage", articlePage);
                    }
                }else{
                    if (dataPage != null) {
                        scope.setGlobal("articlePage", dataPage);
                    }
                }
            }
        }





        //需要页面自行判断 articlePage 是否为空
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }


    @JFinalDirective("articleSearchPaginate")
    public static class SearchPaginateDirective extends JbootPaginateDirective {
        @Override
        protected Page<?> getPage(Env env, Scope scope, Writer writer) {
            return (Page<?>) scope.get("articlePage");
        }

    }
}
