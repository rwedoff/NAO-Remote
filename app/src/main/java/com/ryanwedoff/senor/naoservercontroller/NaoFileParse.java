package com.ryanwedoff.senor.naoservercontroller;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class NaoFileParse {
        private final String[] SET_VALUES = new String[] {"ButtonA","ButtonB","ButtonX","Theta=","LeftY=","LeftX=","RightY=","RightX=","Speech","Mood"};
        private HashSet<String> commands = new HashSet<String>(Arrays.asList(SET_VALUES));
        private HashSet<String> moods;
        private HashSet<String> robotNames;

        public NaoFileParse(ArrayList<String> names, String [] moodFromArray){
            robotNames = new HashSet<String>(names);
            moods = new HashSet<String>(Arrays.asList(moodFromArray));
        }

        //Todo strip semicolons in mood text edit
        public boolean firstCheckLine(String line){
            return line.equals("--NAOSTART");
        }
        public boolean lastCheckLine(String line){
            return line.equals("--NAOSTOP");
        }
        public boolean checkLine(String line, int lineNum){
            /**
             * Checks the lines fed from FileActivty
             */
            String [] splitLine = line.split(";");
            String name = splitLine[0];
            if(!robotNames.contains(name)){
                Log.e(Integer.toString(lineNum),"Robot Name Not Found");
                return false;
            }
            try{
                String command = splitLine[1];
                if(command.contains("Theta") || command.contains("RightX") || command.contains("RightY") || command.contains("LeftX") || command.contains("LeftY")){
                    String []  equalSplit = command.split("=");
                    try{
                        String num = equalSplit[1];
                    } catch (Exception e){
                        Log.e(Integer.toString(lineNum),"No number given in command");
                        return false;
                    }
                } else{
                    if(!commands.contains(command)){
                        Log.e(Integer.toString(lineNum),"Invalid command");
                        return false;
                    }
                }
                if(command.equals("Mood")){
                    try{
                        String md = splitLine[2];
                        if(!moods.contains(splitLine[2])){
                            Log.e(Integer.toString(lineNum),"Invalid Mood");
                            return false;
                        }
                    } catch (Exception e){
                        Log.e(Integer.toString(lineNum), "No mood given");
                        return false;
                    }

                }
            }catch (Exception e){
                Log.e(Integer.toString(lineNum),"No command given");
                return  false;
            }
            return true;
        }
}

