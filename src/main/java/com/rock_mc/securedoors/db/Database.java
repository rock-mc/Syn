package com.rock_mc.securedoors.db;

public abstract class Database {

    public abstract void load();

    public abstract String getCodeCreateDate(String code);

    public abstract void removeCode(String code);

    public abstract void save();

    public abstract void close();

    public abstract void addCode(String code);

}
