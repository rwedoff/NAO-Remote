package com.ryanwedoff.senor.naoservercontroller;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.LargeTest;


import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */

@LargeTest
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testFileParse(){
        /**
         *Tests that check the NaoFileParsee
         * @see NaoFileParse
         */

        ArrayList<String> name = new ArrayList<>();
        name.add("Ryan");
        String[]  m = {"Sad"};
        NaoFileParse fileParse = new NaoFileParse(name,m);
        assertEquals(true, fileParse.firstCheckLine("--NAO-START"));
        assertEquals(true, fileParse.lastCheckLine("--NAO-STOP"));
        assertEquals(true, fileParse.checkLine("Ryan;ButtonA;", 12));
        assertEquals(false, fileParse.checkLine("Bob;ButtonA;", 12));
        assertEquals(true, fileParse.checkLine("Ryan;ButtonX;",12));
        assertEquals(true, fileParse.checkLine("Ryan;ButtonB;",12));
        assertEquals(true, fileParse.checkLine("Ryan;Speech;",12));
        assertEquals(false, fileParse.checkLine("Ryan;Mood;",12));
        assertEquals(true, fileParse.checkLine("Ryan;Mood;Sad",12));
        assertEquals(false, fileParse.checkLine("Ryan;Mood;Upset",12));
        assertEquals(false, fileParse.checkLine("Ryan",12));
        assertEquals(true, fileParse.checkLine("Ryan;Theta=0;",12));
        assertEquals(false, fileParse.checkLine("Ryan;Theta=;",12));
        assertEquals(false, fileParse.checkLine("Ryan;Theta12345230;",12));
        assertEquals(true, fileParse.checkLine("Ryan;RightX=0;",12));
        assertEquals(true, fileParse.checkLine("Ryan;RightY=0;",12));
        assertEquals(true, fileParse.checkLine("Ryan;LeftX=0;",12));
        assertEquals(true, fileParse.checkLine("Ryan;LeftY=0;",12));

    }
}