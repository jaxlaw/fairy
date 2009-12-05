package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.cli.Param;

import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Spell
{
    @Param (desc = "output file, default standard out")
    private File outputFile ;
    @Param (desc = "append to output file", defaultValue = "false")
    private boolean append = false;
    @Param (desc = "print help", defaultValue = "false")
    private boolean help = false;

    private OutputStream out ;

    public void before()
    {
        if (outputFile != null) {
            try {
                out = new FileOutputStream(outputFile, append);
            }
            catch (FileNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        else {
            out = System.out ;
        }
    }

    public void after()
    {
        try {            
            out.close();
        }
        catch (IOException e) {
        }
    }

    protected OutputStream getOutputStream()
    {
        return this.out;
    }
    
    public abstract void cast();

    public boolean isHelp()
    {
        return help;
    }
}
