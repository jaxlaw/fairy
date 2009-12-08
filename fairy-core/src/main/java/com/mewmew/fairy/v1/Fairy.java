package com.mewmew.fairy.v1;

import com.mewmew.fairy.v1.spell.Spells;
import com.mewmew.fairy.v1.spell.Spell;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.AnnotatedCLI;

public class Fairy
{
    private final String[] args;
    private static final String helpMsg = "java -jar fairy.jar %s <args>";
    private static final String VERSION = "0.1";

    public Fairy(String[] args)
    {
        this.args = args;
    }

    private void doMagic()
    {
        if (args.length == 0) {
            System.out.printf("+---------+\n");
            System.out.printf("|Fairy %s|\n", VERSION);
            System.out.printf("+---------+\n");
            System.out.printf(helpMsg, "<spell>");
            System.out.printf("\n\navailable spells :\n\n");
            for (Class<? extends Spell> arg : Spells.getSpells()) {
                Help help = arg.getAnnotation(Help.class);
                System.out.printf("%20s - %s ...\n", arg.getSimpleName().toLowerCase(), help != null ? help.desc() : "");
            }
            System.out.println();
            System.out.println();
        }
        else {
            String nargs[] = new String[args.length - 1];
            System.arraycopy(args, 1, nargs, 0, nargs.length);

            Class<? extends Spell> c = Spells.getSpell(args[0]);
            if (c != null) {
                try {
                    AnnotatedCLI cli = AnnotatedCLI.getMagicCLI(c);
                    Spell spell = cli.getInstance(c, nargs);
                    if (spell.isHelp()) {
                        Help annotation = c.getAnnotation(Help.class);
                        String spellName = c.getSimpleName().toLowerCase();
                        System.err.printf("%s : %s\n%s : %s\n\n", "Spell", spellName, "Description",                               
                                annotation != null ? annotation.desc() : spellName);
                        cli.printHelp(String.format(helpMsg, spellName));
                    }
                    else {
                        spell.cast();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.err.printf("unknown spell %s", args[0]);
            }
        }

    }

    public static void main(String[] args)
    {
        new Fairy(args).doMagic();
    }

}
