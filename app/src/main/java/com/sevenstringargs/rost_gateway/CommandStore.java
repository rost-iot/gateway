package com.sevenstringargs.rost_gateway;

import java.util.HashMap;

public class CommandStore {
    private static HashMap<String, String> commands = new HashMap<>();

    public synchronized static void clearCommands(){
        commands.clear();
    }

    public synchronized static void addPair(String from, String to){
            commands.put(from, to);
    }

    public synchronized static String getTo(String from){
        return commands.get(from);
    }

}
