package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.cli.Param;

import java.util.List;

public class ExampleSpell
{
    @Param
    private final String string ;
    @Param
    private Double number ;
    @Param
    float n2 ;
    @Param
    List<String> b1 ;
    @Param
    boolean b2 ;

    public ExampleSpell()
    {
        string = null ;
    }

    public String toString()
    {
        return String.format("string = %s, number = %s, n2 = %s, b1 = %s, b2 = %s", string, number, n2, b1, b2);
    }
}
