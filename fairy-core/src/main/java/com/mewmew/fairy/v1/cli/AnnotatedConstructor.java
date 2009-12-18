package com.mewmew.fairy.v1.cli;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

class AnnotatedConstructor
{
    Class clazz;
    List<AnnotatedParam> params = new ArrayList<AnnotatedParam>();
    Constructor ctor;

    AnnotatedConstructor(Class clazz, Constructor ctor)
    {
        this.clazz = clazz;
        this.ctor = ctor;
    }

    public AnnotatedConstructor addParam(Param p, Class type)
    {
        params.add(new AnnotatedParam(p, type));
        return this;
    }

    public boolean isValid()
    {
        return params.size() == ctor.getParameterTypes().length;
    }

    public Object newInstance(AnnotatedCLI.ParsedCLI cli) throws Exception
    {
        // TODO : may be move this back to AnnotatedCLI ?
        Object param[] = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            AnnotatedParam ap = params.get(i);
            if (cli.cmd.hasOption(ap.getParam().option())) {
                if (ap.getType() == boolean.class || ap.getType() == Boolean.class) {
                    param[i] = true ;
                }
                else {
                    param[i] = cli.cli().parsers.parse(ap.getType(), cli.cmd.getOptionValue(ap.getParam().option()));
                }
            }
            else {
                String defaultValue = ap.getParam().defaultValue();
                if (defaultValue.length() > 0) {
                    param[i] = cli.cli().parsers.parse(ap.getType(), cli.cmd.getOptionValue(ap.getParam().option(), defaultValue));
                }
            }
        }
        return ctor.newInstance(param);
    }

    class AnnotatedParam
    {
        Param param;
        Class type;

        AnnotatedParam(Param param, Class type)
        {
            this.param = param;
            this.type = type;
        }

        public Param getParam()
        {
            return param;
        }

        public Class getType()
        {
            return type;
        }
    }

    List<AnnotatedParam> getParams()
    {
        return params;
    }
}
