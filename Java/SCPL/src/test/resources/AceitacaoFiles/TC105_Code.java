package com.github.diogosmendonca.view;

import com.github.diogosmendonca.control.*;
import java.util.*;
import com.github.diogosmendonca.view.commands.*;

public class Main{

    private static String output = null;

    private static Map<String,Command> commands = new HashMap<>();

    static{
        commands.put("diciplineSpecificCommand", new DiciplineSpecificCommand());
        commands.put("studentSpecificCommand", new StudentSpecificCommand());
    }

    public static void main(String[] args){
        String result = null;
        if(args != null && args.length > 0){
            Command c = commands.get(args[0]);
            if(c != null)
                result = c.execute();
            else
                result = "Command not found";
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