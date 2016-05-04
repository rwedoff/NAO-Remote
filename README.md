# NAOControllerAndroidApp
## NAO Remote

###Installation Instructions
  1.  Install the Android Application *NAO Remote* from Google Play 
  2.  Get Choregraphe Installed on a computer:  Found on [Aldebaran's Website](https://www.aldebaran.com/en)
  3.  Download  AndroidTheater.crg:  Found [Here](https://github.com/szecsei/NAOAndroidTheater)
  4.  Install NAO Server Chat or Python Server Chat Found [Here](https://github.com/rwedoff/NAOServerChat/releases) 

###Running the Application
  To use this app you have to have completed all the installation requirements.  You will need a NAO Robot or a Virtual NAO using Choregraphe 2.1~.  (Note, the app will work
  with either firmware, but for a Virtual NAO you need to use the newest version of Choregraphe).
  This application combines three parts, the Android App, the socket chat, and the AndroidTheater.crg.  You can find links to all of these right here:
        
  +  Android App (NAO Remote)
  +  [AndroidTheater.crg](https://github.com/szecsei/NAOAndroidTheater)
  +  [Server Chat](https://github.com/rwedoff/NAOServerChat/releases)
    
  For Windows users, you can use NAO Server Chat, a chat written in C# (Note, Windows Firewall by default doesn't open Port 5000.  Port 8002 is used instead). For Mac and Linux users, use PythonServerChat.py (Simply run it in terminal).
  Run the socket and get the IP Address of the server.
  Next, open Choregraphe and enter the IP Address and Port in the Server Script Box.  
  Connect to a robot, virtual or real.  Robot firmware does not matter.
  The robot will now listen to teh socket chat on the given port (Windows 8002 is recommended).
  
  Next open the Android App.  Type in the IP Address and the Port from the server.  Make sure you are on the same network as the server.
  Tap connect, and type in the Robot's Name[s].  
  
###App Uses
This application was created for the University of Iowa, primarily to aid the production of Robot Theater.
#####Remote
  The remote funtion creates a screen for each robot.  You can make the robot stand, crouch, wave, sit, say text, move its head, walk, and turn.
#####Mood
  The mood remote takes a Robot Name, a Mood and then you can send text.  The robot will say the text and will do actions related to the mood.
#####File
  The file function takes in a file of robot commands and send the file line by line.  The application will give various error to see what went wrong.
  The file function also has a syntax checker.
######File Syntax:
  * `--NAOSTART`  > This is needed at the beginning of every NAO File
  * `--NAOSTOP`   > This is needed at the end of every NAO File
  * `<Robot Name>;<Command>;`
  
  Possible Commands
  * `Speech;<text>`
  * `Mood;<Neutral, Happy, Sad, Angry>;<text>`
  * `Wave`
  * `SitDown`
  * `StandUp`
  * `Crouch`
  * `RightX=<#>`
  * `RightY=<#>`
  * `Theta=<#>`
  * `LeftX=<#>`
  * `LeftY=<#>`
  
##### Socket Connection
  The socket connection is just a generic socket client to send messages to the socket chat.
  

### Troubleshooting 
+ Check that the server is running.
+ Check that Choregraphe has the right IP Address and Port given by the server.
+ Check that the server, Choregraphe, and NAO Remote are all on the same network.
+ Check that NAO Remote has the right IP Address and Port given by the server.
+ Make sure robot names are added.
+ Make sure the robot name is spelled correctly, given by the connected robot.
+ Make sure AndroidTheater.crg is running on the robot.
+ Make sure the app is connected to the server.

 
#### Sources / Acknowledgments
+ JoystickView by AJ Alves is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License
+ GSON: Licensed under the Apache License, Version 2.0 (the "License");
+ Material Design Icons: https://design.google.com/icons/
+ The University of Iowa
+ Denise Szecsei
+ Aldebaran's NAO Robots
