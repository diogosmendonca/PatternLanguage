package com.github.diogosmendonca.view;

import com.github.diogosmendonca.control.*;

public class Main{

    private static String output = null;

    public static void main(String[] args){
        String result = null;
        if(args != null && args.length > 0){
            if(args[0].equals("studentSpecificCommand")){
                result = ControlFacade.studentSpecificService();
            }else if (args[0].equals("diciplineSpecificCommand")){
                result = ControlFacade.diciplineSpecificService();
            }else{
                result = "Command not found";
            }
        }else{
            result = "Command not found";
        }
        System.out.println(result);
        output = result;
    }

    public static String getOutput(){
        return output;
    }

}