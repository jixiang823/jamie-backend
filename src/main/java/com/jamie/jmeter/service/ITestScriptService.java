package com.jamie.jmeter.service;

import com.jamie.jmeter.form.ScriptForm;

public interface ITestScriptService {
    String runScript(String userId, ScriptForm scriptForm);

}
