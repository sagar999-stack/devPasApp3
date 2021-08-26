package com.example.devposapp2;

public class SpaceManager {
    private static SpaceManager INSTANCE = null;
    private static Object mutex = new Object();
    private SpaceManager() {

    }
    public static SpaceManager getInstance() {
        synchronized (mutex) {
            if (INSTANCE == null) {
                INSTANCE = new SpaceManager();
            }
        }
        return(INSTANCE);
    }
    public String getSpace(String firstText, String lastText,String total){


         String space="";
        int totalLen = total.length();
        int firstTextLen = firstText.length();
        int lastTextLen = lastText.length();
        int spaceLen = totalLen-(firstTextLen+lastTextLen);
        for(int j=spaceLen;j>0;j--){
            space=space+" ";
        }
        return space;
    }
    public String sideSpace(String firstText, String total){


        String space="";
        int totalLen = total.length();
        int firstTextLen = firstText.length();

        int spaceLen = totalLen-firstTextLen;
        for(int j=spaceLen/2;j>0;j--){
            space=space+" ";
        }
        return space;
    }
}
