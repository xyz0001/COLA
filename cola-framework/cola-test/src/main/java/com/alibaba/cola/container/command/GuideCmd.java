package com.alibaba.cola.container.command;

import com.alibaba.cola.container.TestsContainer;

import org.apache.commons.cli.Options;

import static com.alibaba.cola.mock.utils.Constants.GUIDE_HELP;
import static com.alibaba.cola.mock.utils.Constants.GUIDE_QUIT;
import static com.alibaba.cola.mock.utils.Constants.GUIDE_REPEAT;

/**
 * @author shawnzhan.zxy
 * @date 2019/03/20
 */
public class GuideCmd extends AbstractCommand {

    public GuideCmd(String cmdRaw) {
        super(cmdRaw);
    }

    @Override
    public void execute(){
        action();
    }

    @Override
    protected void action() {
        if(cmdRaw.equals(GUIDE_HELP)){
            //System.out.println("************** 欢迎使用轻量级TDD测试工具 ***************************");
            //System.out.println("**** 1.测试单个方法，请在控制台输入方法全称");
            //System.out.println("**** 例如：com.alibaba.cola.sales.service.test.CustomerServiceTest.testCheckConflict()");
            //System.out.println("**** 2.测试整个测试类，请在控制台输入类全称");
            //System.out.println("**** 例如：com.alibaba.cola.sales.service.test.CustomerServiceTest");
            //System.out.println("**** 3.重复上一次测试，只需在控制台输入字母 - ‘r’");
            //System.out.println("**** 4.自动生成ColaTest测试类,请输入‘new 方法全称  参数1 参数2 ...’");
            //System.out.println("**** 例如：new com.alibaba.crm.sales.domain.customer.entity.CustomerE#addContact");
            //System.out.println("***********************************************************************************");

            System.out.println("************** 欢迎使用轻量级TDD测试工具 ***************************");
            System.out.println("**** 1.测试/录制测试类，请在控制台输入方法全称");
            System.out.println("**** 例如：com.alibaba.cola.sales.service.test.CustomerServiceTest.testCheckConflict()");
            System.out.println("**** 2.自动生成ColaTest测试类,请输入‘new 方法全称  参数1 参数2 ...’");
            System.out.println("**** 例如：new com.alibaba.crm.sales.domain.customer.entity.CustomerE#addContact");
            System.out.println("**** 3.局部录制,请输入‘方法全称 -rr 重录点1(正则),重录点2...’");
            System.out.println("**** 例如：com.alibaba.crm.customer.entity.CustomerE#addContact -tt com.alibaba.crm.*Tunnel.get");
            System.out.println("**** 4.监视录制,请输入‘watch 方法全称’ 按h键停止监视");
            System.out.println("**** 例如：watch com.alibaba.crm.customer.entity.CustomerE#addContact");
            System.out.println("**** 5.重复上一次测试，只需在控制台输入字母 - ‘r’");
            System.out.println("**** 6.帮助 - ‘h’");
            System.out.println("**** 7.退出 - ‘q’");
            System.out.println("***********************************************************************************");
        }else if(cmdRaw.equals(GUIDE_REPEAT)){
            TestsContainer.execute(preCmd.cmdRaw);
        }else if(cmdRaw.equals(GUIDE_QUIT)){
            System.exit(0);
            throw new Error("强制退出");
        }
    }

}
