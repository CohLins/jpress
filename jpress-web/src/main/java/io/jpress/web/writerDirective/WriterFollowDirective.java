package io.jpress.web.writerDirective;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.JbootControllerContext;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.User;
import io.jpress.model.UserFavorite;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.UserFavoriteService;
import io.jpress.service.UserService;

import java.util.ArrayList;
import java.util.List;

@JFinalDirective("writerFollow")
public class WriterFollowDirective extends JbootDirectiveBase {
    @Inject
    private UserFavoriteService favoriteService;
    @Inject
    private ArticleService articleService;
    @Inject
    private UserService userService;
    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Long otherId=getPara(0,scope);
        System.out.println("follow-->"+otherId);
        Controller controller = JbootControllerContext.get();
        User user = controller.getAttr("master");
        if(Long.valueOf(otherId)==user.getId()){
            Columns columns = new Columns();
            columns.eq("user_id",user.getId());
            columns.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(columns);
            List<User> users=new ArrayList<>();
            for (UserFavorite userFavorite:listByColumns){
                User byId = userService.findById(userFavorite.getTypeId());
                users.add(byId);
            }
            scope.setGlobal("follows",users);
        }else {
            Columns columns = new Columns();
            columns.eq("user_id",Long.valueOf(otherId));
            columns.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(columns);
            List<User> users=new ArrayList<>();
            for (UserFavorite userFavorite:listByColumns){
                User byId = userService.findById(userFavorite.getTypeId());
                users.add(byId);
            }
            scope.setGlobal("follows",users);
        }





        renderBody(env, scope, writer);
    }
    @Override
    public boolean hasEnd() {
        return true;
    }
}
