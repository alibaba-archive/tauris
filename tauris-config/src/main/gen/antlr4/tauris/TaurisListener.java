// Generated from Tauris.g4 by ANTLR 4.6

    package antlr4.tauris;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TaurisParser}.
 */
public interface TaurisListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TaurisParser#pipeline}.
	 * @param ctx the parse tree
	 */
	void enterPipeline(TaurisParser.PipelineContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#pipeline}.
	 * @param ctx the parse tree
	 */
	void exitPipeline(TaurisParser.PipelineContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#inputGroup}.
	 * @param ctx the parse tree
	 */
	void enterInputGroup(TaurisParser.InputGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#inputGroup}.
	 * @param ctx the parse tree
	 */
	void exitInputGroup(TaurisParser.InputGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#filterGroup}.
	 * @param ctx the parse tree
	 */
	void enterFilterGroup(TaurisParser.FilterGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#filterGroup}.
	 * @param ctx the parse tree
	 */
	void exitFilterGroup(TaurisParser.FilterGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#outputGroup}.
	 * @param ctx the parse tree
	 */
	void enterOutputGroup(TaurisParser.OutputGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#outputGroup}.
	 * @param ctx the parse tree
	 */
	void exitOutputGroup(TaurisParser.OutputGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#pluginGroup}.
	 * @param ctx the parse tree
	 */
	void enterPluginGroup(TaurisParser.PluginGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#pluginGroup}.
	 * @param ctx the parse tree
	 */
	void exitPluginGroup(TaurisParser.PluginGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#plugins}.
	 * @param ctx the parse tree
	 */
	void enterPlugins(TaurisParser.PluginsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#plugins}.
	 * @param ctx the parse tree
	 */
	void exitPlugins(TaurisParser.PluginsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#plugin}.
	 * @param ctx the parse tree
	 */
	void enterPlugin(TaurisParser.PluginContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#plugin}.
	 * @param ctx the parse tree
	 */
	void exitPlugin(TaurisParser.PluginContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#pluginName}.
	 * @param ctx the parse tree
	 */
	void enterPluginName(TaurisParser.PluginNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#pluginName}.
	 * @param ctx the parse tree
	 */
	void exitPluginName(TaurisParser.PluginNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#assignments}.
	 * @param ctx the parse tree
	 */
	void enterAssignments(TaurisParser.AssignmentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#assignments}.
	 * @param ctx the parse tree
	 */
	void exitAssignments(TaurisParser.AssignmentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(TaurisParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(TaurisParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(TaurisParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(TaurisParser.NameContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(TaurisParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(TaurisParser.ValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#simpleValue}.
	 * @param ctx the parse tree
	 */
	void enterSimpleValue(TaurisParser.SimpleValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#simpleValue}.
	 * @param ctx the parse tree
	 */
	void exitSimpleValue(TaurisParser.SimpleValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#environValue}.
	 * @param ctx the parse tree
	 */
	void enterEnvironValue(TaurisParser.EnvironValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#environValue}.
	 * @param ctx the parse tree
	 */
	void exitEnvironValue(TaurisParser.EnvironValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#array}.
	 * @param ctx the parse tree
	 */
	void enterArray(TaurisParser.ArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#array}.
	 * @param ctx the parse tree
	 */
	void exitArray(TaurisParser.ArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#hash}.
	 * @param ctx the parse tree
	 */
	void enterHash(TaurisParser.HashContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#hash}.
	 * @param ctx the parse tree
	 */
	void exitHash(TaurisParser.HashContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#keyValues}.
	 * @param ctx the parse tree
	 */
	void enterKeyValues(TaurisParser.KeyValuesContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#keyValues}.
	 * @param ctx the parse tree
	 */
	void exitKeyValues(TaurisParser.KeyValuesContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#keyValue}.
	 * @param ctx the parse tree
	 */
	void enterKeyValue(TaurisParser.KeyValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#keyValue}.
	 * @param ctx the parse tree
	 */
	void exitKeyValue(TaurisParser.KeyValueContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#key}.
	 * @param ctx the parse tree
	 */
	void enterKey(TaurisParser.KeyContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#key}.
	 * @param ctx the parse tree
	 */
	void exitKey(TaurisParser.KeyContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#strings}.
	 * @param ctx the parse tree
	 */
	void enterStrings(TaurisParser.StringsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#strings}.
	 * @param ctx the parse tree
	 */
	void exitStrings(TaurisParser.StringsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#integers}.
	 * @param ctx the parse tree
	 */
	void enterIntegers(TaurisParser.IntegersContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#integers}.
	 * @param ctx the parse tree
	 */
	void exitIntegers(TaurisParser.IntegersContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#floats}.
	 * @param ctx the parse tree
	 */
	void enterFloats(TaurisParser.FloatsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#floats}.
	 * @param ctx the parse tree
	 */
	void exitFloats(TaurisParser.FloatsContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#booleans}.
	 * @param ctx the parse tree
	 */
	void enterBooleans(TaurisParser.BooleansContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#booleans}.
	 * @param ctx the parse tree
	 */
	void exitBooleans(TaurisParser.BooleansContext ctx);
	/**
	 * Enter a parse tree produced by {@link TaurisParser#nulls}.
	 * @param ctx the parse tree
	 */
	void enterNulls(TaurisParser.NullsContext ctx);
	/**
	 * Exit a parse tree produced by {@link TaurisParser#nulls}.
	 * @param ctx the parse tree
	 */
	void exitNulls(TaurisParser.NullsContext ctx);
}