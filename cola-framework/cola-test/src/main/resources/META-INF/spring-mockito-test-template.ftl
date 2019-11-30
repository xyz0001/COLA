<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
       xmlns:cola="http://repo.alibaba-inc.com/schema/cola"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
	http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
	http://repo.alibaba-inc.com/schema/cola http://repo.alibaba-inc.com/schema/cola-mock.xsd
   ">

    <bean class="com.alibaba.cola.mock.spring.ColaMockPropertiesResolve"/>
    <context:component-scan base-package="${basePackage}" >
    </context:component-scan>

    <cola:cola-mock base-package="${basePackage}"/>
</beans>