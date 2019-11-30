package ${basePackage};

import org.apache.ibatis.annotations.Mapper;
import com.alibaba.cola.mock.annotation.ColaMockConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

${imports}

/**
* @author xxxxx
* @date ${date}
*/
@ContextConfiguration("classpath:spring-mockito-test.xml")
@TestPropertySource(properties = { "spring.hsf.enabled=false"})
//@TestPropertySource("classpath:larissa_test.properties")
@ColaMockConfig(mocks = {${mocks}}, annotationMocks={Mapper.class})
public class SpringBaseTest {

    /**
    * 多线程情况下 等待子线程结束
    */
    public void waitForThread(){
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
    * 多线程情况下 等待子线程结束
    */
    public void waitForThread(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
