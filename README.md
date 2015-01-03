# Mephisto III

The project is a re-implementation of the https://github.com/syd711/mephisto_fx 
radio UI and based on the callete framework: https://github.com/syd711/callete

# Development Setup

The project was developed with Intellij IDEA, so some of the features described may not 
work in an Eclipse environment:

Clone the https://github.com/syd711/callete project and following the setup instructions described
on the github README.md.

Next, have a look on the *conf/callete.properties* file. The file contains all the necessary settings
to connect to the Google Music account, EchoNest for artist image retrieval, MPD connection, etc...
Once you've imported the maven project into IDEA and configured the callete.properties, you should be able
to start the UI locally using a remote MPD connection from your Raspberry Pi.

# Deployment

The project uses the deployment server of the callete framework. Basically it creates a zip file
with all stuff you need to execute the project on another system. Have a look on the *pom.xml* file
that contains all deployment options. If you're missing something, feel free to change the deployment service in the 
https://github.com/syd711/callete project.

If you have the callete deployment service running, configure the deployment host in the *callete.properties*
and create a new maven run configuration with target "install" and profile "deploy". This will create 
the deployment archive that is then copied via HTTP call to the target environment and executed there.

For more details, check out the https://github.com/syd711/callete-template project.

# Screenshots

Here are some screenshots of the UI:

![](http://www.paderpoint.net/radio/iii-new/radio1.png)

![](http://www.paderpoint.net/radio/iii-new/radio2.png)

![](http://www.paderpoint.net/radio/iii-new/radio3.png)

![](http://www.paderpoint.net/radio/iii-new/radio4.png)

![](http://www.paderpoint.net/radio/iii-new/radio5.png)

![](http://www.paderpoint.net/radio/iii-new/radio6.png)

![](http://www.paderpoint.net/radio/iii-new/radio7.png)

![](http://www.paderpoint.net/radio/iii-new/radio8.png)

![](http://www.paderpoint.net/radio/iii-new/radio9.png)

![](http://www.paderpoint.net/radio/iii-new/radio10.png)

![](http://www.paderpoint.net/radio/iii-new/radio11.png)

