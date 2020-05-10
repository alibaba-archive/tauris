// Generated from /Users/zhanglei/Work/Projects/ware/tauris4/tauris-config/src/main/antlr4/Tauris.g4 by ANTLR 4.6

    package antlr4.tauris;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TaurisParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TaurisVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TaurisParser#pipeline}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPipeline(TaurisParser.PipelineContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#inputGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInputGroup(TaurisParser.InputGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#filterGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFilterGroup(TaurisParser.FilterGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#outputGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputGroup(TaurisParser.OutputGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#pluginGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPluginGroup(TaurisParser.PluginGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#plugins}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlugins(TaurisParser.PluginsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#plugin}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPlugin(TaurisParser.PluginContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#assignments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignments(TaurisParser.AssignmentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(TaurisParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(TaurisParser.NameContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(TaurisParser.ValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#simpleValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleValue(TaurisParser.SimpleValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#environValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnvironValue(TaurisParser.EnvironValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(TaurisParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#hash}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHash(TaurisParser.HashContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#keyValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyValues(TaurisParser.KeyValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#keyValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyValue(TaurisParser.KeyValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#key}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKey(TaurisParser.KeyContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#strings}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStrings(TaurisParser.StringsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#integers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegers(TaurisParser.IntegersContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#floats}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloats(TaurisParser.FloatsContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#booleans}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleans(TaurisParser.BooleansContext ctx);
	/**
	 * Visit a parse tree produced by {@link TaurisParser#nulls}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNulls(TaurisParser.NullsContext ctx);
}