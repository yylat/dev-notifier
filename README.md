# dev-notifier
DevNotifier is a plugin for email notifications about Jenkins builds.

An attempt to mimic Travis CI emails style.

# how it looks
![DevNotifier email](/.github/images/devnotifier.jpg)

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
                userRemoteConfigs: [[url: 'https://github.com/vhs21/dev-notifier']]
                ])
            }
        }
    }
    post {
        always{
            script{
                notifyDev()
            }
        }
    }
}
```

To send emails there is also need in configuring Mailer plugin (Manage Jenkins &rarr; Configure System &rarr; E-mail Notification):

![Mailer config](/.github/images/mailer_config.jpg)
