import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version ("1.2.31")
    kotlin("kapt") version ("1.2.31")

    id("org.jenkins-ci.jpi") version ("0.25.0")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

repositories {
    maven("http://repo.jenkins-ci.org/public")
    jcenter()
}

val kotlinVersion by project
val thymeleafVersion by project
val javaxMailVersion by project
val javaxServletVersion by project
val jGitVersion by project
val jupiterVersion by project
val hamcrestVersion by project
val log4jVersion by project

val jenkinsMailerPluginVersion by project
val jenkinsCredentialsPluginVersion by project
val jenkinsWorkflowPluginVersion by project
val jenkinsCoreVersion by project

val sezpozVersion by project

dependencies {
    implementation(kotlin("stdlib-jre8", "$kotlinVersion"))
    implementation("com.sun.mail:javax.mail:$javaxMailVersion")
    implementation("org.thymeleaf:thymeleaf:$thymeleafVersion")
    implementation("org.eclipse.jgit:org.eclipse.jgit:$jGitVersion")

    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")

    jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-api:$jenkinsWorkflowPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-job:$jenkinsWorkflowPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins:mailer:$jenkinsMailerPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins:credentials:$jenkinsCredentialsPluginVersion@jar")
    jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-step-api:$jenkinsWorkflowPluginVersion@jar")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${jupiterVersion}")

    kapt("net.java.sezpoz:sezpoz:${sezpozVersion}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

kapt {
    correctErrorTypes = true
}

jenkinsPlugin {
    displayName = "Jarvis"
    shortName = "jarvis"
    gitHubUrl = "https://github.com/vhs21/jarvis"

    coreVersion = jenkinsCoreVersion as String?
    compatibleSinceVersion = coreVersion
    fileExtension = "jpi"
    pluginFirstClassLoader = false

    developers = this.Developers().apply {
        developer(delegateClosureOf<JpiDeveloper> {
            setProperty("id", "madhead")
            setProperty("name", "Siarhei Krukau")
            setProperty("email", "siarhei.krukau@gmail.com")
            setProperty("url", "https://madhead.me")
            setProperty("timezone", "UTC+3")
        })
        developer(delegateClosureOf<JpiDeveloper> {
            setProperty("id", "vhs21")
            setProperty("name", "Uladzislau Shamionak")
            setProperty("email", "uladzislau.shamionak@gmail.com")
            setProperty("timezone", "UTC+3")
        })
    }

    licenses = this.Licenses().apply {
        license(delegateClosureOf<JpiLicense> {
            setProperty("url", "http://www.apache.org/licenses/LICENSE-2.0")
        })
    }
}

tasks.withType(KotlinCompile::class.java).all {
    dependsOn("localizer")

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType(KaptTask::class.java).all {
    outputs.upToDateWhen { false }
}

tasks.withType(KaptGenerateStubsTask::class.java).all {
    outputs.upToDateWhen { false }
}

task<Wrapper>("wrapper") {
    gradleVersion = "4.6"
    distributionType = Wrapper.DistributionType.ALL
}