package io.jpress.model;

import io.jboot.db.annotation.Table;
import io.jpress.model.base.BaseMember;
import io.jpress.model.base.BaseTest;

@Table(tableName = "test", primaryKey = "id")
public class Test extends BaseTest<Test> {
    private static final long serialVersionUID = 1L;

}
