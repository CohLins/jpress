package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Test;
import io.jpress.service.TestService;

@Bean
public class TestServiceProvider extends JbootServiceBase<Test> implements TestService{

    @Override
    public Test findById(Long id){
        Columns column = new Columns();
        column.eq("id",id);
        Test firstByColumns = DAO.findFirstByColumns(column);
        return firstByColumns;
    }
}
