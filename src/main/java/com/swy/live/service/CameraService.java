package com.swy.live.service;

import com.jds.core.common.KendoResult;
import com.jds.core.service.BaseService;

import java.util.List;
import java.util.Map;

public interface CameraService extends BaseService {
    List getCameraList();

    KendoResult getCameraPaged(Map params);
}
