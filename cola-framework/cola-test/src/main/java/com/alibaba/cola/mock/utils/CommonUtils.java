package com.alibaba.cola.mock.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.alibaba.cola.mock.ColaMockito;
import com.alibaba.cola.mock.model.MockServiceModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.alibaba.cola.mock.utils.Constants.COLAMOCK_PROXY_FLAG2;
import static com.alibaba.cola.mock.utils.Constants.OS_WINDOWS;

/**
 * @author shawnzhan.zxy
 * @date 2019/01/08
 */
public class CommonUtils {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);

    /** 默认日期格式*/
    public final static String DATE_FORMAT = "yyyy/MM/dd";

    /**
     * 格式化成colaconfig的 拼接class
     * @return
     */
    public static String format2ColaConfigClazz(Collection<String> clazzNameSet){
        StringBuilder sb = new StringBuilder();
        for(String clazzName : clazzNameSet){
            sb.append(clazzName.substring(clazzName.lastIndexOf(Constants.DOT) + 1)).append(".class").append(", ");
        }
        if(sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    /**
     * 首字母转小写
     * @param s
     * @return
     */
    public static String toLowerCaseFirstOne(String s){
        if(Character.isLowerCase(s.charAt(0))) {
            return s;
        }else {
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
        }
    }

    public static String formatDate(Date date){
        return new SimpleDateFormat(DATE_FORMAT).format(date);
    }

    public static String buildLoopChar(String c, int loopCnt){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < loopCnt; i++){
            sb.append(c);
        }
        return sb.toString();
    }

    public static String getSimpleClassName(String className){
        String[] sp = className.split("\\.");
        String simpleClassName = sp[sp.length - 1];
        String[] sp2 = simpleClassName.split("\\$\\$");
        return sp2[0];
    }

    public static void closeWriter(Writer writer){
        if(writer == null){
            return;
        }
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeStream(Closeable stream){
        if(stream == null){
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 参数类型转换
     * @param value
     * @param t
     * @return
     * @throws
     */
    public static<T> T convert(Object value, Class<T> t){
        T rst = null;
        if(value != null){
            if(Long.class.equals(t)){
                rst = (T) Long.valueOf(value.toString());
            }else if(Integer.class.equals(t)){
                if(value instanceof Double){
                    rst = (T) Integer.valueOf(((Double)value).intValue());
                }else{
                    rst = (T) Integer.valueOf(value.toString());
                }
            }else if(String.class.equals(t)){
                rst = (T)value.toString();
            }else{
                rst = (T)value;
            }
        }
        return rst;
    }

    public static boolean isWindows(){
        String osName = System.getProperty("os.name");
        return osName.contains(OS_WINDOWS);
    }

    public static void printMockObjectList(){
        logger.info("===mock object list===");
        int cnt = 0;
        for(MockServiceModel m : ColaMockito.g().getContext().getMonitorList()){
            //if(m.getInterfaceCls().getName().indexOf(Constants.COLAMOCK_PROXY_FLAG) < 0){
            //    continue;
            //}
            cnt++;
            String[] sp = m.getInterfaceCls().getName().split(COLAMOCK_PROXY_FLAG2);
            String flag = m.isManual() ? "manual" : "auto";
            flag = flag + "_" + (m.isLeaf() ? "LF" : "NF");
            logger.info("[" + flag + "]serviceName:" + m.getServiceName() + ",class:" + sp[0]);
        }
        logger.info("===mock object list=== cnt " + cnt);
    }

    public static String autoWrapForClassNames(String source, int length){
        String[] classNames = source.split(",");
        StringBuilder sb = new StringBuilder();
        StringBuilder sbLine = new StringBuilder();
        for(String name : classNames){
            sbLine.append(name).append(",");
            if(sbLine.length() > length){
                sb.append(sbLine).append("\n");
                sbLine = new StringBuilder();
            }
        }
        if(sbLine.length() > 0){
            sb.append(sbLine);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "111111111111111111111111111111111111111111111111,11111111111111111,11111222222222222,aaaaaaaaaaa,aaaaaa,a345aaaaaaaaa,aaaaaaa";
        System.out.println(autoWrapForClassNames(s, 100) + "\n");
        System.out.println(autoWrapForClassNames(s, 50) + "\n");
    }

}
