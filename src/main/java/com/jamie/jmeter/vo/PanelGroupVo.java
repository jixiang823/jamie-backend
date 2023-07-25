package com.jamie.jmeter.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

//
@Data
public class PanelGroupVo {

    private List<Map<String,Integer>> totalPass;
    private List<Map<String,Integer>> totalFail;
    private List<Map<String,Integer>> newlyFail;
    private List<Map<String,Integer>> keepFailing;

}
