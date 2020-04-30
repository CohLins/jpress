package io.jpress.service;

import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.Test;

public interface TestService extends JbootServiceJoiner {
    public Test findById(Long id);
}
