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

@JFinalDirective("centerFollow")
public class CenterFollowDirective extends JbootDirectiveBase {
    @Inject
    private UserFavoriteService favoriteService;
    @Inject
    private ArticleService articleService;
    @Inject
    private UserService userService;
    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        Controller controller = JbootControllerContext.get();
        User user = controller.getAttr("centerUser");
        String key=controller.getAttr("keyValue");
//        System.out.println(key);
        if (key==null||key==""||key.length()==0){
//            System.out.println(11111);
            Columns columns = new Columns();
            columns.eq("user_id",user.getId());
            columns.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(columns);
            List<User> users=new ArrayList<>();
            for (UserFavorite userFavorite:listByColumns){
                User byId = userService.findById(userFavorite.getTypeId());
                users.add(byId);
            }
            scope.setGlobal("centerFollows",users);
        }else {
            System.out.println(key);
            Columns columns = new Columns();
            columns.eq("user_id",user.getId());
            columns.eq("type","follow");
            List<UserFavorite> listByColumns = favoriteService.findListByColumns(columns);
            List<User> users=new ArrayList<>();
            for (UserFavorite userFavorite:listByColumns){
                User byId = userService.findById(userFavorite.getTypeId());
                System.out.println(byId.getNickname());
                if(byId.getNickname().equals(key)){
                    users.add(byId);
                }
            }
            scope.setGlobal("centerFollows",users);
        }

        renderBody(env, scope, writer);
    }
    @Override
    public boolean hasEnd() {
        return true;
    }
}
