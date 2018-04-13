<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="${statusLevel}">

    <Appenders>
    
      <#list appenders as appender>
        <#if appender.clazz == "org.apache.log4j.RollingFileAppender">
        <RollingFile name="${appender.name}">
            <PatternLayout pattern="${appender.layout.param?first.value}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="30 MB"/>
            </Policies>
            <DefaultRolloverStrategy max="2"/>
        </RollingFile>
        
        </#if>
        <#if appender.clazz == "org.apache.log4j.ConsoleAppender">
        <Console name="${appender.name}" target="STDOUT">
        </Console>
        
        </#if>
      </#list>
    </Appenders>
    
    <Loggers>
      <#list loggers as logger>
        <Logger name="${logger.name}"<#if logger.level??> level="${logger.level.value}"</#if><#if !logger.additivity?boolean> additivity="${logger.additivity}"</#if>>
          <#if (logger.appenderRef?size > 0)>
            <#list logger.appenderRef as appender>
            <AppenderRef ref="${appender.ref}"/>
            </#list>
          </#if>
        </Logger>
        
      </#list>
        <Root level="root.level">
          <#list root.appenderRef as appender>
            <AppenderRef ref="${appender.ref}"/>
          </#list>
        </Root>      
    
    </Loggers>
    
</Configuration>
