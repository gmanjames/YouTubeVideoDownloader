# Random YouTube Video Downloader  
A simple, experimental application that uses the [YouTube](https://youtube.com) data API and [PickVideo.net](https://pickvideo.net)  
to download a random video based on a keyword search.  

## How-To Use This Project
This project is built with [gradle](https://gradle.org/). It has the "application"  
plugin applied by default and may be ran with the following command:  
```
./gradlew run -Pkeyword=<searchterm>  
```  
replace <searchterm> with your chosen keyword

### What You'll Need
1. A Google API [authorization credentials](https://developers.google.com/youtube/registering_an_application)  
2. [JDK 9](http://www.oracle.com/technetwork/java/javase/downloads/jdk9-downloads-3848520.html)  

I have provided an example application properties file in the project root directory.  
You must update youtube.apiKey and targetPath (where you would like the video to be saved)  
with the proper information. Before you run the application, ensure that this file is in  
the Java classpath.  
