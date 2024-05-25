package com.rock_mc.syn.command;

public class Command {
    public String name;
    public String description;
    public String usage;
    public String permission;

    public Command(String name, String description, String usage, String permission) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.permission = permission;
    }
}

