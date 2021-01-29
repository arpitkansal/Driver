package com.example.driver;

import javax.xml.namespace.NamespaceContext;

public class Areas {
    private int id;
    private double lats;
    private double longs;
    private String  name;

    public Areas(int id, double lats, double longs, String  name )
    {
        this.id = id;
        this.name = name;
        this.lats = lats;
        this.longs = longs;
    }

    public int getId(){
        return id;
    }

    public String getName()
    {
        return name;
    }

    public double getLats()
    {
        return lats;
    }

    public double getLongs()
    {
        return longs;
    }
}
