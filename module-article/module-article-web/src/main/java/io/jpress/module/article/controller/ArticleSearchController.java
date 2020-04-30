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

import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.web.base.TemplateControllerBase;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/article/search")
public class ArticleSearchController extends TemplateControllerBase {


    public void index() {

        /**
         * 不让页面大于100，我认为：
         * 1、当一个真实用户在搜索某个关键字的内容，通过翻页去找对应数据，不可能翻到100页以上。
         * 2、翻页翻到100页以上，一般是机器：可能是来抓取数据的。
         */
        int page = getParaToInt("page", 1);
        if (page <= 0 || page > 100) {
            renderError(404);
            return;
        }
        //关键字 或作者名
        String keyword = getEscapeHtmlPara("keyword");
        //搜索条件
        String value=getEscapeHtmlPara("value");
        ArticleCategory category = getAttr("category");
        if (keyword==null){
            renderJson(Ret.fail("message", "输入不能为空"));
            return;
        }
        //存到域中
        if(value=="请选择搜索类别"||value=="社区"){
            setAttr("value","all");
        }else{
            setAttr("value",value);
        }
        setAttr("keyword", keyword);
        setAttr("page", page);

        setMenuActive(menu -> menu.isUrlStartWidth("/article/search"));
        render("artsearch.html");
    }


}
