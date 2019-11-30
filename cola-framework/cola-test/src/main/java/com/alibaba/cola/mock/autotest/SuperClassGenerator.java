package com.alibaba.cola.mock.autotest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.cola.mock.utils.CommonUtils;
import com.alibaba.cola.mock.utils.FileUtils;

import static com.alibaba.cola.mock.utils.CommonUtils.autoWrapForClassNames;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/30
 */
public class SuperClassGenerator extends AbstractTemplateGenerator{
    private static final String TEMPLATE_FILE = "testsuperclass-template.ftl";
    private static final String FILE_NAME = "SpringBaseTest.java";

    public SuperClassGenerator() {
        super(false);
    }

    @Override
    public Map<String, Object> buildTemplateMap() {
        Map<String, Object> templateMap = new HashMap<>();
        templateMap.put("basePackage", getStringParam("basePackage"));

        StringBuilder mocksSb = new StringBuilder();
        StringBuilder importsSb = new StringBuilder();
        List<Class> mocksClass = (List<Class>)params.get("mocks");
        for(Class clazz : mocksClass){
            mocksSb.append(clazz.getSimpleName() + ".class,");
            importsSb.append("import " + clazz.getName() + ";\n");
        }
        //删除最后一个逗号
        if(mocksSb.length() > 0){
            mocksSb.deleteCharAt(mocksSb.length() - 1);
        }

        templateMap.put("imports", importsSb.toString());
        templateMap.put("mocks", autoWrapForClassNames(mocksSb.toString(), 120));
        templateMap.put("date", CommonUtils.formatDate(new Date()));
        return templateMap;
    }

    @Override
    public String getOutputFilePath() {
        String parentPath = FileUtils.appendSlashToURILast(FileUtils.getDefaultDirectory4TestFile());
        String filePath = parentPath + FileUtils.convertPackage2Path(getStringParam("basePackage")) + FILE_NAME;
        return filePath;
    }

    @Override
    public String getTemplateFileName() {
        return TEMPLATE_FILE;
    }
}
