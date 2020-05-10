// Generated from /Users/zhanglei/Work/Projects/ware/tauris4/tauris-config/src/main/antlr4/Tauris.g4 by ANTLR 4.6

    package antlr4.tauris;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TaurisLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, Integer=12, Float=13, Boolean=14, Null=15, String=16, 
		Environ=17, ID=18, WS=19, LINE_COMMENT=20;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "Integer", "Float", "Boolean", "Null", "String", "Environ", 
		"STRING", "STRING_ESCAPE_SEQ", "ESC", "INT", "NAME", "EXP", "ID", "WS", 
		"LINE_COMMENT"
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


	public TaurisLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Tauris.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\26\u00c4\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\5\r^\n\r\3\r\3\r\3\16\5\16c\n"+
		"\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\5"+
		"\17r\n\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\7\23\u0082\n\23\f\23\16\23\u0085\13\23\3\23\3\23\3\23\3\23"+
		"\7\23\u008b\n\23\f\23\16\23\u008e\13\23\3\23\5\23\u0091\n\23\3\24\3\24"+
		"\3\24\3\25\3\25\3\25\3\26\3\26\3\26\7\26\u009c\n\26\f\26\16\26\u009f\13"+
		"\26\5\26\u00a1\n\26\3\27\3\27\6\27\u00a5\n\27\r\27\16\27\u00a6\3\30\3"+
		"\30\5\30\u00ab\n\30\3\30\3\30\3\31\3\31\6\31\u00b1\n\31\r\31\16\31\u00b2"+
		"\3\32\6\32\u00b6\n\32\r\32\16\32\u00b7\3\32\3\32\3\33\3\33\7\33\u00be"+
		"\n\33\f\33\16\33\u00c1\13\33\3\33\3\33\2\2\34\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\2\'\2)\2+"+
		"\2-\2/\2\61\24\63\25\65\26\3\2\16\6\2\f\f\17\17))^^\6\2\f\f\17\17$$^^"+
		"\n\2$$\61\61^^ddhhppttvv\3\2\63;\3\2\62;\4\2C\\c|\b\2\60\60\62;C\\^^a"+
		"ac|\4\2GGgg\4\2--//\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\u00cc"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\3\67\3\2\2\2\5=\3\2\2\2\7"+
		"D\3\2\2\2\tK\3\2\2\2\13M\3\2\2\2\rO\3\2\2\2\17R\3\2\2\2\21T\3\2\2\2\23"+
		"V\3\2\2\2\25X\3\2\2\2\27Z\3\2\2\2\31]\3\2\2\2\33b\3\2\2\2\35q\3\2\2\2"+
		"\37s\3\2\2\2!x\3\2\2\2#z\3\2\2\2%\u0090\3\2\2\2\'\u0092\3\2\2\2)\u0095"+
		"\3\2\2\2+\u00a0\3\2\2\2-\u00a2\3\2\2\2/\u00a8\3\2\2\2\61\u00ae\3\2\2\2"+
		"\63\u00b5\3\2\2\2\65\u00bb\3\2\2\2\678\7k\2\289\7p\2\29:\7r\2\2:;\7w\2"+
		"\2;<\7v\2\2<\4\3\2\2\2=>\7h\2\2>?\7k\2\2?@\7n\2\2@A\7v\2\2AB\7g\2\2BC"+
		"\7t\2\2C\6\3\2\2\2DE\7q\2\2EF\7w\2\2FG\7v\2\2GH\7r\2\2HI\7w\2\2IJ\7v\2"+
		"\2J\b\3\2\2\2KL\7}\2\2L\n\3\2\2\2MN\7\177\2\2N\f\3\2\2\2OP\7?\2\2PQ\7"+
		"@\2\2Q\16\3\2\2\2RS\7=\2\2S\20\3\2\2\2TU\7]\2\2U\22\3\2\2\2VW\7_\2\2W"+
		"\24\3\2\2\2XY\7.\2\2Y\26\3\2\2\2Z[\7<\2\2[\30\3\2\2\2\\^\7/\2\2]\\\3\2"+
		"\2\2]^\3\2\2\2^_\3\2\2\2_`\5+\26\2`\32\3\2\2\2ac\7/\2\2ba\3\2\2\2bc\3"+
		"\2\2\2cd\3\2\2\2de\5+\26\2ef\7\60\2\2fg\5+\26\2g\34\3\2\2\2hi\7v\2\2i"+
		"j\7t\2\2jk\7w\2\2kr\7g\2\2lm\7h\2\2mn\7c\2\2no\7n\2\2op\7u\2\2pr\7g\2"+
		"\2qh\3\2\2\2ql\3\2\2\2r\36\3\2\2\2st\7p\2\2tu\7w\2\2uv\7n\2\2vw\7n\2\2"+
		"w \3\2\2\2xy\5%\23\2y\"\3\2\2\2z{\7b\2\2{|\5-\27\2|}\7b\2\2}$\3\2\2\2"+
		"~\u0083\7)\2\2\177\u0082\5\'\24\2\u0080\u0082\n\2\2\2\u0081\177\3\2\2"+
		"\2\u0081\u0080\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084"+
		"\3\2\2\2\u0084\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0091\7)\2\2\u0087"+
		"\u008c\7$\2\2\u0088\u008b\5\'\24\2\u0089\u008b\n\3\2\2\u008a\u0088\3\2"+
		"\2\2\u008a\u0089\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0091\7$"+
		"\2\2\u0090~\3\2\2\2\u0090\u0087\3\2\2\2\u0091&\3\2\2\2\u0092\u0093\7^"+
		"\2\2\u0093\u0094\13\2\2\2\u0094(\3\2\2\2\u0095\u0096\7^\2\2\u0096\u0097"+
		"\t\4\2\2\u0097*\3\2\2\2\u0098\u00a1\7\62\2\2\u0099\u009d\t\5\2\2\u009a"+
		"\u009c\t\6\2\2\u009b\u009a\3\2\2\2\u009c\u009f\3\2\2\2\u009d\u009b\3\2"+
		"\2\2\u009d\u009e\3\2\2\2\u009e\u00a1\3\2\2\2\u009f\u009d\3\2\2\2\u00a0"+
		"\u0098\3\2\2\2\u00a0\u0099\3\2\2\2\u00a1,\3\2\2\2\u00a2\u00a4\t\7\2\2"+
		"\u00a3\u00a5\t\b\2\2\u00a4\u00a3\3\2\2\2\u00a5\u00a6\3\2\2\2\u00a6\u00a4"+
		"\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7.\3\2\2\2\u00a8\u00aa\t\t\2\2\u00a9"+
		"\u00ab\t\n\2\2\u00aa\u00a9\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ac\3\2"+
		"\2\2\u00ac\u00ad\5+\26\2\u00ad\60\3\2\2\2\u00ae\u00b0\t\7\2\2\u00af\u00b1"+
		"\t\13\2\2\u00b0\u00af\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2\u00b0\3\2\2\2"+
		"\u00b2\u00b3\3\2\2\2\u00b3\62\3\2\2\2\u00b4\u00b6\t\f\2\2\u00b5\u00b4"+
		"\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8"+
		"\u00b9\3\2\2\2\u00b9\u00ba\b\32\2\2\u00ba\64\3\2\2\2\u00bb\u00bf\7%\2"+
		"\2\u00bc\u00be\n\r\2\2\u00bd\u00bc\3\2\2\2\u00be\u00c1\3\2\2\2\u00bf\u00bd"+
		"\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c2"+
		"\u00c3\b\33\2\2\u00c3\66\3\2\2\2\22\2]bq\u0081\u0083\u008a\u008c\u0090"+
		"\u009d\u00a0\u00a6\u00aa\u00b2\u00b7\u00bf\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}