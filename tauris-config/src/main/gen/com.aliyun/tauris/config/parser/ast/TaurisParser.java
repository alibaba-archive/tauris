// Generated from Tauris.g4 by ANTLR 4.6

    package com.aliyun.tauris.config.parser.ast;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TaurisParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, Integer=13, Float=14, Boolean=15, Null=16, 
		String=17, Environ=18, ID=19, WS=20, LINE_COMMENT=21;
	public static final int
		RULE_pipeline = 0, RULE_inputGroup = 1, RULE_filterGroup = 2, RULE_outputGroup = 3, 
		RULE_pluginGroup = 4, RULE_plugins = 5, RULE_plugin = 6, RULE_pluginName = 7, 
		RULE_assignments = 8, RULE_assignment = 9, RULE_name = 10, RULE_value = 11, 
		RULE_simpleValue = 12, RULE_environValue = 13, RULE_array = 14, RULE_hash = 15, 
		RULE_keyValues = 16, RULE_keyValue = 17, RULE_key = 18, RULE_strings = 19, 
		RULE_integers = 20, RULE_floats = 21, RULE_booleans = 22, RULE_nulls = 23;
	public static final String[] ruleNames = {
		"pipeline", "inputGroup", "filterGroup", "outputGroup", "pluginGroup", 
		"plugins", "plugin", "pluginName", "assignments", "assignment", "name", 
		"value", "simpleValue", "environValue", "array", "hash", "keyValues", 
		"keyValue", "key", "strings", "integers", "floats", "booleans", "nulls"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'input'", "'filter'", "'output'", "'{'", "'}'", "'.'", "'=>'", 
		"';'", "'['", "']'", "','", "':'", null, null, null, "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, "Integer", "Float", "Boolean", "Null", "String", "Environ", "ID", 
		"WS", "LINE_COMMENT"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Tauris.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TaurisParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class PipelineContext extends ParserRuleContext {
		public List<InputGroupContext> inputGroup() {
			return getRuleContexts(InputGroupContext.class);
		}
		public InputGroupContext inputGroup(int i) {
			return getRuleContext(InputGroupContext.class,i);
		}
		public List<FilterGroupContext> filterGroup() {
			return getRuleContexts(FilterGroupContext.class);
		}
		public FilterGroupContext filterGroup(int i) {
			return getRuleContext(FilterGroupContext.class,i);
		}
		public List<OutputGroupContext> outputGroup() {
			return getRuleContexts(OutputGroupContext.class);
		}
		public OutputGroupContext outputGroup(int i) {
			return getRuleContext(OutputGroupContext.class,i);
		}
		public PipelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipeline; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterPipeline(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitPipeline(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitPipeline(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PipelineContext pipeline() throws RecognitionException {
		PipelineContext _localctx = new PipelineContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_pipeline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(48);
				inputGroup();
				}
				}
				setState(51); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(53);
				filterGroup();
				}
				}
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(59);
				outputGroup();
				}
				}
				setState(62); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__2 );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InputGroupContext extends ParserRuleContext {
		public PluginsContext plugins() {
			return getRuleContext(PluginsContext.class,0);
		}
		public InputGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterInputGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitInputGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitInputGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InputGroupContext inputGroup() throws RecognitionException {
		InputGroupContext _localctx = new InputGroupContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_inputGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			match(T__0);
			setState(65);
			plugins();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FilterGroupContext extends ParserRuleContext {
		public PluginsContext plugins() {
			return getRuleContext(PluginsContext.class,0);
		}
		public FilterGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_filterGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterFilterGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitFilterGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitFilterGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FilterGroupContext filterGroup() throws RecognitionException {
		FilterGroupContext _localctx = new FilterGroupContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_filterGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(T__1);
			setState(68);
			plugins();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OutputGroupContext extends ParserRuleContext {
		public PluginsContext plugins() {
			return getRuleContext(PluginsContext.class,0);
		}
		public OutputGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_outputGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterOutputGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitOutputGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitOutputGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OutputGroupContext outputGroup() throws RecognitionException {
		OutputGroupContext _localctx = new OutputGroupContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_outputGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(T__2);
			setState(71);
			plugins();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PluginGroupContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TaurisParser.ID, 0); }
		public PluginsContext plugins() {
			return getRuleContext(PluginsContext.class,0);
		}
		public PluginGroupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pluginGroup; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterPluginGroup(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitPluginGroup(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitPluginGroup(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PluginGroupContext pluginGroup() throws RecognitionException {
		PluginGroupContext _localctx = new PluginGroupContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_pluginGroup);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(ID);
			setState(74);
			plugins();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PluginsContext extends ParserRuleContext {
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public List<PluginContext> plugin() {
			return getRuleContexts(PluginContext.class);
		}
		public PluginContext plugin(int i) {
			return getRuleContext(PluginContext.class,i);
		}
		public PluginsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plugins; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterPlugins(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitPlugins(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitPlugins(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PluginsContext plugins() throws RecognitionException {
		PluginsContext _localctx = new PluginsContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_plugins);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			match(T__3);
			setState(80);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(77);
					assignment();
					}
					} 
				}
				setState(82);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(84); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(83);
				plugin();
				}
				}
				setState(86); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << ID))) != 0) );
			setState(88);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PluginContext extends ParserRuleContext {
		public PluginNameContext pluginName() {
			return getRuleContext(PluginNameContext.class,0);
		}
		public AssignmentsContext assignments() {
			return getRuleContext(AssignmentsContext.class,0);
		}
		public PluginContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plugin; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterPlugin(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitPlugin(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitPlugin(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PluginContext plugin() throws RecognitionException {
		PluginContext _localctx = new PluginContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_plugin);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			pluginName();
			setState(91);
			assignments();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PluginNameContext extends ParserRuleContext {
		public List<NameContext> name() {
			return getRuleContexts(NameContext.class);
		}
		public NameContext name(int i) {
			return getRuleContext(NameContext.class,i);
		}
		public PluginNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pluginName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterPluginName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitPluginName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitPluginName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PluginNameContext pluginName() throws RecognitionException {
		PluginNameContext _localctx = new PluginNameContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_pluginName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			name();
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(94);
				match(T__5);
				setState(95);
				name();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentsContext extends ParserRuleContext {
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public AssignmentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterAssignments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitAssignments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitAssignments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentsContext assignments() throws RecognitionException {
		AssignmentsContext _localctx = new AssignmentsContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_assignments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(98);
			match(T__3);
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << ID))) != 0)) {
				{
				{
				setState(99);
				assignment();
				}
				}
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(105);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AssignmentContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public EnvironValueContext environValue() {
			return getRuleContext(EnvironValueContext.class,0);
		}
		public AssignmentsContext assignments() {
			return getRuleContext(AssignmentsContext.class,0);
		}
		public PluginsContext plugins() {
			return getRuleContext(PluginsContext.class,0);
		}
		public PluginContext plugin() {
			return getRuleContext(PluginContext.class,0);
		}
		public HashContext hash() {
			return getRuleContext(HashContext.class,0);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_assignment);
		try {
			setState(133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(107);
				name();
				setState(108);
				match(T__6);
				setState(109);
				value();
				setState(110);
				match(T__7);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(112);
				name();
				setState(113);
				match(T__6);
				setState(114);
				environValue();
				setState(115);
				match(T__7);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(117);
				name();
				setState(118);
				match(T__6);
				setState(119);
				assignments();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(121);
				name();
				setState(122);
				match(T__6);
				setState(123);
				plugins();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(125);
				name();
				setState(126);
				match(T__6);
				setState(127);
				plugin();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(129);
				name();
				setState(130);
				match(T__6);
				setState(131);
				hash();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(TaurisParser.ID, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << ID))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ValueContext extends ParserRuleContext {
		public SimpleValueContext simpleValue() {
			return getRuleContext(SimpleValueContext.class,0);
		}
		public EnvironValueContext environValue() {
			return getRuleContext(EnvironValueContext.class,0);
		}
		public ArrayContext array() {
			return getRuleContext(ArrayContext.class,0);
		}
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_value);
		try {
			setState(140);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Integer:
			case Float:
			case Boolean:
			case Null:
			case String:
				enterOuterAlt(_localctx, 1);
				{
				setState(137);
				simpleValue();
				}
				break;
			case Environ:
				enterOuterAlt(_localctx, 2);
				{
				setState(138);
				environValue();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(139);
				array();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleValueContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(TaurisParser.String, 0); }
		public TerminalNode Integer() { return getToken(TaurisParser.Integer, 0); }
		public TerminalNode Float() { return getToken(TaurisParser.Float, 0); }
		public TerminalNode Boolean() { return getToken(TaurisParser.Boolean, 0); }
		public TerminalNode Null() { return getToken(TaurisParser.Null, 0); }
		public SimpleValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterSimpleValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitSimpleValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitSimpleValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleValueContext simpleValue() throws RecognitionException {
		SimpleValueContext _localctx = new SimpleValueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_simpleValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(142);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Integer) | (1L << Float) | (1L << Boolean) | (1L << Null) | (1L << String))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnvironValueContext extends ParserRuleContext {
		public TerminalNode Environ() { return getToken(TaurisParser.Environ, 0); }
		public EnvironValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_environValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterEnvironValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitEnvironValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitEnvironValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnvironValueContext environValue() throws RecognitionException {
		EnvironValueContext _localctx = new EnvironValueContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_environValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(Environ);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayContext extends ParserRuleContext {
		public IntegersContext integers() {
			return getRuleContext(IntegersContext.class,0);
		}
		public StringsContext strings() {
			return getRuleContext(StringsContext.class,0);
		}
		public FloatsContext floats() {
			return getRuleContext(FloatsContext.class,0);
		}
		public BooleansContext booleans() {
			return getRuleContext(BooleansContext.class,0);
		}
		public NullsContext nulls() {
			return getRuleContext(NullsContext.class,0);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_array);
		int _la;
		try {
			setState(173);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(146);
				match(T__8);
				setState(148);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Integer) {
					{
					setState(147);
					integers();
					}
				}

				setState(150);
				match(T__9);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(151);
				match(T__8);
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==String) {
					{
					setState(152);
					strings();
					}
				}

				setState(155);
				match(T__9);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(156);
				match(T__8);
				setState(158);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Float) {
					{
					setState(157);
					floats();
					}
				}

				setState(160);
				match(T__9);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(161);
				match(T__8);
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Boolean) {
					{
					setState(162);
					booleans();
					}
				}

				setState(165);
				match(T__9);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(166);
				match(T__8);
				setState(168);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Null) {
					{
					setState(167);
					nulls();
					}
				}

				setState(170);
				match(T__9);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(171);
				match(T__8);
				setState(172);
				match(T__9);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HashContext extends ParserRuleContext {
		public KeyValuesContext keyValues() {
			return getRuleContext(KeyValuesContext.class,0);
		}
		public HashContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hash; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterHash(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitHash(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitHash(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HashContext hash() throws RecognitionException {
		HashContext _localctx = new HashContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_hash);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			match(T__3);
			setState(176);
			keyValues();
			setState(177);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeyValuesContext extends ParserRuleContext {
		public List<KeyValueContext> keyValue() {
			return getRuleContexts(KeyValueContext.class);
		}
		public KeyValueContext keyValue(int i) {
			return getRuleContext(KeyValueContext.class,i);
		}
		public KeyValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyValues; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterKeyValues(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitKeyValues(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitKeyValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyValuesContext keyValues() throws RecognitionException {
		KeyValuesContext _localctx = new KeyValuesContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_keyValues);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(179);
			keyValue();
			setState(184);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(180);
					match(T__10);
					setState(181);
					keyValue();
					}
					} 
				}
				setState(186);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,15,_ctx);
			}
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(187);
				match(T__10);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeyValueContext extends ParserRuleContext {
		public KeyContext key() {
			return getRuleContext(KeyContext.class,0);
		}
		public SimpleValueContext simpleValue() {
			return getRuleContext(SimpleValueContext.class,0);
		}
		public EnvironValueContext environValue() {
			return getRuleContext(EnvironValueContext.class,0);
		}
		public KeyValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_keyValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterKeyValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitKeyValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitKeyValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyValueContext keyValue() throws RecognitionException {
		KeyValueContext _localctx = new KeyValueContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_keyValue);
		try {
			setState(198);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(190);
				key();
				setState(191);
				match(T__11);
				setState(192);
				simpleValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(194);
				key();
				setState(195);
				match(T__11);
				setState(196);
				environValue();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class KeyContext extends ParserRuleContext {
		public TerminalNode String() { return getToken(TaurisParser.String, 0); }
		public KeyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_key; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterKey(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitKey(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitKey(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_key);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(200);
			match(String);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringsContext extends ParserRuleContext {
		public List<TerminalNode> String() { return getTokens(TaurisParser.String); }
		public TerminalNode String(int i) {
			return getToken(TaurisParser.String, i);
		}
		public StringsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_strings; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterStrings(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitStrings(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitStrings(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringsContext strings() throws RecognitionException {
		StringsContext _localctx = new StringsContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_strings);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(202);
			match(String);
			setState(207);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(203);
					match(T__10);
					setState(204);
					match(String);
					}
					} 
				}
				setState(209);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			}
			setState(211);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(210);
				match(T__10);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IntegersContext extends ParserRuleContext {
		public List<TerminalNode> Integer() { return getTokens(TaurisParser.Integer); }
		public TerminalNode Integer(int i) {
			return getToken(TaurisParser.Integer, i);
		}
		public IntegersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_integers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterIntegers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitIntegers(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitIntegers(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegersContext integers() throws RecognitionException {
		IntegersContext _localctx = new IntegersContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_integers);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(213);
			match(Integer);
			setState(218);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(214);
					match(T__10);
					setState(215);
					match(Integer);
					}
					} 
				}
				setState(220);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			setState(222);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(221);
				match(T__10);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FloatsContext extends ParserRuleContext {
		public List<TerminalNode> Float() { return getTokens(TaurisParser.Float); }
		public TerminalNode Float(int i) {
			return getToken(TaurisParser.Float, i);
		}
		public FloatsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_floats; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterFloats(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitFloats(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitFloats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FloatsContext floats() throws RecognitionException {
		FloatsContext _localctx = new FloatsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_floats);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			match(Float);
			setState(229);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(225);
					match(T__10);
					setState(226);
					match(Float);
					}
					} 
				}
				setState(231);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,22,_ctx);
			}
			setState(233);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(232);
				match(T__10);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BooleansContext extends ParserRuleContext {
		public List<TerminalNode> Boolean() { return getTokens(TaurisParser.Boolean); }
		public TerminalNode Boolean(int i) {
			return getToken(TaurisParser.Boolean, i);
		}
		public BooleansContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_booleans; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterBooleans(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitBooleans(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitBooleans(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleansContext booleans() throws RecognitionException {
		BooleansContext _localctx = new BooleansContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_booleans);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(Boolean);
			setState(240);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(236);
					match(T__10);
					setState(237);
					match(Boolean);
					}
					} 
				}
				setState(242);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
			}
			setState(244);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(243);
				match(T__10);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NullsContext extends ParserRuleContext {
		public List<TerminalNode> Null() { return getTokens(TaurisParser.Null); }
		public TerminalNode Null(int i) {
			return getToken(TaurisParser.Null, i);
		}
		public NullsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nulls; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).enterNulls(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TaurisListener ) ((TaurisListener)listener).exitNulls(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitNulls(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NullsContext nulls() throws RecognitionException {
		NullsContext _localctx = new NullsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_nulls);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			match(Null);
			setState(251);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(247);
					match(T__10);
					setState(248);
					match(Null);
					}
					} 
				}
				setState(253);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			}
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(254);
				match(T__10);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\27\u0104\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\6\2\64\n\2\r\2\16\2\65\3\2\7\29\n\2\f\2\16\2<\13\2\3\2\6\2?\n\2\r"+
		"\2\16\2@\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\7\7Q"+
		"\n\7\f\7\16\7T\13\7\3\7\6\7W\n\7\r\7\16\7X\3\7\3\7\3\b\3\b\3\b\3\t\3\t"+
		"\3\t\5\tc\n\t\3\n\3\n\7\ng\n\n\f\n\16\nj\13\n\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u0088\n\13\3\f\3\f\3"+
		"\r\3\r\3\r\5\r\u008f\n\r\3\16\3\16\3\17\3\17\3\20\3\20\5\20\u0097\n\20"+
		"\3\20\3\20\3\20\5\20\u009c\n\20\3\20\3\20\3\20\5\20\u00a1\n\20\3\20\3"+
		"\20\3\20\5\20\u00a6\n\20\3\20\3\20\3\20\5\20\u00ab\n\20\3\20\3\20\3\20"+
		"\5\20\u00b0\n\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\7\22\u00b9\n\22\f"+
		"\22\16\22\u00bc\13\22\3\22\5\22\u00bf\n\22\3\23\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\5\23\u00c9\n\23\3\24\3\24\3\25\3\25\3\25\7\25\u00d0\n\25"+
		"\f\25\16\25\u00d3\13\25\3\25\5\25\u00d6\n\25\3\26\3\26\3\26\7\26\u00db"+
		"\n\26\f\26\16\26\u00de\13\26\3\26\5\26\u00e1\n\26\3\27\3\27\3\27\7\27"+
		"\u00e6\n\27\f\27\16\27\u00e9\13\27\3\27\5\27\u00ec\n\27\3\30\3\30\3\30"+
		"\7\30\u00f1\n\30\f\30\16\30\u00f4\13\30\3\30\5\30\u00f7\n\30\3\31\3\31"+
		"\3\31\7\31\u00fc\n\31\f\31\16\31\u00ff\13\31\3\31\5\31\u0102\n\31\3\31"+
		"\2\2\32\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\4\4\2\3\5"+
		"\25\25\3\2\17\23\u0110\2\63\3\2\2\2\4B\3\2\2\2\6E\3\2\2\2\bH\3\2\2\2\n"+
		"K\3\2\2\2\fN\3\2\2\2\16\\\3\2\2\2\20_\3\2\2\2\22d\3\2\2\2\24\u0087\3\2"+
		"\2\2\26\u0089\3\2\2\2\30\u008e\3\2\2\2\32\u0090\3\2\2\2\34\u0092\3\2\2"+
		"\2\36\u00af\3\2\2\2 \u00b1\3\2\2\2\"\u00b5\3\2\2\2$\u00c8\3\2\2\2&\u00ca"+
		"\3\2\2\2(\u00cc\3\2\2\2*\u00d7\3\2\2\2,\u00e2\3\2\2\2.\u00ed\3\2\2\2\60"+
		"\u00f8\3\2\2\2\62\64\5\4\3\2\63\62\3\2\2\2\64\65\3\2\2\2\65\63\3\2\2\2"+
		"\65\66\3\2\2\2\66:\3\2\2\2\679\5\6\4\28\67\3\2\2\29<\3\2\2\2:8\3\2\2\2"+
		":;\3\2\2\2;>\3\2\2\2<:\3\2\2\2=?\5\b\5\2>=\3\2\2\2?@\3\2\2\2@>\3\2\2\2"+
		"@A\3\2\2\2A\3\3\2\2\2BC\7\3\2\2CD\5\f\7\2D\5\3\2\2\2EF\7\4\2\2FG\5\f\7"+
		"\2G\7\3\2\2\2HI\7\5\2\2IJ\5\f\7\2J\t\3\2\2\2KL\7\25\2\2LM\5\f\7\2M\13"+
		"\3\2\2\2NR\7\6\2\2OQ\5\24\13\2PO\3\2\2\2QT\3\2\2\2RP\3\2\2\2RS\3\2\2\2"+
		"SV\3\2\2\2TR\3\2\2\2UW\5\16\b\2VU\3\2\2\2WX\3\2\2\2XV\3\2\2\2XY\3\2\2"+
		"\2YZ\3\2\2\2Z[\7\7\2\2[\r\3\2\2\2\\]\5\20\t\2]^\5\22\n\2^\17\3\2\2\2_"+
		"b\5\26\f\2`a\7\b\2\2ac\5\26\f\2b`\3\2\2\2bc\3\2\2\2c\21\3\2\2\2dh\7\6"+
		"\2\2eg\5\24\13\2fe\3\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2ik\3\2\2\2jh\3"+
		"\2\2\2kl\7\7\2\2l\23\3\2\2\2mn\5\26\f\2no\7\t\2\2op\5\30\r\2pq\7\n\2\2"+
		"q\u0088\3\2\2\2rs\5\26\f\2st\7\t\2\2tu\5\34\17\2uv\7\n\2\2v\u0088\3\2"+
		"\2\2wx\5\26\f\2xy\7\t\2\2yz\5\22\n\2z\u0088\3\2\2\2{|\5\26\f\2|}\7\t\2"+
		"\2}~\5\f\7\2~\u0088\3\2\2\2\177\u0080\5\26\f\2\u0080\u0081\7\t\2\2\u0081"+
		"\u0082\5\16\b\2\u0082\u0088\3\2\2\2\u0083\u0084\5\26\f\2\u0084\u0085\7"+
		"\t\2\2\u0085\u0086\5 \21\2\u0086\u0088\3\2\2\2\u0087m\3\2\2\2\u0087r\3"+
		"\2\2\2\u0087w\3\2\2\2\u0087{\3\2\2\2\u0087\177\3\2\2\2\u0087\u0083\3\2"+
		"\2\2\u0088\25\3\2\2\2\u0089\u008a\t\2\2\2\u008a\27\3\2\2\2\u008b\u008f"+
		"\5\32\16\2\u008c\u008f\5\34\17\2\u008d\u008f\5\36\20\2\u008e\u008b\3\2"+
		"\2\2\u008e\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f\31\3\2\2\2\u0090\u0091"+
		"\t\3\2\2\u0091\33\3\2\2\2\u0092\u0093\7\24\2\2\u0093\35\3\2\2\2\u0094"+
		"\u0096\7\13\2\2\u0095\u0097\5*\26\2\u0096\u0095\3\2\2\2\u0096\u0097\3"+
		"\2\2\2\u0097\u0098\3\2\2\2\u0098\u00b0\7\f\2\2\u0099\u009b\7\13\2\2\u009a"+
		"\u009c\5(\25\2\u009b\u009a\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u009d\3\2"+
		"\2\2\u009d\u00b0\7\f\2\2\u009e\u00a0\7\13\2\2\u009f\u00a1\5,\27\2\u00a0"+
		"\u009f\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00b0\7\f"+
		"\2\2\u00a3\u00a5\7\13\2\2\u00a4\u00a6\5.\30\2\u00a5\u00a4\3\2\2\2\u00a5"+
		"\u00a6\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00b0\7\f\2\2\u00a8\u00aa\7\13"+
		"\2\2\u00a9\u00ab\5\60\31\2\u00aa\u00a9\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab"+
		"\u00ac\3\2\2\2\u00ac\u00b0\7\f\2\2\u00ad\u00ae\7\13\2\2\u00ae\u00b0\7"+
		"\f\2\2\u00af\u0094\3\2\2\2\u00af\u0099\3\2\2\2\u00af\u009e\3\2\2\2\u00af"+
		"\u00a3\3\2\2\2\u00af\u00a8\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0\37\3\2\2"+
		"\2\u00b1\u00b2\7\6\2\2\u00b2\u00b3\5\"\22\2\u00b3\u00b4\7\7\2\2\u00b4"+
		"!\3\2\2\2\u00b5\u00ba\5$\23\2\u00b6\u00b7\7\r\2\2\u00b7\u00b9\5$\23\2"+
		"\u00b8\u00b6\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00b8\3\2\2\2\u00ba\u00bb"+
		"\3\2\2\2\u00bb\u00be\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd\u00bf\7\r\2\2\u00be"+
		"\u00bd\3\2\2\2\u00be\u00bf\3\2\2\2\u00bf#\3\2\2\2\u00c0\u00c1\5&\24\2"+
		"\u00c1\u00c2\7\16\2\2\u00c2\u00c3\5\32\16\2\u00c3\u00c9\3\2\2\2\u00c4"+
		"\u00c5\5&\24\2\u00c5\u00c6\7\16\2\2\u00c6\u00c7\5\34\17\2\u00c7\u00c9"+
		"\3\2\2\2\u00c8\u00c0\3\2\2\2\u00c8\u00c4\3\2\2\2\u00c9%\3\2\2\2\u00ca"+
		"\u00cb\7\23\2\2\u00cb\'\3\2\2\2\u00cc\u00d1\7\23\2\2\u00cd\u00ce\7\r\2"+
		"\2\u00ce\u00d0\7\23\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1"+
		"\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2"+
		"\2\2\u00d4\u00d6\7\r\2\2\u00d5\u00d4\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6"+
		")\3\2\2\2\u00d7\u00dc\7\17\2\2\u00d8\u00d9\7\r\2\2\u00d9\u00db\7\17\2"+
		"\2\u00da\u00d8\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00dd"+
		"\3\2\2\2\u00dd\u00e0\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e1\7\r\2\2\u00e0"+
		"\u00df\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1+\3\2\2\2\u00e2\u00e7\7\20\2\2"+
		"\u00e3\u00e4\7\r\2\2\u00e4\u00e6\7\20\2\2\u00e5\u00e3\3\2\2\2\u00e6\u00e9"+
		"\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9"+
		"\u00e7\3\2\2\2\u00ea\u00ec\7\r\2\2\u00eb\u00ea\3\2\2\2\u00eb\u00ec\3\2"+
		"\2\2\u00ec-\3\2\2\2\u00ed\u00f2\7\21\2\2\u00ee\u00ef\7\r\2\2\u00ef\u00f1"+
		"\7\21\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0\3\2\2\2"+
		"\u00f2\u00f3\3\2\2\2\u00f3\u00f6\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5\u00f7"+
		"\7\r\2\2\u00f6\u00f5\3\2\2\2\u00f6\u00f7\3\2\2\2\u00f7/\3\2\2\2\u00f8"+
		"\u00fd\7\22\2\2\u00f9\u00fa\7\r\2\2\u00fa\u00fc\7\22\2\2\u00fb\u00f9\3"+
		"\2\2\2\u00fc\u00ff\3\2\2\2\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe"+
		"\u0101\3\2\2\2\u00ff\u00fd\3\2\2\2\u0100\u0102\7\r\2\2\u0101\u0100\3\2"+
		"\2\2\u0101\u0102\3\2\2\2\u0102\61\3\2\2\2\36\65:@RXbh\u0087\u008e\u0096"+
		"\u009b\u00a0\u00a5\u00aa\u00af\u00ba\u00be\u00c8\u00d1\u00d5\u00dc\u00e0"+
		"\u00e7\u00eb\u00f2\u00f6\u00fd\u0101";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}