// Generated from Tauris.g4 by ANTLR 4.6

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
		T__9=10, T__10=11, T__11=12, Integer=13, Float=14, Boolean=15, Null=16, 
		String=17, Environ=18, ID=19, WS=20, LINE_COMMENT=21;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "Integer", "Float", "Boolean", "Null", "String", 
		"Environ", "STRING", "STRING_ESCAPE_SEQ", "ESC", "INT", "NAME", "EXP", 
		"ID", "WS", "LINE_COMMENT"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\27\u00c8\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7"+
		"\3\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\5\16b\n\16"+
		"\3\16\3\16\3\17\5\17g\n\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\5\20v\n\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\7\24\u0086\n\24\f\24\16\24\u0089\13\24"+
		"\3\24\3\24\3\24\3\24\7\24\u008f\n\24\f\24\16\24\u0092\13\24\3\24\5\24"+
		"\u0095\n\24\3\25\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\7\27\u00a0\n"+
		"\27\f\27\16\27\u00a3\13\27\5\27\u00a5\n\27\3\30\3\30\6\30\u00a9\n\30\r"+
		"\30\16\30\u00aa\3\31\3\31\5\31\u00af\n\31\3\31\3\31\3\32\3\32\6\32\u00b5"+
		"\n\32\r\32\16\32\u00b6\3\33\6\33\u00ba\n\33\r\33\16\33\u00bb\3\33\3\33"+
		"\3\34\3\34\7\34\u00c2\n\34\f\34\16\34\u00c5\13\34\3\34\3\34\2\2\35\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\2)\2+\2-\2/\2\61\2\63\25\65\26\67\27\3\2\16\6\2\f\f\17"+
		"\17))^^\6\2\f\f\17\17$$^^\n\2$$\61\61^^ddhhppttvv\3\2\63;\3\2\62;\4\2"+
		"C\\c|\b\2\60\60\62;C\\^^aac|\4\2GGgg\4\2--//\6\2\62;C\\aac|\5\2\13\f\17"+
		"\17\"\"\4\2\f\f\17\17\u00d0\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3"+
		"\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2"+
		"\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2"+
		"\67\3\2\2\2\39\3\2\2\2\5?\3\2\2\2\7F\3\2\2\2\tM\3\2\2\2\13O\3\2\2\2\r"+
		"Q\3\2\2\2\17S\3\2\2\2\21V\3\2\2\2\23X\3\2\2\2\25Z\3\2\2\2\27\\\3\2\2\2"+
		"\31^\3\2\2\2\33a\3\2\2\2\35f\3\2\2\2\37u\3\2\2\2!w\3\2\2\2#|\3\2\2\2%"+
		"~\3\2\2\2\'\u0094\3\2\2\2)\u0096\3\2\2\2+\u0099\3\2\2\2-\u00a4\3\2\2\2"+
		"/\u00a6\3\2\2\2\61\u00ac\3\2\2\2\63\u00b2\3\2\2\2\65\u00b9\3\2\2\2\67"+
		"\u00bf\3\2\2\29:\7k\2\2:;\7p\2\2;<\7r\2\2<=\7w\2\2=>\7v\2\2>\4\3\2\2\2"+
		"?@\7h\2\2@A\7k\2\2AB\7n\2\2BC\7v\2\2CD\7g\2\2DE\7t\2\2E\6\3\2\2\2FG\7"+
		"q\2\2GH\7w\2\2HI\7v\2\2IJ\7r\2\2JK\7w\2\2KL\7v\2\2L\b\3\2\2\2MN\7}\2\2"+
		"N\n\3\2\2\2OP\7\177\2\2P\f\3\2\2\2QR\7\60\2\2R\16\3\2\2\2ST\7?\2\2TU\7"+
		"@\2\2U\20\3\2\2\2VW\7=\2\2W\22\3\2\2\2XY\7]\2\2Y\24\3\2\2\2Z[\7_\2\2["+
		"\26\3\2\2\2\\]\7.\2\2]\30\3\2\2\2^_\7<\2\2_\32\3\2\2\2`b\7/\2\2a`\3\2"+
		"\2\2ab\3\2\2\2bc\3\2\2\2cd\5-\27\2d\34\3\2\2\2eg\7/\2\2fe\3\2\2\2fg\3"+
		"\2\2\2gh\3\2\2\2hi\5-\27\2ij\7\60\2\2jk\5-\27\2k\36\3\2\2\2lm\7v\2\2m"+
		"n\7t\2\2no\7w\2\2ov\7g\2\2pq\7h\2\2qr\7c\2\2rs\7n\2\2st\7u\2\2tv\7g\2"+
		"\2ul\3\2\2\2up\3\2\2\2v \3\2\2\2wx\7p\2\2xy\7w\2\2yz\7n\2\2z{\7n\2\2{"+
		"\"\3\2\2\2|}\5\'\24\2}$\3\2\2\2~\177\7b\2\2\177\u0080\5/\30\2\u0080\u0081"+
		"\7b\2\2\u0081&\3\2\2\2\u0082\u0087\7)\2\2\u0083\u0086\5)\25\2\u0084\u0086"+
		"\n\2\2\2\u0085\u0083\3\2\2\2\u0085\u0084\3\2\2\2\u0086\u0089\3\2\2\2\u0087"+
		"\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\u008a\3\2\2\2\u0089\u0087\3\2"+
		"\2\2\u008a\u0095\7)\2\2\u008b\u0090\7$\2\2\u008c\u008f\5)\25\2\u008d\u008f"+
		"\n\3\2\2\u008e\u008c\3\2\2\2\u008e\u008d\3\2\2\2\u008f\u0092\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u0093\3\2\2\2\u0092\u0090\3\2"+
		"\2\2\u0093\u0095\7$\2\2\u0094\u0082\3\2\2\2\u0094\u008b\3\2\2\2\u0095"+
		"(\3\2\2\2\u0096\u0097\7^\2\2\u0097\u0098\13\2\2\2\u0098*\3\2\2\2\u0099"+
		"\u009a\7^\2\2\u009a\u009b\t\4\2\2\u009b,\3\2\2\2\u009c\u00a5\7\62\2\2"+
		"\u009d\u00a1\t\5\2\2\u009e\u00a0\t\6\2\2\u009f\u009e\3\2\2\2\u00a0\u00a3"+
		"\3\2\2\2\u00a1\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3"+
		"\u00a1\3\2\2\2\u00a4\u009c\3\2\2\2\u00a4\u009d\3\2\2\2\u00a5.\3\2\2\2"+
		"\u00a6\u00a8\t\7\2\2\u00a7\u00a9\t\b\2\2\u00a8\u00a7\3\2\2\2\u00a9\u00aa"+
		"\3\2\2\2\u00aa\u00a8\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\60\3\2\2\2\u00ac"+
		"\u00ae\t\t\2\2\u00ad\u00af\t\n\2\2\u00ae\u00ad\3\2\2\2\u00ae\u00af\3\2"+
		"\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b1\5-\27\2\u00b1\62\3\2\2\2\u00b2\u00b4"+
		"\t\7\2\2\u00b3\u00b5\t\13\2\2\u00b4\u00b3\3\2\2\2\u00b5\u00b6\3\2\2\2"+
		"\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\64\3\2\2\2\u00b8\u00ba"+
		"\t\f\2\2\u00b9\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bb"+
		"\u00bc\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00be\b\33\2\2\u00be\66\3\2\2"+
		"\2\u00bf\u00c3\7%\2\2\u00c0\u00c2\n\r\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c5"+
		"\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c6\3\2\2\2\u00c5"+
		"\u00c3\3\2\2\2\u00c6\u00c7\b\34\2\2\u00c78\3\2\2\2\22\2afu\u0085\u0087"+
		"\u008e\u0090\u0094\u00a1\u00a4\u00aa\u00ae\u00b6\u00bb\u00c3\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}