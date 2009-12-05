package com.mewmew.fairy.v1.cli;

import com.mewmew.fairy.v1.cli.Param;

import java.lang.reflect.Field;

class AnnotatedOption
{
    final Class clazz ;
    final Field field ;
    final Param param ;
    String opt , name;

    AnnotatedOption(Class clazz, Field field, Param param)
    {
        this.clazz = clazz;
        this.field = field;
        this.param = param;

        if (param.option().isEmpty()) {
            opt = field.getName().substring(0,1).toLowerCase();
        }
        else {
            opt = param.option() ;
        }

        if (param.name().isEmpty()) {
            name = field.getName().toLowerCase();
        }
        else {
            name = param.name() ;
        }
    }

    public String toString()
    {
        return String.format("AnnotatedOption(%s %s %s %s)", clazz, field, opt, name);
    }

    public Param getParam()
    {
        return param;
    }

    public String getOpt()
    {
        return opt;
    }

    public String getName()
    {
        return name;
    }

    public void setOpt(String s)
    {
        opt = s ;
    }
}
