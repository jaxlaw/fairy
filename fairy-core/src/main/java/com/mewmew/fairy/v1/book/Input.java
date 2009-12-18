package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.spell.Help;
import com.mewmew.fairy.v1.spell.Spell;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.cli.Args;
import com.mewmew.fairy.v1.json.JsonRegistry;
import com.mewmew.fairy.v1.json.JsonOutput;
import com.mewmew.fairy.v1.json.OutputFormat;
import com.mewmew.fairy.v1.pipe.Output;

import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

@Help(desc = "manage input formats")
public class Input extends Spell
{
    @Param(desc = "register input type")
    String register;
    @Param(desc = "list all registered input")
    boolean list;
    @Args(desc = "input pipe to register")
    String args[];

    JsonRegistry registry = new JsonRegistry("inputs.json");

    @Override
    public void cast()
    {
        if (list) {
            registry.list(getOutputStream(), "name");
            return;
        }
        if (!StringUtils.isEmpty(register)) {
            Map<String, Object> json = new HashMap<String, Object>();
            json.put("name", register);
            json.put("pipe", args) ;
            try {
                registry.add(json);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
