/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package com.mewmew.fairy.v1;

import com.mewmew.fairy.v1.spell.Spells;
import com.mewmew.fairy.v1.spell.Spell;
import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.cli.AnnotatedCLI;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;

public class Fairy
{
    private final String[] args;
    private static final String helpMsg = "java -jar fairy.jar %s <options> <args>";
    private static final String VERSION = "0.1";

    public Fairy(String[] args)
    {
        this.args = args;
    }

    private void doMagic()
    {
        if (args.length == 0) {
            System.out.printf("+------------------+\n");
            System.out.printf("|Fairy %s         |\n", VERSION);
            System.out.printf("|       It's magic!|\n");
            System.out.printf("+------------------+\n");
            System.out.printf(helpMsg, "<spell>");
            System.out.printf("\n\navailable spells :\n");

            List<Class<? extends Spell>> list = new ArrayList<Class<? extends Spell>>(Spells.getSpells());
            Collections.sort(list, new Comparator<Class<? extends Spell>>()
            {
                public int compare(Class<? extends Spell> o1, Class<? extends Spell> o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            Package pkg = null;
            for (Class<? extends Spell> arg : list) {
                if (pkg == null || pkg != arg.getPackage()) {
                    pkg = arg.getPackage() ;
                    System.out.printf("\n%s:\n\n", pkg.getName());
                }
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
                        spell.printHelp(cli, String.format(helpMsg, args[0]));
                    }
                    else {
                        spell.run();
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
