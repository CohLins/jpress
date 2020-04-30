package io.jpress.model.base;

import com.jfinal.plugin.activerecord.IBean;
import io.jboot.db.model.JbootModel;

@SuppressWarnings("serial")
public abstract class BaseTest<M extends BaseTest<M>> extends JbootModel<M> implements IBean {
    private static final long serialVersionUID = 1L;

    public void setId(java.lang.Long id) {
        set("id", id);
    }

    public java.lang.Long getId() {
        return getLong("id");
    }

    public void setUserId(java.lang.Long userId) {
        set("user_id", userId);
    }

    public java.lang.Long getUserId() {
        return getLong("user_id");
    }

    public void setTextId(java.lang.Long textId) {
        set("text_id", textId);
    }

    public java.lang.Long getTextId() {
        return getLong("text_id");
    }

    public void setType(java.lang.String type) {
        set("type", type);
    }

    public java.lang.String getType() {
        return getStr("type");
    }
}
