package com.mewmew.fairy.v1.book;

import com.mewmew.fairy.v1.json.JsonSpell;
import com.mewmew.fairy.v1.map.MapFunction;
import com.mewmew.fairy.v1.pipe.ObjectPipe;
import com.mewmew.fairy.v1.pipe.Output;
import com.mewmew.fairy.v1.cli.Param;
import com.mewmew.fairy.v1.spell.Help;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.SelectClauseElement;
import com.espertech.esper.client.soda.SelectClauseExpression;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

@Help(desc = "run stream query on command line !")
public class Esper extends JsonSpell implements MapFunction<Map<String, Object>, Map<String, Object>>,
        ObjectPipe<Map<String, Object>, Map<String, Object>>
{
    @Param (desc="the esper query")
    String query = "select * from S";
    @Param (desc="print only first n events")
    int first = -1;
    @Param (desc="print only last n events")
    int last = -1;
    @Param (option="P", desc="print events in the window at the end")
    boolean print = false;
    @Param (option="U", desc="print all update events")
    boolean update = false;
    @Param
    boolean verbose = false;

    EPServiceProvider provider;
    EPStatement statement;
    EPStatementObjectModel stmtModel;
    ArrayList<String> groupBy;
    private Map<String, Map<String, Object>> groups;
    private OutputWrapper outputWrapper;
    private int count = 0;

    public Esper()
    {
        provider = EPServiceProviderManager.getProvider("default");
    }

    public Esper(String query)
    {
        provider = EPServiceProviderManager.getProvider("default");
        this.query = query;
    }

    @Override
    protected ObjectPipe<Map<String, Object>, Map<String, Object>> createPipe()
    {
        return this;
    }

    public void each(Map<String, Object> input, Output<Map<String, Object>> mapOutput) throws IOException
    {
        if (statement == null) {
            registerEventAndStatement(input, mapOutput);
        }
        if (verbose) {
            if (++count % 1000 == 0) {
                System.err.printf("%d items processed\n", count);
            }
        }
        provider.getEPRuntime().sendEvent(input, "S");
    }

    private void registerEventAndStatement(Map<String, Object> input, final Output<Map<String, Object>> mapOutput)
    {
        Properties type = new Properties();
        for (Map.Entry<String, Object> entry : input.entrySet()) {
            type.put(entry.getKey(), entry.getValue().getClass().getName());
        }
        provider.getEPAdministrator().getConfiguration().addEventType("S", type);
        stmtModel = provider.getEPAdministrator().compileEPL(query);
        statement = provider.getEPAdministrator().create(stmtModel);
        statement.addListener(new UpdateListener()
        {
            public void update(EventBean[] inserts, EventBean[] removes)
            {
                try {
                    if (first > 0) {
                        int max = Math.min(first, inserts.length);
                        for (int i = 0; i < max; i++) {
                            mapOutput.output(toMap(inserts[i]));
                        }
                    }
                    else if (last > 0) {
                        int min = Math.max(0, inserts.length - last);
                        for (int i = min; i < inserts.length ; i++) {
                            mapOutput.output(toMap(inserts[i]));
                        }
                    }
                    else {
                        for (EventBean insert : inserts) {
                            mapOutput.output(toMap(insert));
                        }
                    }
                    if (update) {
                        outputWrapper.flush();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        extractGroupBy();
    }

    private Map<String, Object> toMap(EventBean insert)
    {
        if (insert.getUnderlying() instanceof Map) {
            return (Map<String, Object>) insert.getUnderlying();
        }
        else {
            Map<String, Object> map = new HashMap<String, Object>();
            for (String name : insert.getEventType().getPropertyNames()) {
                map.put(name, insert.get(name));
            }
            return map;
        }
    }

    private void extractGroupBy()
    {
        groupBy = new ArrayList<String>();
        if (stmtModel.getGroupByClause() != null) {
            List<Expression> exprs = stmtModel.getGroupByClause().getGroupByExpressions();
            for (Expression expr : exprs) {
                StringWriter sw = new StringWriter();
                expr.toEPL(sw);
                String groupColumn = sw.toString();
                List<SelectClauseElement> selects = stmtModel.getSelectClause().getSelectList();
                for (SelectClauseElement select : selects) {
                    if (select instanceof SelectClauseExpression) {
                        sw = new StringWriter();
                        SelectClauseExpression expression = (SelectClauseExpression) select;
                        expression.getExpression().toEPL(sw);
                        String exprStr = sw.toString();
                        if (exprStr.equals(groupColumn)) {
                            if (!StringUtils.isEmpty(expression.getAsName())) {
                                groupColumn = expression.getAsName();
                            }
                        }
                    }
                }
                groupBy.add(groupColumn);
            }
        }
        if (!groupBy.isEmpty()) {
            groups = new HashMap<String, Map<String, Object>>();
        }
        else {
            groupBy = null;
        }
    }

    public static void main(String[] args)
    {
        new Esper("select count(*), com.mewmew.fairy.v1.book.Esper.bucket(k) as k1 from S group by com.mewmew.fairy.v1.book.Esper.bucket(k)").registerEventAndStatement(ImmutableMap.<String, Object>builder()
                .put("k", "value")
                .put("n", 1)
                .build(), null);
    }

    public static int bucket(String v)
    {
        return 0;
    }

    public void open(Output<Map<String, Object>> mapOutput) throws IOException
    {

    }

    public void close(Output<Map<String, Object>> mapOutput) throws IOException
    {
        mapOutput.close();
    }

    @Override
    protected Output<Map<String, Object>> createOutput(OutputStream out) throws IOException
    {
        return outputWrapper = new OutputWrapper(super.createOutput(out));
    }

    private class OutputWrapper implements Output<Map<String, Object>>
    {
        private final Output<Map<String, Object>> delegate;

        public OutputWrapper(Output<Map<String, Object>> delegate)
        {
            this.delegate = delegate;
        }

        public void output(Map<String, Object> obj) throws IOException
        {
            if (groupBy != null) {
                List<Object> list = new ArrayList<Object>();
                for (String col : groupBy) {
                    Object v = obj.get(col);
                    list.add(v != null ? v : "");
                }
                groups.put(MD5.md5(list), obj);
            }
            else if (update) {
                this.delegate.output(obj);
            }
        }

        public void close() throws IOException
        {
            flush();
            if (print) {
                Iterator<EventBean> iter = statement.iterator();
                while (iter.hasNext()) {
                    this.delegate.output(toMap(iter.next()));
                }
            }
            this.delegate.close();
        }

        private void flush()
                throws IOException
        {
            if (groups != null) {
                for (Map<String, Object> map : groups.values()) {
                    this.delegate.output(map);
                }
            }
        }
    }
}

