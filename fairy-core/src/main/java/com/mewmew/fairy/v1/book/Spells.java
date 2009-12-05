package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.spell.Spell;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class Spells
{
    static final Map<String, Class<? extends Spell>> spells = new HashMap<String, Class<? extends Spell>>();
    static {
        add(Cat.class);
        add(Head.class);
    }

    public static Class<? extends Spell> add(Class<? extends Spell> spell)
    {
        spells.put(spell.getSimpleName().toLowerCase(), spell);
        return spell ;
    }

    public static Iterable<Class<? extends Spell>> getSpells()
    {
        return spells.values() ;
    }

    public static Class<? extends Spell> getSpell(String name)
    {
        return spells.get(name);
    }


}
