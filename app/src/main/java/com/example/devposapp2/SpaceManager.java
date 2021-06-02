package com.example.devposapp2;

public class SpaceManager {
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
}
