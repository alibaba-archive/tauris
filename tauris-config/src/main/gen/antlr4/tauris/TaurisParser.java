// Generated from /Users/zhanglei/Work/Projects/ware/tauris4/tauris-config/src/main/antlr4/Tauris.g4 by ANTLR 4.6

    package antlr4.tauris;

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
		T__9=10, T__10=11, Integer=12, Float=13, Boolean=14, Null=15, String=16, 
		Environ=17, ID=18, WS=19, LINE_COMMENT=20;
	public static final int
		RULE_pipeline = 0, RULE_inputGroup = 1, RULE_filterGroup = 2, RULE_outputGroup = 3, 
		RULE_pluginGroup = 4, RULE_plugins = 5, RULE_plugin = 6, RULE_assignments = 7, 
		RULE_assignment = 8, RULE_name = 9, RULE_value = 10, RULE_simpleValue = 11, 
		RULE_environValue = 12, RULE_array = 13, RULE_hash = 14, RULE_keyValues = 15, 
		RULE_keyValue = 16, RULE_key = 17, RULE_strings = 18, RULE_integers = 19, 
		RULE_floats = 20, RULE_booleans = 21, RULE_nulls = 22;
	public static final String[] ruleNames = {
		"pipeline", "inputGroup", "filterGroup", "outputGroup", "pluginGroup", 
		"plugins", "plugin", "assignments", "assignment", "name", "value", "simpleValue", 
		"environValue", "array", "hash", "keyValues", "keyValue", "key", "strings", 
		"integers", "floats", "booleans", "nulls"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'input'", "'filter'", "'output'", "'{'", "'}'", "'=>'", "';'", 
		"'['", "']'", "','", "':'", null, null, null, "'null'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		"Integer", "Float", "Boolean", "Null", "String", "Environ", "ID", "WS", 
		"LINE_COMMENT"
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
			setState(47); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(46);
				inputGroup();
				}
				}
				setState(49); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==T__0 );
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(51);
				filterGroup();
				}
				}
				setState(56);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(58); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(57);
				outputGroup();
				}
				}
				setState(60); 
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
			setState(62);
			match(T__0);
			setState(63);
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
			setState(65);
			match(T__1);
			setState(66);
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
			setState(68);
			match(T__2);
			setState(69);
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
			setState(71);
			match(ID);
			setState(72);
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
			setState(74);
			match(T__3);
			setState(78);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(75);
					assignment();
					}
					} 
				}
				setState(80);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(82); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(81);
				plugin();
				}
				}
				setState(84); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << ID))) != 0) );
			setState(86);
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
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public AssignmentsContext assignments() {
			return getRuleContext(AssignmentsContext.class,0);
		}
		public PluginContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_plugin; }
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
			setState(88);
			name();
			setState(89);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitAssignments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentsContext assignments() throws RecognitionException {
		AssignmentsContext _localctx = new AssignmentsContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_assignments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(T__3);
			setState(95);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__1) | (1L << T__2) | (1L << ID))) != 0)) {
				{
				{
				setState(92);
				assignment();
				}
				}
				setState(97);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(98);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_assignment);
		try {
			setState(126);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(100);
				name();
				setState(101);
				match(T__5);
				setState(102);
				value();
				setState(103);
				match(T__6);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(105);
				name();
				setState(106);
				match(T__5);
				setState(107);
				environValue();
				setState(108);
				match(T__6);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(110);
				name();
				setState(111);
				match(T__5);
				setState(112);
				assignments();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(114);
				name();
				setState(115);
				match(T__5);
				setState(116);
				plugins();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(118);
				name();
				setState(119);
				match(T__5);
				setState(120);
				plugin();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(122);
				name();
				setState(123);
				match(T__5);
				setState(124);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_value);
		try {
			setState(133);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case Integer:
			case Float:
			case Boolean:
			case Null:
			case String:
				enterOuterAlt(_localctx, 1);
				{
				setState(130);
				simpleValue();
				}
				break;
			case Environ:
				enterOuterAlt(_localctx, 2);
				{
				setState(131);
				environValue();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 3);
				{
				setState(132);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitSimpleValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleValueContext simpleValue() throws RecognitionException {
		SimpleValueContext _localctx = new SimpleValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_simpleValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitEnvironValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnvironValueContext environValue() throws RecognitionException {
		EnvironValueContext _localctx = new EnvironValueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_environValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_array);
		int _la;
		try {
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(139);
				match(T__7);
				setState(141);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Integer) {
					{
					setState(140);
					integers();
					}
				}

				setState(143);
				match(T__8);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				match(T__7);
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==String) {
					{
					setState(145);
					strings();
					}
				}

				setState(148);
				match(T__8);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(149);
				match(T__7);
				setState(151);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Float) {
					{
					setState(150);
					floats();
					}
				}

				setState(153);
				match(T__8);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(154);
				match(T__7);
				setState(156);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Boolean) {
					{
					setState(155);
					booleans();
					}
				}

				setState(158);
				match(T__8);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(159);
				match(T__7);
				setState(161);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==Null) {
					{
					setState(160);
					nulls();
					}
				}

				setState(163);
				match(T__8);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(164);
				match(T__7);
				setState(165);
				match(T__8);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitHash(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HashContext hash() throws RecognitionException {
		HashContext _localctx = new HashContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_hash);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(T__3);
			setState(169);
			keyValues();
			setState(170);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitKeyValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyValuesContext keyValues() throws RecognitionException {
		KeyValuesContext _localctx = new KeyValuesContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_keyValues);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(172);
			keyValue();
			setState(177);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(173);
					match(T__9);
					setState(174);
					keyValue();
					}
					} 
				}
				setState(179);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
			}
			setState(181);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(180);
				match(T__9);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitKeyValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyValueContext keyValue() throws RecognitionException {
		KeyValueContext _localctx = new KeyValueContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_keyValue);
		try {
			setState(191);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,16,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(183);
				key();
				setState(184);
				match(T__10);
				setState(185);
				simpleValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				key();
				setState(188);
				match(T__10);
				setState(189);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitKey(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KeyContext key() throws RecognitionException {
		KeyContext _localctx = new KeyContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_key);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitStrings(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StringsContext strings() throws RecognitionException {
		StringsContext _localctx = new StringsContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_strings);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			match(String);
			setState(200);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(196);
					match(T__9);
					setState(197);
					match(String);
					}
					} 
				}
				setState(202);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			}
			setState(204);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(203);
				match(T__9);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitIntegers(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntegersContext integers() throws RecognitionException {
		IntegersContext _localctx = new IntegersContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_integers);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(Integer);
			setState(211);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(207);
					match(T__9);
					setState(208);
					match(Integer);
					}
					} 
				}
				setState(213);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			}
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(214);
				match(T__9);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitFloats(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FloatsContext floats() throws RecognitionException {
		FloatsContext _localctx = new FloatsContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_floats);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			match(Float);
			setState(222);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(218);
					match(T__9);
					setState(219);
					match(Float);
					}
					} 
				}
				setState(224);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
			}
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(225);
				match(T__9);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitBooleans(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BooleansContext booleans() throws RecognitionException {
		BooleansContext _localctx = new BooleansContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_booleans);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(228);
			match(Boolean);
			setState(233);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(229);
					match(T__9);
					setState(230);
					match(Boolean);
					}
					} 
				}
				setState(235);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			setState(237);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(236);
				match(T__9);
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
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TaurisVisitor ) return ((TaurisVisitor<? extends T>)visitor).visitNulls(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NullsContext nulls() throws RecognitionException {
		NullsContext _localctx = new NullsContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_nulls);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			match(Null);
			setState(244);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(240);
					match(T__9);
					setState(241);
					match(Null);
					}
					} 
				}
				setState(246);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,25,_ctx);
			}
			setState(248);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(247);
				match(T__9);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\26\u00fd\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\6\2\62"+
		"\n\2\r\2\16\2\63\3\2\7\2\67\n\2\f\2\16\2:\13\2\3\2\6\2=\n\2\r\2\16\2>"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\7\7O\n\7\f\7"+
		"\16\7R\13\7\3\7\6\7U\n\7\r\7\16\7V\3\7\3\7\3\b\3\b\3\b\3\t\3\t\7\t`\n"+
		"\t\f\t\16\tc\13\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u0081"+
		"\n\n\3\13\3\13\3\f\3\f\3\f\5\f\u0088\n\f\3\r\3\r\3\16\3\16\3\17\3\17\5"+
		"\17\u0090\n\17\3\17\3\17\3\17\5\17\u0095\n\17\3\17\3\17\3\17\5\17\u009a"+
		"\n\17\3\17\3\17\3\17\5\17\u009f\n\17\3\17\3\17\3\17\5\17\u00a4\n\17\3"+
		"\17\3\17\3\17\5\17\u00a9\n\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21\7\21"+
		"\u00b2\n\21\f\21\16\21\u00b5\13\21\3\21\5\21\u00b8\n\21\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\5\22\u00c2\n\22\3\23\3\23\3\24\3\24\3\24\7\24"+
		"\u00c9\n\24\f\24\16\24\u00cc\13\24\3\24\5\24\u00cf\n\24\3\25\3\25\3\25"+
		"\7\25\u00d4\n\25\f\25\16\25\u00d7\13\25\3\25\5\25\u00da\n\25\3\26\3\26"+
		"\3\26\7\26\u00df\n\26\f\26\16\26\u00e2\13\26\3\26\5\26\u00e5\n\26\3\27"+
		"\3\27\3\27\7\27\u00ea\n\27\f\27\16\27\u00ed\13\27\3\27\5\27\u00f0\n\27"+
		"\3\30\3\30\3\30\7\30\u00f5\n\30\f\30\16\30\u00f8\13\30\3\30\5\30\u00fb"+
		"\n\30\3\30\2\2\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\4"+
		"\4\2\3\5\24\24\3\2\16\22\u0109\2\61\3\2\2\2\4@\3\2\2\2\6C\3\2\2\2\bF\3"+
		"\2\2\2\nI\3\2\2\2\fL\3\2\2\2\16Z\3\2\2\2\20]\3\2\2\2\22\u0080\3\2\2\2"+
		"\24\u0082\3\2\2\2\26\u0087\3\2\2\2\30\u0089\3\2\2\2\32\u008b\3\2\2\2\34"+
		"\u00a8\3\2\2\2\36\u00aa\3\2\2\2 \u00ae\3\2\2\2\"\u00c1\3\2\2\2$\u00c3"+
		"\3\2\2\2&\u00c5\3\2\2\2(\u00d0\3\2\2\2*\u00db\3\2\2\2,\u00e6\3\2\2\2."+
		"\u00f1\3\2\2\2\60\62\5\4\3\2\61\60\3\2\2\2\62\63\3\2\2\2\63\61\3\2\2\2"+
		"\63\64\3\2\2\2\648\3\2\2\2\65\67\5\6\4\2\66\65\3\2\2\2\67:\3\2\2\28\66"+
		"\3\2\2\289\3\2\2\29<\3\2\2\2:8\3\2\2\2;=\5\b\5\2<;\3\2\2\2=>\3\2\2\2>"+
		"<\3\2\2\2>?\3\2\2\2?\3\3\2\2\2@A\7\3\2\2AB\5\f\7\2B\5\3\2\2\2CD\7\4\2"+
		"\2DE\5\f\7\2E\7\3\2\2\2FG\7\5\2\2GH\5\f\7\2H\t\3\2\2\2IJ\7\24\2\2JK\5"+
		"\f\7\2K\13\3\2\2\2LP\7\6\2\2MO\5\22\n\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2\2"+
		"PQ\3\2\2\2QT\3\2\2\2RP\3\2\2\2SU\5\16\b\2TS\3\2\2\2UV\3\2\2\2VT\3\2\2"+
		"\2VW\3\2\2\2WX\3\2\2\2XY\7\7\2\2Y\r\3\2\2\2Z[\5\24\13\2[\\\5\20\t\2\\"+
		"\17\3\2\2\2]a\7\6\2\2^`\5\22\n\2_^\3\2\2\2`c\3\2\2\2a_\3\2\2\2ab\3\2\2"+
		"\2bd\3\2\2\2ca\3\2\2\2de\7\7\2\2e\21\3\2\2\2fg\5\24\13\2gh\7\b\2\2hi\5"+
		"\26\f\2ij\7\t\2\2j\u0081\3\2\2\2kl\5\24\13\2lm\7\b\2\2mn\5\32\16\2no\7"+
		"\t\2\2o\u0081\3\2\2\2pq\5\24\13\2qr\7\b\2\2rs\5\20\t\2s\u0081\3\2\2\2"+
		"tu\5\24\13\2uv\7\b\2\2vw\5\f\7\2w\u0081\3\2\2\2xy\5\24\13\2yz\7\b\2\2"+
		"z{\5\16\b\2{\u0081\3\2\2\2|}\5\24\13\2}~\7\b\2\2~\177\5\36\20\2\177\u0081"+
		"\3\2\2\2\u0080f\3\2\2\2\u0080k\3\2\2\2\u0080p\3\2\2\2\u0080t\3\2\2\2\u0080"+
		"x\3\2\2\2\u0080|\3\2\2\2\u0081\23\3\2\2\2\u0082\u0083\t\2\2\2\u0083\25"+
		"\3\2\2\2\u0084\u0088\5\30\r\2\u0085\u0088\5\32\16\2\u0086\u0088\5\34\17"+
		"\2\u0087\u0084\3\2\2\2\u0087\u0085\3\2\2\2\u0087\u0086\3\2\2\2\u0088\27"+
		"\3\2\2\2\u0089\u008a\t\3\2\2\u008a\31\3\2\2\2\u008b\u008c\7\23\2\2\u008c"+
		"\33\3\2\2\2\u008d\u008f\7\n\2\2\u008e\u0090\5(\25\2\u008f\u008e\3\2\2"+
		"\2\u008f\u0090\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u00a9\7\13\2\2\u0092"+
		"\u0094\7\n\2\2\u0093\u0095\5&\24\2\u0094\u0093\3\2\2\2\u0094\u0095\3\2"+
		"\2\2\u0095\u0096\3\2\2\2\u0096\u00a9\7\13\2\2\u0097\u0099\7\n\2\2\u0098"+
		"\u009a\5*\26\2\u0099\u0098\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009b\3\2"+
		"\2\2\u009b\u00a9\7\13\2\2\u009c\u009e\7\n\2\2\u009d\u009f\5,\27\2\u009e"+
		"\u009d\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a9\7\13"+
		"\2\2\u00a1\u00a3\7\n\2\2\u00a2\u00a4\5.\30\2\u00a3\u00a2\3\2\2\2\u00a3"+
		"\u00a4\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5\u00a9\7\13\2\2\u00a6\u00a7\7"+
		"\n\2\2\u00a7\u00a9\7\13\2\2\u00a8\u008d\3\2\2\2\u00a8\u0092\3\2\2\2\u00a8"+
		"\u0097\3\2\2\2\u00a8\u009c\3\2\2\2\u00a8\u00a1\3\2\2\2\u00a8\u00a6\3\2"+
		"\2\2\u00a9\35\3\2\2\2\u00aa\u00ab\7\6\2\2\u00ab\u00ac\5 \21\2\u00ac\u00ad"+
		"\7\7\2\2\u00ad\37\3\2\2\2\u00ae\u00b3\5\"\22\2\u00af\u00b0\7\f\2\2\u00b0"+
		"\u00b2\5\"\22\2\u00b1\u00af\3\2\2\2\u00b2\u00b5\3\2\2\2\u00b3\u00b1\3"+
		"\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00b7\3\2\2\2\u00b5\u00b3\3\2\2\2\u00b6"+
		"\u00b8\7\f\2\2\u00b7\u00b6\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8!\3\2\2\2"+
		"\u00b9\u00ba\5$\23\2\u00ba\u00bb\7\r\2\2\u00bb\u00bc\5\30\r\2\u00bc\u00c2"+
		"\3\2\2\2\u00bd\u00be\5$\23\2\u00be\u00bf\7\r\2\2\u00bf\u00c0\5\32\16\2"+
		"\u00c0\u00c2\3\2\2\2\u00c1\u00b9\3\2\2\2\u00c1\u00bd\3\2\2\2\u00c2#\3"+
		"\2\2\2\u00c3\u00c4\7\22\2\2\u00c4%\3\2\2\2\u00c5\u00ca\7\22\2\2\u00c6"+
		"\u00c7\7\f\2\2\u00c7\u00c9\7\22\2\2\u00c8\u00c6\3\2\2\2\u00c9\u00cc\3"+
		"\2\2\2\u00ca\u00c8\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00ce\3\2\2\2\u00cc"+
		"\u00ca\3\2\2\2\u00cd\u00cf\7\f\2\2\u00ce\u00cd\3\2\2\2\u00ce\u00cf\3\2"+
		"\2\2\u00cf\'\3\2\2\2\u00d0\u00d5\7\16\2\2\u00d1\u00d2\7\f\2\2\u00d2\u00d4"+
		"\7\16\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d7\3\2\2\2\u00d5\u00d3\3\2\2\2"+
		"\u00d5\u00d6\3\2\2\2\u00d6\u00d9\3\2\2\2\u00d7\u00d5\3\2\2\2\u00d8\u00da"+
		"\7\f\2\2\u00d9\u00d8\3\2\2\2\u00d9\u00da\3\2\2\2\u00da)\3\2\2\2\u00db"+
		"\u00e0\7\17\2\2\u00dc\u00dd\7\f\2\2\u00dd\u00df\7\17\2\2\u00de\u00dc\3"+
		"\2\2\2\u00df\u00e2\3\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1"+
		"\u00e4\3\2\2\2\u00e2\u00e0\3\2\2\2\u00e3\u00e5\7\f\2\2\u00e4\u00e3\3\2"+
		"\2\2\u00e4\u00e5\3\2\2\2\u00e5+\3\2\2\2\u00e6\u00eb\7\20\2\2\u00e7\u00e8"+
		"\7\f\2\2\u00e8\u00ea\7\20\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00ed\3\2\2\2"+
		"\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec\u00ef\3\2\2\2\u00ed\u00eb"+
		"\3\2\2\2\u00ee\u00f0\7\f\2\2\u00ef\u00ee\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0"+
		"-\3\2\2\2\u00f1\u00f6\7\21\2\2\u00f2\u00f3\7\f\2\2\u00f3\u00f5\7\21\2"+
		"\2\u00f4\u00f2\3\2\2\2\u00f5\u00f8\3\2\2\2\u00f6\u00f4\3\2\2\2\u00f6\u00f7"+
		"\3\2\2\2\u00f7\u00fa\3\2\2\2\u00f8\u00f6\3\2\2\2\u00f9\u00fb\7\f\2\2\u00fa"+
		"\u00f9\3\2\2\2\u00fa\u00fb\3\2\2\2\u00fb/\3\2\2\2\35\638>PVa\u0080\u0087"+
		"\u008f\u0094\u0099\u009e\u00a3\u00a8\u00b3\u00b7\u00c1\u00ca\u00ce\u00d5"+
		"\u00d9\u00e0\u00e4\u00eb\u00ef\u00f6\u00fa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}