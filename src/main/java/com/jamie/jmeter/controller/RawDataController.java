package com.jamie.jmeter.controller;

import com.jamie.jmeter.service.IRawDataService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RawDataController {

    @Resource
    private IRawDataService rawDataService;

    @PostMapping("/jmeter/report/save")
    public void save(@RequestBody String rawData) {
        rawDataService.save(rawData);
    }

}
