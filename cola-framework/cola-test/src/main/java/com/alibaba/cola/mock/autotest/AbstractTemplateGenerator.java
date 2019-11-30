package com.alibaba.cola.mock.autotest;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.cola.mock.utils.CommonUtils;
import com.alibaba.cola.mock.utils.FileUtils;
import com.alibaba.cola.mock.utils.TemplateBuilder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shawnzhan.zxy
 * @date 2019/06/30
 */
public abstract class AbstractTemplateGenerator {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateGenerator.class);
    protected Map<String, Object> params = new HashMap<>();
    private boolean overWrite = true;

    public AbstractTemplateGenerator(boolean overWrite){
        this.overWrite = overWrite;
    }

    public void generate(){
        logger.info("generate start... file=>" + getOutputFilePath());
        Writer writer = null;
        try{
            // 执行插值，并输出到指定的输出流中
            File file = createIfNotExists(getOutputFilePath());
            if(file.exists() && !overWrite){
                logger.info(getOutputFilePath() + " already exists!");
                return;
            }
            writer = new FileWriter(file);

            TemplateBuilder builder = new TemplateBuilder(FileUtils.getTemplate(getTemplateFileName()));
            builder.setVar(buildTemplateMap());
            builder.build(writer);
            logger.info("generate success.");
        }catch(Exception e){
            logger.error("AbstractTemplateGenerator.generate ERROR!", e);
        }finally {
            CommonUtils.closeWriter(writer);
        }
    }

    public abstract Map<String, Object> buildTemplateMap();
    public abstract String getOutputFilePath();
    public abstract String getTemplateFileName();

    public AbstractTemplateGenerator putParam(String key, Object value){
        params.put(key, value);
        return this;
    }

    protected String getStringParam(String key){
        return params.get(key) == null ? StringUtils.EMPTY : params.get(key).toString();
    }

    private File createIfNotExists(String filePath){
        FileUtils.createDirectory(filePath);
        return new File(filePath);
    }
}
