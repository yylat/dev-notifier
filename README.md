# jarvis
Jarvis is a plugin for email notifications about Jenkins builds.

An attempt to mimic Travis CI emails style.

# how it looks
![Jarvis email](/.github/images/jarvis.jpg)

# usage
Configuration in build like freestyle project:

![Freestyle job config](/.github/images/build.jpg)


Configuration in pipeline:
```groovy
pipeline {
    agent any
    stages {
        stage('Clone sources') {
            steps {
                checkout([
                $class: 'GitSCM', 
                branches: [[name: '*/master']], 
                doGenerateSubmoduleConfigurations: false, 
                extensions: [[$class: 'AuthorInChangelog']], 
                submoduleCfg: [], 
                userRemoteConfigs: [[url: 'https://github.com/vhs21/jarvis']]
                ])
            }
        }
    }
    post {
        always{
            script{
                jarvisNotification()
            }
        }
    }
}
```

To send emails there is also need in configuring Mailer plugin (Manage Jenkins &rarr; Configure System &rarr; E-mail Notification):

![Mailer config](/.github/images/mailer_config.jpg)
