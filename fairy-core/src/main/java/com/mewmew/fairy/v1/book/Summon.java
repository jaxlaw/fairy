package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.spell.PipeSpell;

import java.io.InputStream;
import java.io.OutputStream;

@Help(desc="start an interactive fairy shell")
public class Summon extends PipeSpell
{
    @Override
    public void process(InputStream in, OutputStream out)
    {

    }
}
