package com.aliyun.tauris.config.parser;

import antlr4.tauris.TaurisBaseVisitor;
import antlr4.tauris.TaurisLexer;
import antlr4.tauris.TaurisParser;
import com.alibaba.texpr.ExprException;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

/**
 * Created by yundun-waf-dev
 */
public class Parser {

    public static Pipeline parsePipeline(String configText) {
        CharStream   charStream = new ANTLRInputStream(configText);
        TaurisLexer  lexer      = new TaurisLexer(charStream);
        TokenStream  tokens     = new CommonTokenStream(lexer);
        TaurisParser parser     = new TaurisParser(tokens);

        parser.removeErrorListeners();
        ConfigErrorListener errorListener = new ConfigErrorListener(configText);
        parser.addErrorListener(errorListener);

        PipelineVisitor visitor = new PipelineVisitor();
        try {
            Pipeline pipeline = visitor.visit(parser.pipeline());
            if (errorListener.hasError()) {
                throw new ExprException(String.format("config has syntax error, %s", errorListener.errorMessage));
            }
            return pipeline;
        } catch (Exception e) {
            if (errorListener.hasError()) {
                throw new ExprException(String.format("config has syntax error, %s", errorListener.errorMessage));
            } else {
                throw new ExprException(String.format("config has syntax error, %s", e.getMessage()));
            }
        }
    }

    public static Plugin parsePlugin(String config) {
        PluginVisitor visitor = new PluginVisitor();
        return parse(config, (parser) -> {
            return visitor.visitPlugin(parser.plugin());
        });
    }

    public static PluginGroup parsePluginGroup(String config) {
        PluginGroupVisitor visitor = new PluginGroupVisitor();
        return parse(config, (parser) -> {
            return visitor.visitPluginGroup(parser.pluginGroup());
        });
    }

    private static <T> T parse(String config, Function<TaurisParser, T> visitor) {
        CharStream   charStream = new ANTLRInputStream(config);
        TaurisLexer  lexer      = new TaurisLexer(charStream);
        TokenStream  tokens     = new CommonTokenStream(lexer);
        TaurisParser parser     = new TaurisParser(tokens);

        parser.removeErrorListeners();
        ConfigErrorListener errorListener = new ConfigErrorListener(config);
        parser.addErrorListener(errorListener);

        try {
            T ret = visitor.apply(parser);
            if (errorListener.hasError()) {
                throw new ExprException(String.format("config has syntax error, %s", errorListener.errorMessage));
            }
            return ret;
        } catch (Exception e) {
            if (errorListener.hasError()) {
                throw new ExprException(String.format("config has syntax error, %s", errorListener.errorMessage));
            } else {
                throw new ExprException(String.format("config has syntax error, %s", e.getMessage()));
            }
        }
    }


    private static class PipelineVisitor extends TaurisBaseVisitor<Pipeline> {

        @Override
        public Pipeline visitPipeline(TaurisParser.PipelineContext ctx) {
            InputGroupVisitor  inputVisitor  = new InputGroupVisitor();
            FilterGroupVisitor filterVisitor = new FilterGroupVisitor();
            OutputGroupVisitor outputVisitor = new OutputGroupVisitor();

            List<InputGroup> inputGroups = new ArrayList<>();
            if (ctx.inputGroup() != null) {
                for (TaurisParser.InputGroupContext f : ctx.inputGroup()) {
                    InputGroup inputGroup = inputVisitor.visitInputGroup(f);
                    inputGroups.add(inputGroup);
                }
            }

            List<FilterGroup> filterGroups = new ArrayList<>();
            if (ctx.filterGroup() != null) {
                for (TaurisParser.FilterGroupContext f : ctx.filterGroup()) {
                    FilterGroup filterGroup = filterVisitor.visitFilterGroup(f);
                    filterGroups.add(filterGroup);
                }
            }

            List<OutputGroup> outputGroups = new ArrayList<>();
            for (TaurisParser.OutputGroupContext c : ctx.outputGroup()) {
                OutputGroup outputGroup = outputVisitor.visitOutputGroup(c);
                outputGroups.add(outputGroup);
            }
            return new Pipeline(inputGroups, filterGroups, outputGroups);
        }
    }

    private static class PluginGroupVisitor extends TaurisBaseVisitor<PluginGroup> {
        @Override
        public PluginGroup visitPluginGroup(TaurisParser.PluginGroupContext ctx) {
            PluginsVisitor     pluginsVisitor     = new PluginsVisitor();
            AssignmentsVisitor assignmentsVisitor = new AssignmentsVisitor();
            String             typeName           = ctx.ID().getText();
            return new PluginGroup(typeName, pluginsVisitor.visitPlugins(ctx.plugins()), assignmentsVisitor.visit(ctx.plugins().assignment()));
        }
    }

    private static class InputGroupVisitor extends TaurisBaseVisitor<InputGroup> {
        @Override
        public InputGroup visitInputGroup(TaurisParser.InputGroupContext ctx) {
            PluginsVisitor     pluginsVisitor     = new PluginsVisitor();
            AssignmentsVisitor assignmentsVisitor = new AssignmentsVisitor();
            return new InputGroup(pluginsVisitor.visitPlugins(ctx.plugins()), assignmentsVisitor.visit(ctx.plugins().assignment()));
        }
    }

    private static class FilterGroupVisitor extends TaurisBaseVisitor<FilterGroup> {

        @Override
        public FilterGroup visitFilterGroup(TaurisParser.FilterGroupContext ctx) {
            PluginsVisitor     pluginsVisitor     = new PluginsVisitor();
            AssignmentsVisitor assignmentsVisitor = new AssignmentsVisitor();
            return new FilterGroup(pluginsVisitor.visitPlugins(ctx.plugins()), assignmentsVisitor.visit(ctx.plugins().assignment()));
        }
    }

    private static class OutputGroupVisitor extends TaurisBaseVisitor<OutputGroup> {

        @Override
        public OutputGroup visitOutputGroup(TaurisParser.OutputGroupContext ctx) {
            PluginsVisitor     pluginsVisitor     = new PluginsVisitor();
            AssignmentsVisitor assignmentsVisitor = new AssignmentsVisitor();
            return new OutputGroup(pluginsVisitor.visitPlugins(ctx.plugins()), assignmentsVisitor.visit(ctx.plugins().assignment()));
        }
    }

    private static class PluginsVisitor extends TaurisBaseVisitor<List<Plugin>> {

        @Override
        public List<Plugin> visitPlugins(TaurisParser.PluginsContext ctx) {
            PluginVisitor pluginVisitor = new PluginVisitor();
            return ctx.plugin()
                    .stream()
                    .map(method -> method.accept(pluginVisitor))
                    .collect(toList());
        }
    }

    private static class PluginVisitor extends TaurisBaseVisitor<Plugin> {
        @Override
        public Plugin visitPlugin(TaurisParser.PluginContext ctx) {
            TaurisParser.PluginNameContext pluginNameCtx = ctx.pluginName();
            String majorName = pluginNameCtx.name(0).getText();
            String minorName = pluginNameCtx.name().size() > 1 ?  pluginNameCtx.name(1).getText() : null;
            AssignmentsVisitor objectVisitor = new AssignmentsVisitor();
            Assignments        pluginBody    = objectVisitor.visitAssignments(ctx.assignments());
            return new Plugin(majorName, minorName, pluginBody);
        }
    }

    private static class HashVisitor extends TaurisBaseVisitor<HashValue> {

        @Override
        public HashValue visitHash(TaurisParser.HashContext ctx) {
            List<KeyValue>      pairs               = new ArrayList<>();
            SimpleValueVisitor  simpleValueVisitor  = new SimpleValueVisitor();
            EnvironValueVisitor environValueVisitor = new EnvironValueVisitor();
            for (TaurisParser.KeyValueContext c : ctx.keyValues().keyValue()) {
                SimpleValue.StringValue key = new SimpleValue.StringValue(c.key().String().getText());
                if (c.simpleValue() != null) {
                    pairs.add(new KeyValue(key, simpleValueVisitor.visitSimpleValue(c.simpleValue()).value()));
                }
                if (c.environValue() != null) {
                    pairs.add(new KeyValue(key, environValueVisitor.visitEnvironValue(c.environValue()).value()));
                }
            }
            return new HashValue(pairs);
        }
    }

    private static class AssignmentsVisitor extends TaurisBaseVisitor<Assignments> {

        @Override
        public Assignments visitAssignments(TaurisParser.AssignmentsContext ctx) {
            return this.visit(ctx.assignment());
        }

        public Assignments visit(List<TaurisParser.AssignmentContext> ctx) {
            AssignmentVisitor assignmentVisitor = new AssignmentVisitor();
            List<Assignment> assignments = ctx
                    .stream()
                    .map(assignment -> assignment.accept(assignmentVisitor))
                    .collect(toList());
            return new Assignments(assignments);
        }
    }

    private static class AssignmentVisitor extends TaurisBaseVisitor<Assignment> {

        @Override
        public Assignment visitAssignment(TaurisParser.AssignmentContext ctx) {
            String name = ctx.name().getText();
            if (ctx.plugins() != null) {
                Value value = ctx.plugins().accept(new ValueVisitor());
                return new Assignment(name, value);
            }
            if (ctx.value() != null) {
                Value value = ctx.value().accept(new ValueVisitor());
                return new Assignment(name, value);
            }
            if (ctx.plugin() != null) {
                Value value = ctx.plugin().accept(new ValueVisitor());
                return new Assignment(name, value);
            }
            if (ctx.hash() != null) {
                Value value = ctx.hash().accept(new ValueVisitor());
                return new Assignment(name, value);
            }
            if (ctx.assignments() != null) {
                Value value = ctx.assignments().accept(new ValueVisitor());
                return new Assignment(name, value);
            }
            throw new RuntimeException("assignment is null");
        }
    }

    private static class ValueVisitor extends TaurisBaseVisitor<Value> {
        //
        @Override
        public Value visitPlugins(TaurisParser.PluginsContext ctx) {
            PluginVisitor pluginVisitor = new PluginVisitor();
            return new PluginsValue(ctx.plugin()
                    .stream()
                    .map(method -> method.accept(pluginVisitor))
                    .collect(toList()));
        }

        @Override
        public Value visitPlugin(TaurisParser.PluginContext ctx) {
            PluginVisitor pluginVisitor = new PluginVisitor();
            return new PluginValue(pluginVisitor.visitPlugin(ctx));
        }

        @Override
        public Value visitAssignments(TaurisParser.AssignmentsContext ctx) {
            AssignmentsVisitor assignmentsVisitor = new AssignmentsVisitor();
            return new AssignmentsValue(assignmentsVisitor.visitAssignments(ctx));
        }

        @Override
        public Value visitHash(TaurisParser.HashContext ctx) {
            return new HashVisitor().visitHash(ctx);
        }

        @Override
        public Value visitSimpleValue(TaurisParser.SimpleValueContext ctx) {
            return new SimpleValueVisitor().visitSimpleValue(ctx);
        }

        @Override
        public Value visitValue(TaurisParser.ValueContext ctx) {
            //array , assignment, simplevalue
            if (ctx.array() != null) {
                return new ArrayValueVisitor().visitArray(ctx.array());
            }
            if (ctx.simpleValue() != null) {
                return new SimpleValueVisitor().visitSimpleValue(ctx.simpleValue());
            }
            if (ctx.environValue() != null) {
                return new EnvironValueVisitor().visitEnvironValue(ctx.environValue());
            }
            throw new RuntimeException("unknown value");
        }
    }

    private static class SimpleValueVisitor extends TaurisBaseVisitor<SimpleValue> {

        @Override
        public SimpleValue visitSimpleValue(TaurisParser.SimpleValueContext ctx) {
            if (ctx.String() != null) {                                 //String
                return new SimpleValue.StringValue(ctx.String().getText());
            } else if (ctx.Integer() != null) {                         //Integer
                return new SimpleValue.IntValue(ctx.Integer().getText());
            } else if (ctx.Float() != null) {                           //Float
                return new SimpleValue.FloatValue(ctx.Float().getText());
            } else if (ctx.Boolean() != null) {                          //Boolean
                return new SimpleValue.BooleanValue(ctx.Boolean().getText());
            } else if (ctx.Null() != null) {                            //Null
                return new SimpleValue.NullValue();
            } else
                throw new RuntimeException("unknown simple value");
        }
    }

    private static class EnvironValueVisitor extends TaurisBaseVisitor<EnvironValue> {
        @Override
        public EnvironValue visitEnvironValue(TaurisParser.EnvironValueContext ctx) {
            return new EnvironValue(ctx.Environ().getText());
        }
    }

    private static class ArrayValueVisitor extends TaurisBaseVisitor<ArrayValue> {


        @Override
        public ArrayValue visitStrings(TaurisParser.StringsContext ctx) {
            return new ArrayValue(ctx.String()
                    .stream()
                    .map(s -> new SimpleValue.StringValue(s.getText()))
                    .collect(toList()));
        }

        @Override
        public ArrayValue visitIntegers(TaurisParser.IntegersContext ctx) {
            return new ArrayValue(ctx.Integer()
                    .stream()
                    .map(s -> new SimpleValue.IntValue(s.getText()))
                    .collect(toList()));
        }

        @Override
        public ArrayValue visitFloats(TaurisParser.FloatsContext ctx) {
            return new ArrayValue(ctx.Float()
                    .stream()
                    .map(s -> new SimpleValue.FloatValue(s.getText()))
                    .collect(toList()));
        }

        @Override
        public ArrayValue visitBooleans(TaurisParser.BooleansContext ctx) {
            return new ArrayValue(ctx.Boolean()
                    .stream()
                    .map(s -> new SimpleValue.BooleanValue(s.getText()))
                    .collect(toList()));
        }

        @Override
        public ArrayValue visitNulls(TaurisParser.NullsContext ctx) {
            return new ArrayValue(ctx.Null()
                    .stream()
                    .map(s -> new SimpleValue.NullValue())
                    .collect(toList()));
        }

        @Override
        public ArrayValue visitArray(TaurisParser.ArrayContext ctx) {

            if (ctx.integers() != null) {
                return visitIntegers(ctx.integers());
            }
            if (ctx.strings() != null) {
                return visitStrings(ctx.strings());
            }

            if (ctx.floats() != null) {
                return visitFloats(ctx.floats());
            }

            if (ctx.booleans() != null) {
                return visitBooleans(ctx.booleans());
            }

            if (ctx.nulls() != null) {
                return visitNulls(ctx.nulls());
            }

            return new ArrayValue(Collections.emptyList());
        }
    }


    private static class ConfigErrorListener extends BaseErrorListener {

        String[] lines;

        StringBuffer errorMessage = new StringBuffer();

        public ConfigErrorListener(String content) {
            this.lines = content.split("\n");
        }

        public boolean hasError() {
            return errorMessage.length() > 0;
        }

        public String getErrorMessage() {
            return errorMessage.toString();
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            StringBuilder part      = new StringBuilder();
            int           startLine = line > 0 ? line - 2 : 0;
            int           endLine   = line > 0 ? line : 1;
            part.append(String.format("syntax error: line:%d:%d, %s\n", line, charPositionInLine, msg));
            for (int no = startLine; no <= endLine; no++) {
                part.append(String.format("%d %s %s\n", no + 1, no == line - 1 ? "*" : " ", lines[no]));
            }
            errorMessage.append(part.toString());
        }
    }
}
