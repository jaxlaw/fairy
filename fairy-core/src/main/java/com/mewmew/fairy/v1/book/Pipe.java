package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.cli.AnnotatedCLI;
import com.mewmew.fairy.v1.cli.Args;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.spell.PipeSpell;
import com.mewmew.fairy.v1.spell.Spell;
import com.mewmew.fairy.v1.spell.Spells;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Pipe extends PipeSpell
{
    @Param
    String delimiter = "pipe";
    @Args
    String[] args;

    List<PipeSpell> pipes = new ArrayList<PipeSpell>();

    public Pipe()
    {
    }

    public Pipe setup(List<String[]> spells) throws Exception
    {
        for (String[] args : spells) {
            Spell spell = toSpell(args);
            pipes.add((PipeSpell) spell);
        }
        return this;
    }

    public static Spell toSpell(String[] args)
            throws Exception
    {
        String nargs[] = new String[args.length - 1];
        System.arraycopy(args, 1, nargs, 0, nargs.length);
        Class<? extends Spell> c = Spells.getSpell(args[0]);
        if (c == null) {
            throw new IllegalArgumentException(String.format("unknown spell %s", args[0]));
        }
        AnnotatedCLI cli = AnnotatedCLI.getMagicCLI(c);
        Spell spell = cli.getInstance(c, nargs);
        return spell;
    }

    @Override
    public void process(InputStream in, OutputStream out)
    {
        try {
            setup(parseSpells(args, delimiter));
            PipedOutputStream pout = null;
            final CountDownLatch latch = new CountDownLatch(pipes.size());
            for (int i = pipes.size() - 1; i >= 0; i--) {
                PipeSpell ps = pipes.get(i);
                if (i == pipes.size() - 1) {
                    new PipeThread(ps, new PipedInputStream(pout = new PipedOutputStream()), out, latch).start();
                }
                else if (i == 0) {
                    new PipeThread(ps, in, pout, latch).start();
                }
                else {
                    PipedOutputStream temp = new PipedOutputStream();
                    new PipeThread(ps, new PipedInputStream(temp), pout, latch).start();
                    pout = temp ;
                }
            }
            latch.await();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Pipe parsePipe(String[] args, String delim) throws Exception
    {
        List<String[]> spells = parseSpells(args, delim);
        return new Pipe().setup(spells);
    }

    public static List<String[]> parseSpells(String[] args, String delim)
    {
        List<String[]> spells = new ArrayList<String[]>();
        List<String> current = new ArrayList<String>();
        for (String v : args) {
            if (v.equalsIgnoreCase(delim) || v.equalsIgnoreCase("|")) {
                if (current.size() > 0) {
                    spells.add(current.toArray(new String[0]));
                }
                current = new ArrayList<String>();
            }
            else {
                current.add(v);
            }
        }
        if (current.size() > 0) {
            spells.add(current.toArray(new String[0]));
        }
        return spells;
    }

    static class PipeThread extends Thread
    {
        private final PipeSpell spell;
        private final InputStream in;
        private final OutputStream out;
        private final CountDownLatch latch;

        PipeThread(PipeSpell spell, InputStream in, OutputStream out, CountDownLatch latch)
        {
            this.spell = spell;
            this.in = in;
            this.out = out;
            this.latch = latch;
            setDaemon(true);
        }

        @Override
        public void run()
        {
            try {
                spell.process(in, out);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                try {
                    spell.after();
                }
                catch (Exception e) {                    
                }
                latch.countDown();
            }
        }
    }
}
