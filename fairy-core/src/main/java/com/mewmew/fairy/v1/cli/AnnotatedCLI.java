package com.mewmew.fairy.v1.cli;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.spell.ExampleSpell;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class AnnotatedCLI
{
    final Options options;
    final Multimap<Class, AnnotatedOption> mappings;
    final Unsafe unsafe = stealUnsafe();
    final StringParsers parsers = new StringParsers();

    public AnnotatedCLI(Class... clazz)
    {
        final List<AnnotatedOption> list = new ArrayList<AnnotatedOption>();
        for (Class aClass : clazz) {
            for (Field field : aClass.getDeclaredFields()) {
                Param param = field.getAnnotation(Param.class);
                if (param != null) {
                    list.add(new AnnotatedOption(aClass, field, param));
                }
            }
        }
        options = new Options();
        mappings = Multimaps.newMultimap(Maps.<Class, Collection<AnnotatedOption>>newHashMap(), new Supplier<Collection<AnnotatedOption>>()
        {
            public Collection<AnnotatedOption> get()
            {
                return new ArrayList<AnnotatedOption>();
            }
        });
        for (AnnotatedOption opt : list) {
            boolean hasArgs = !(opt.field.getType().equals(boolean.class) ||
                    opt.field.getType().equals(Boolean.class));
            while (options.hasOption(opt.getOpt())) {
                opt.setOpt(opt.getOpt() + opt.getOpt());
            }
            options.addOption(opt.getOpt(), opt.getName(), hasArgs, opt.getParam().desc());
            mappings.put(opt.clazz, opt);
        }
    }

    public static <T> AnnotatedCLI getMagicCLI(Class<T> c)
    {
        Stack<Class> stack = new Stack<Class>();
        Class p = c;
        while (p != Object.class) {
            stack.push(p);
            p = p.getSuperclass();
        }
        return new AnnotatedCLI(stack.toArray(new Class[stack.size()]));
    }

    public <T> T getInstance(Class<T> c, String[] args) throws Exception
    {
        T obj = (T) c.newInstance();
        parse(args).inject(obj);
        return obj;
    }

    public AnnotatedCLI addParser(StringParser t)
    {
        parsers.addParser(t);
        return this;
    }

    private void magicallySetField(Object obj, AnnotatedOption opt, Object value)
    {
        if (value == null) {
            return;
        }
        boolean wasInaccessible = !opt.field.isAccessible();
        if (wasInaccessible) {
            opt.field.setAccessible(true);
        }
        try {
            opt.field.set(obj, value);
        }
        catch (IllegalAccessException e) {
            System.err.println("diving into unsafe territory !");
            long offset = unsafe.objectFieldOffset(opt.field);
            if (value instanceof Integer) {
                unsafe.putInt(obj, offset, (Integer) value);
            }
            else if (value instanceof Long) {
                unsafe.putLong(obj, offset, (Long) value);
            }
            else if (value instanceof Boolean) {
                unsafe.putBoolean(obj, offset, (Boolean) value);
            }
            else if (value instanceof Float) {
                unsafe.putFloat(obj, offset, (Float) value);
            }
            else if (value instanceof Double) {
                unsafe.putDouble(obj, offset, (Double) value);
            }
            else if (value instanceof Byte) {
                unsafe.putByte(obj, offset, (Byte) value);
            }
            else if (value instanceof Character) {
                unsafe.putChar(obj, offset, (Character) value);
            }
            else if (value instanceof Short) {
                unsafe.putShort(obj, offset, (Short) value);
            }
            else {
                unsafe.putObject(obj, offset, value);
            }
        }
        opt.field.setAccessible(!wasInaccessible);
    }

    public ParsedCLI parse(String... args) throws ParseException
    {
        return new ParsedCLI(new GnuParser().parse(options, args));
    }

    public void printHelp(String msg)
    {
        new HelpFormatter().printHelp(msg, options);
    }

    public static void main(String[] args) throws ParseException
    {
        ExampleSpell spell = new ExampleSpell();
        new AnnotatedCLI(ExampleSpell.class)
                .parse(new String[]{"-s", "jax", "-n", "2", "-b", "1,2,3,4"})
                .inject(spell);
        System.out.println(spell);
    }

    private static Unsafe stealUnsafe()
    {
        try {
            Class objectStreamClass = Class.forName("java.io.ObjectStreamClass$FieldReflector");
            Field unsafeField = objectStreamClass.getDeclaredField("unsafe");
            unsafeField.setAccessible(true);
            return (Unsafe) unsafeField.get(null);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public class ParsedCLI
    {
        final CommandLine cmd;

        ParsedCLI(CommandLine cmd)
        {
            this.cmd = cmd;
        }

        public ParsedCLI inject(Object obj)
        {
            Class clazz = obj.getClass();
            while (clazz != Object.class) {
                Collection<AnnotatedOption> opts = mappings.get(clazz);
                if (opts != null) {
                    for (AnnotatedOption opt : opts) {
                        if (cmd.hasOption(opt.getOpt())) {
                            if (opt.field.getType() == boolean.class || opt.field.getType() == Boolean.class) {
                                magicallySetField(obj, opt, true);
                            }
                            else {
                                String defaultValue = opt.getParam().defaultValue();
                                if (defaultValue.length() > 0) {
                                    magicallySetField(obj, opt, parsers.parse(opt.field.getType(), cmd.getOptionValue(opt.getOpt(), defaultValue)));
                                }
                                else {
                                    magicallySetField(obj, opt, parsers.parse(opt.field.getType(), cmd.getOptionValue(opt.getOpt())));
                                }
                            }
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }
            return this;
        }

        public AnnotatedCLI cli()
        {
            return AnnotatedCLI.this;
        }
    }
}
