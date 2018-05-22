<?xml version="1.0" encoding="UTF-8"?>
${comments.log4jconfiguration}
<Configuration status="${statusLevel}">

    <Appenders>
    
      <#list appenders as appender>
        ${comments[appender.name]!}
        <#list appender.param as param>
          <#if param.name == "File">
            <#assign fileName=param.value>
          </#if>
          <#if param.name == "MaxFileSize">
            <#assign maxFileSize=param.value>
          </#if>
          <#if param.name == "MaxBackupIndex">
            <#assign maxBackupIndex=param.value>
          </#if>
          <#if param.name == "Append">
            <#assign append=param.value>
          </#if>
        </#list>
        <#if appender.clazz == "org.apache.log4j.RollingFileAppender">
        <RollingFile name="${appender.name}" fileName="${fileName}">
            <PatternLayout pattern="${appender.layout.param?first.value}"/>
            <Policies>
                <SizeBasedTriggeringPolicy size="${maxFileSize}"/>
            </Policies>
            <DefaultRolloverStrategy max="${maxBackupIndex}"/>
        </RollingFile>
        
        </#if>
        <#if appender.clazz == "org.apache.log4j.FileAppender">
        <File name="${appender.name}" fileName="${fileName}" append="${append}">
            <PatternLayout pattern="${appender.layout.param?first.value}"/>
            <#list appender.param as p>
                <#if p.name == "Threshold">
            <Filter type="ThresholdFilter" level="${p.value}"/>
                </#if>
            </#list>
        </File>
        
        </#if>
        <#if appender.clazz == "org.apache.log4j.ConsoleAppender">
        <Console name="${appender.name}" target="STDOUT">
            <PatternLayout pattern="${appender.layout.param?first.value}"/>
        </Console>
        
        </#if>
      </#list>
    </Appenders>
    
    <Loggers>
      <#list loggers as logger>
        ${comments[logger.name]!}
        <Logger name="${logger.name}"<#if logger.level??> level="${logger.level.value}"</#if><#if !logger.additivity?boolean> additivity="${logger.additivity}"</#if><#if (logger.appenderRef?size == 0)>/</#if>>
          <#if (logger.appenderRef?size > 0)>
            <#list logger.appenderRef as appender>
            <AppenderRef ref="${appender.ref}"/>
            </#list>
        </Logger>
          </#if>
        
      </#list>
        ${comments.root!}
        <Root level="${root.priorityOrLevel?first.value}">
          <#list root.appenderRef as appender>
            <AppenderRef ref="${appender.ref}"/>
          </#list>
        </Root>      
    
    </Loggers>
    
</Configuration>
