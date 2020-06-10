package com.swy.live.service.impl;

import com.jds.core.common.KendoResult;
import com.jds.core.service.impl.BaseServiceImpl;
import com.jds.core.utils.QueryUtil;
import com.swy.live.service.CameraService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CameraServiceImpl extends BaseServiceImpl implements CameraService {

    @Override
    public List getCameraList() {
        List data = db("camera").select();
        return data;
    }

    @Override
    public KendoResult getCameraPaged(Map params) {
        KendoResult data = QueryUtil.getRecordsPaged("camera.getCameraPaged", params, 1, 3);
        return data;
    }
}
