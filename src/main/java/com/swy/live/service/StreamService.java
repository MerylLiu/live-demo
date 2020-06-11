package com.swy.live.service;

import com.jds.core.common.KendoResult;
import com.jds.core.service.BaseService;

import java.util.Map;

public interface StreamService extends BaseService {

    KendoResult getPushedPaged(Map param);

    void addPush(Map param);

    void stopPush(Integer id);
}
