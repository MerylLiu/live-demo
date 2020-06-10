package com.swy.live.service.impl;

import com.jds.core.service.impl.BaseServiceImpl;
import com.swy.live.service.CameraService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CameraServiceImpl extends BaseServiceImpl implements CameraService {

    @Override
    public List getCameraList() {
        List data = db("camera").select();
        return data;
    }
}
