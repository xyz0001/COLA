package com.alibaba.cola.mock.autotest;

import java.util.Map;

import com.alibaba.cola.mock.utils.FileUtils;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/30
 */
public class SpringMockitoTestXmlGenerator extends AbstractTemplateGenerator{
    private static final String TEMPLATE_FILE = "spring-mockito-test-template.ftl";
    private static final String FILE_NAME = "spring-mockito-test.xml";

    public SpringMockitoTestXmlGenerator() {
        super(false);
    }

    @Override
    public Map<String, Object> buildTemplateMap(){
        return params;
    }

    @Override
    public String getOutputFilePath(){
        String parentPath = FileUtils.appendSlashToURILast(FileUtils.getDefaultDirectory4ConfigFile());
        String filePath = parentPath + FILE_NAME;
        return filePath;
    }

    @Override
    public String getTemplateFileName() {
        return TEMPLATE_FILE;
    }
}
