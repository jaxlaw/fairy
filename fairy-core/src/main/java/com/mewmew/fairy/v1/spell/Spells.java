package com.mewmew.fairy.v1.spell;

import com.mewmew.fairy.v1.spell.Spell;

import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.IOException;

public class Spells
{
    static final Map<String, Class<? extends Spell>> spells = new HashMap<String, Class<? extends Spell>>();

    static {
        try {
            magicallyLoadAllSpells();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<? extends Spell> add(Class<? extends Spell> spell)
    {
        spells.put(spell.getSimpleName().toLowerCase(), spell);
        return spell;
    }

    public static Iterable<Class<? extends Spell>> getSpells()
    {
        return spells.values();
    }

    public static Class<? extends Spell> getSpell(String name)
    {
        return spells.get(name);
    }

    static void addSpells(File f) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException
    {
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                addSpells(file);
            }
        }
        else if (f.isFile()) {
            if (f.getName().endsWith(".class") && !f.getName().contains("$") && f.getPath().contains("book")) {
                String c = f.getName().substring(0, f.getName().length() - ".class".length());
                addSpell("com.mewmew.fairy.v1.book" + "." + c);
            }
            else if (f.getName().endsWith(".jar") && f.exists()) {
                ZipFile zip = new ZipFile(f);
                for (Enumeration em = zip.entries(); em.hasMoreElements();) {
                    ZipEntry ze = (ZipEntry) em.nextElement();
                    String c = ze.getName();
                    if (c.contains("book") && c.endsWith(".class") && !c.contains("$")) {
                        String name = c.substring(0, c.length() - ".class".length()).replaceAll("/", ".");
                        addSpell(name);
                    }
                }
            }
        }
    }

    private static void addSpell(String c)
            throws ClassNotFoundException
    {
        Class sc = Class.forName(c);
        if (Spell.class.isAssignableFrom(sc)) {
            Class<Spell> scc = (Class<Spell>) sc;
            add(scc);
        }
    }

    static void magicallyLoadAllSpells() throws Exception
    {
        if (System.getProperty("java.class.path") != null) {
            final String classpath = System.getProperty("java.class.path");
            for (String path : classpath.split(File.pathSeparator)) {
                File f = new File(path);
                addSpells(f);
            }
        }
    }
}
