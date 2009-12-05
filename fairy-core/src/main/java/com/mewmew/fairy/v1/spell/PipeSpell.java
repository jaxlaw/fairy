package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.pipe.Pipe;
import com.mewmew.fairy.v1.cli.Param;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class PipeSpell extends Spell implements Pipe
{
    @Param (desc= "input file, default standard in")
    private File inputFile ;
    private InputStream in ;

    protected PipeSpell()
    {
        in = null;
    }

    public PipeSpell(InputStream in)
    {
        this.in = in;
    }

    @Override
    public void before()
    {
        super.before();
        if (inputFile != null) {
            try {
                in = new FileInputStream(inputFile);
            }
            catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            in = System.in ;
        }
    }

    public InputStream getInputStream()
    {
        return in;
    }

    @Override
    public final void cast()
    {
        before();
        process(in, getOutputStream());
        after();
    }

    public abstract void process(InputStream in, OutputStream out);
}
