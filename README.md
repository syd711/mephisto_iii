# Mephisto III

The project is a re-implementation of the https://github.com/syd711/mephisto_fx 
radio UI and based on the callete framework: https://github.com/syd711/callete

# Known Issues

* The rotary encoder is not working with 100% precision. Sometimes the wrong direction is detected, also sometimes the events lag.
* Unfortunately the tracks of the albums are not sorted. I'm using jkiddo's gmusic api which does not provide the necessary information.

# Development Setup

The project was developed with Intellij IDEA, so some of the features described may not 
work in an Eclipse environment:

Clone the https://github.com/syd711/callete project and following the setup instructions described
on the github README.md.

Next, have a look on the *conf/callete.properties* file. The file contains all the necessary settings
to connect to the Google Music account, EchoNest for artist image retrieval, MPD connection, etc...
Once you've imported the maven project into IDEA and configured the callete.properties, you should be able
to start the UI locally using a remote MPD connection from your Raspberry Pi.

# Raspberry Pi Setup

* Apply the changes on the alsa-base.conf file to enable USB sound output. According to some documentations the 
MPD must be configured too, but this was not necessary in my case.
* Apply the changes on the /boot/config.txt. I've put my settings in the conf folder of the project.
* Adjust window position: the callete.properties file contains settings to configure the x and y coordinates for the window.
 If not set, the window will be centered (which may not work properly, at least with the display I am using).
* Adjust logging: the generated run.sh file will use the logback.xml file from the conf folder. You may want to enable
the console logging during the development process.
* callete-deployment.properties: this file is used when the deployable archive is created, so you can use different
settings for development and the actual deployment.
* file encoding: the deployment archiver applies utf8 as default encoding for the Java process. If you need another one,
you have to modify the callete project.
* following the additional instructions on https://github.com/syd711/callete

# Deployment

The project uses the deployment server of the callete framework. Basically it creates a zip file
with all stuff you need to execute the project on another system. Have a look on the *pom.xml* file
that contains all deployment options. If you're missing something, feel free to change the deployment service in the 
https://github.com/syd711/callete project.

If you have the callete deployment service running, configure the deployment host in the *callete.properties*
and create a new maven run configuration with target "install" and profile "deploy". This will create 
the deployment archive that is then copied via HTTP call to the target environment and executed there.

For more details, check out the https://github.com/syd711/callete-template project.

