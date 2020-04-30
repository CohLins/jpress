package io.jpress.module.article.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.service.UserFavoriteService;

@JFinalDirective("articleGood")
public class ArticleGoodDirective extends JbootDirectiveBase {
    @Inject
    private UserFavoriteService favoriteService;
    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Long userId = getParaToLong("user_id", scope);
        Long textId = getParaToLong(1, scope);
//        System.out.println("userId-->"+user_id.getId()+"\n"+"textId-->"+textId);
        //判断是否已经点赞
        boolean articleFav = favoriteService.isGoodFav(userId, textId);
        //显示点赞数
        Columns columns = new Columns();
        columns.eq("type","good");
        columns.eq("type_id",textId);
        long countByColumns = favoriteService.findCountByColumns(columns);
        scope.setGlobal("goodNum",countByColumns);
        if (articleFav){
            scope.setLocal("isGood", articleFav);
        }else {
            scope.setLocal("isGood", null);
        }

        renderBody(env, scope, writer);
    }
    @Override
    public boolean hasEnd() {
        return true;
    }
}
