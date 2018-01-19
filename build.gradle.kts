import org.junit.platform.gradle.plugin.JUnitPlatformExtension
import org.junit.platform.gradle.plugin.JUnitPlatformPlugin

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.1")
    }
}

plugins {
    kotlin("jvm") version ("1.1.51")

    id("org.jenkins-ci.jpi") version ("0.22.0")
}
apply<JUnitPlatformPlugin>()

repositories {
    jcenter()
}

val kotlinVersion by project
val thymeleafVersion by project
val javaxMailVersion by project
val javaxServletVersion by project
val jGitVersion by project
val junitVersion by project
val powerMockVersion by project
val hamcrestVersion by project

val jenkinsMailerPluginVersion by project
val jenkinsCredentialsPluginVersion by project
val jenkinsWorkflowPluginVersion by project
val jenkinsCoreVersion by project

dependencies {
    compile(kotlin("stdlib-jre8", "$kotlinVersion"))
    compile("com.sun.mail:javax.mail:$javaxMailVersion")
    compile("org.thymeleaf:thymeleaf:$thymeleafVersion")
    compile("org.jenkins-ci.main:jenkins-core:$jenkinsCoreVersion")
    compile("javax.servlet:javax.servlet-api:$javaxServletVersion")
    compile("org.eclipse.jgit:org.eclipse.jgit:$jGitVersion")

    jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-api:$jenkinsWorkflowPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-job:$jenkinsWorkflowPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins:mailer:$jenkinsMailerPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins:credentials:$jenkinsCredentialsPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-step-api:$jenkinsWorkflowPluginVersion@jar")

    testCompile("org.powermock:powermock-module-junit4:$powerMockVersion")
    testCompile("org.powermock:powermock-api-mockito2:$powerMockVersion")
    testCompile("junit:junit:$junitVersion")
    testCompile("org.hamcrest:hamcrest-all:$hamcrestVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

jenkinsPlugin {
    displayName = "Jarvis"
    shortName = "jarvis"
    gitHubUrl = "https://github.com/madhead/jarvis"

    coreVersion = jenkinsCoreVersion as String?
    compatibleSinceVersion = coreVersion
    fileExtension = "jpi"
    pluginFirstClassLoader = false
}

task<Wrapper>("wrapper") {
    gradleVersion = "4.3"
    distributionType = Wrapper.DistributionType.ALL
}