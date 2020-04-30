package io.jpress.module.article.directive;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.User;
import io.jpress.service.UserFavoriteService;

@JFinalDirective("articleFavorite")
public class ArticleFavoriteDirective extends JbootDirectiveBase {
    @Inject
    private UserFavoriteService favoriteService;
    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Long userId = getParaToLong("user_id", scope);
        Long textId = getParaToLong(1, scope);
//        System.out.println("userId-->"+user_id.getId()+"\n"+"textId-->"+textId);
        boolean articleFav = favoriteService.isArticleFav(userId, textId);
        if (articleFav){
            scope.setLocal("isFav", articleFav);
        }else {
            scope.setLocal("isFav", null);
        }

        renderBody(env, scope, writer);
    }
    @Override
    public boolean hasEnd() {
        return true;
    }
}
