package com.sevenstringargs.rost_gateway;

import java.util.HashMap;

public class CommandStore {
    private static HashMap<String, String> fromCommand = new HashMap<>();
    private static HashMap<String, String> toCommand = new HashMap<>();

    public synchronized static void clearPair(){
        clearFromCommand();
        clearToCommand();
    }

    public synchronized static void clearFromCommand(){
        toCommand.clear();
    }

    public synchronized static void clearToCommand(){
        fromCommand.clear();
    }

    public synchronized static void addPair(String from, String to){
        if (!fromCommand.containsKey(from) && !toCommand.containsKey(to) && !from.equals(to)){
            fromCommand.put(from, to);
            toCommand.put(to, from);
        }
    }

    public synchronized static String getTo(String from){
        if (from == null){
            return null;
        }

        return fromCommand.get(from);
    }

    public synchronized static String getFrom(String to){
        if (to == null){
            return null;
        }

        return toCommand.get(to);
    }
}
