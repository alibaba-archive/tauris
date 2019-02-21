// Generated from /Users/zhanglei/Work/Projects/ware/tauris2/tauris-core/src/main/antlr4/TExpr.g4 by ANTLR 4.6

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
public class TExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, Integer=4, Float=5, Boolean=6, String=7, Regex=8, 
		AND=9, OR=10, NOT=11, IS=12, IN=13, TRUE=14, FALSE=15, GT=16, GE=17, LT=18, 
		LE=19, EQ=20, NE=21, MATCH=22, LPAREN=23, RPAREN=24, INT=25, FLOAT=26, 
		IDENTIFIER=27, VARIABLE=28, PLUS=29, MINUS=30, MUL=31, DIV=32, MOD=33, 
		POINT=34, E=35, LSHIFT=36, RSHIFT=37, RSHIFT3=38, BAND=39, BEOR=40, BIOR=41, 
		NL=42, DIGIT=43, WS=44;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "Integer", "Float", "Boolean", "String", "Regex", 
		"REGEX", "STRING", "STRING_ESCAPE_SEQ", "ESC", "NAME", "AND", "OR", "NOT", 
		"IS", "IN", "TRUE", "FALSE", "GT", "GE", "LT", "LE", "EQ", "NE", "MATCH", 
		"LPAREN", "RPAREN", "INT", "FLOAT", "IDENTIFIER", "VARIABLE", "PLUS", 
		"MINUS", "MUL", "DIV", "MOD", "POINT", "E", "LSHIFT", "RSHIFT", "RSHIFT3", 
		"BAND", "BEOR", "BIOR", "NL", "DIGIT", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'['", "']'", "','", null, null, null, null, null, "'&&'", "'||'", 
		"'not'", "'is'", "'in'", "'true'", "'false'", "'>'", "'>='", "'<'", "'<='", 
		"'=='", "'!='", "'=~'", "'('", "')'", null, null, null, null, "'+'", "'-'", 
		"'*'", "'/'", "'%'", "'.'", null, "'<<'", "'>>'", "'>>>'", "'&'", "'^'", 
		"'|'", "'\n'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "Integer", "Float", "Boolean", "String", "Regex", 
		"AND", "OR", "NOT", "IS", "IN", "TRUE", "FALSE", "GT", "GE", "LT", "LE", 
		"EQ", "NE", "MATCH", "LPAREN", "RPAREN", "INT", "FLOAT", "IDENTIFIER", 
		"VARIABLE", "PLUS", "MINUS", "MUL", "DIV", "MOD", "POINT", "E", "LSHIFT", 
		"RSHIFT", "RSHIFT3", "BAND", "BEOR", "BIOR", "NL", "DIGIT", "WS"
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


	public TExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TExpr.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2.\u0135\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\3\2\3\2\3\3\3\3\3"+
		"\4\3\4\3\5\5\5m\n\5\3\5\3\5\3\6\5\6r\n\6\3\6\3\6\3\6\3\6\3\7\3\7\5\7z"+
		"\n\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\7\n\u0083\n\n\f\n\16\n\u0086\13\n\3\n"+
		"\3\n\3\13\3\13\3\13\7\13\u008d\n\13\f\13\16\13\u0090\13\13\3\13\3\13\3"+
		"\13\3\13\7\13\u0096\n\13\f\13\16\13\u0099\13\13\3\13\5\13\u009c\n\13\3"+
		"\f\3\f\3\f\3\r\3\r\3\r\3\16\3\16\6\16\u00a6\n\16\r\16\16\16\u00a7\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26"+
		"\3\27\3\27\3\27\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33"+
		"\3\34\3\34\3\34\3\35\3\35\3\36\3\36\3\37\5\37\u00dd\n\37\3\37\6\37\u00e0"+
		"\n\37\r\37\16\37\u00e1\3 \5 \u00e5\n \3 \6 \u00e8\n \r \16 \u00e9\3 \3"+
		" \6 \u00ee\n \r \16 \u00ef\5 \u00f2\n \3!\3!\7!\u00f6\n!\f!\16!\u00f9"+
		"\13!\3\"\3\"\3\"\7\"\u00fe\n\"\f\"\16\"\u0101\13\"\3\"\3\"\3\"\7\"\u0106"+
		"\n\"\f\"\16\"\u0109\13\"\5\"\u010b\n\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'"+
		"\3(\3(\3)\3)\3*\3*\3*\3+\3+\3+\3,\3,\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60"+
		"\3\61\3\61\3\62\6\62\u0130\n\62\r\62\16\62\u0131\3\62\3\62\2\2\63\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\2\25\2\27\2\31\2\33\2\35\13\37\f!\r"+
		"#\16%\17\'\20)\21+\22-\23/\24\61\25\63\26\65\27\67\309\31;\32=\33?\34"+
		"A\35C\36E\37G I!K\"M#O$Q%S&U\'W(Y)[*]+_,a-c.\3\2\r\6\2\f\f\17\17))^^\6"+
		"\2\f\f\17\17$$^^\n\2$$\61\61^^ddhhppttvv\4\2C\\c|\b\2\60\60\62;C\\^^a"+
		"ac|\3\2\62;\5\2C\\aac|\6\2\62;C\\aac|\7\2\60\60\62;C\\aac|\4\2GGgg\5\2"+
		"\13\f\16\17\"\"\u0145\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\35\3\2\2\2\2\37"+
		"\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3"+
		"\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2"+
		"\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C"+
		"\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2"+
		"\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2"+
		"\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\3e\3\2\2\2\5g\3\2\2\2\7i"+
		"\3\2\2\2\tl\3\2\2\2\13q\3\2\2\2\ry\3\2\2\2\17{\3\2\2\2\21}\3\2\2\2\23"+
		"\177\3\2\2\2\25\u009b\3\2\2\2\27\u009d\3\2\2\2\31\u00a0\3\2\2\2\33\u00a3"+
		"\3\2\2\2\35\u00a9\3\2\2\2\37\u00ac\3\2\2\2!\u00af\3\2\2\2#\u00b3\3\2\2"+
		"\2%\u00b6\3\2\2\2\'\u00b9\3\2\2\2)\u00be\3\2\2\2+\u00c4\3\2\2\2-\u00c6"+
		"\3\2\2\2/\u00c9\3\2\2\2\61\u00cb\3\2\2\2\63\u00ce\3\2\2\2\65\u00d1\3\2"+
		"\2\2\67\u00d4\3\2\2\29\u00d7\3\2\2\2;\u00d9\3\2\2\2=\u00dc\3\2\2\2?\u00e4"+
		"\3\2\2\2A\u00f3\3\2\2\2C\u010a\3\2\2\2E\u010c\3\2\2\2G\u010e\3\2\2\2I"+
		"\u0110\3\2\2\2K\u0112\3\2\2\2M\u0114\3\2\2\2O\u0116\3\2\2\2Q\u0118\3\2"+
		"\2\2S\u011a\3\2\2\2U\u011d\3\2\2\2W\u0120\3\2\2\2Y\u0124\3\2\2\2[\u0126"+
		"\3\2\2\2]\u0128\3\2\2\2_\u012a\3\2\2\2a\u012c\3\2\2\2c\u012f\3\2\2\2e"+
		"f\7]\2\2f\4\3\2\2\2gh\7_\2\2h\6\3\2\2\2ij\7.\2\2j\b\3\2\2\2km\7/\2\2l"+
		"k\3\2\2\2lm\3\2\2\2mn\3\2\2\2no\5=\37\2o\n\3\2\2\2pr\7/\2\2qp\3\2\2\2"+
		"qr\3\2\2\2rs\3\2\2\2st\5=\37\2tu\7\60\2\2uv\5=\37\2v\f\3\2\2\2wz\5\'\24"+
		"\2xz\5)\25\2yw\3\2\2\2yx\3\2\2\2z\16\3\2\2\2{|\5\25\13\2|\20\3\2\2\2}"+
		"~\5\23\n\2~\22\3\2\2\2\177\u0084\7\61\2\2\u0080\u0083\5\27\f\2\u0081\u0083"+
		"\n\2\2\2\u0082\u0080\3\2\2\2\u0082\u0081\3\2\2\2\u0083\u0086\3\2\2\2\u0084"+
		"\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0087\3\2\2\2\u0086\u0084\3\2"+
		"\2\2\u0087\u0088\7\61\2\2\u0088\24\3\2\2\2\u0089\u008e\7)\2\2\u008a\u008d"+
		"\5\27\f\2\u008b\u008d\n\2\2\2\u008c\u008a\3\2\2\2\u008c\u008b\3\2\2\2"+
		"\u008d\u0090\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091"+
		"\3\2\2\2\u0090\u008e\3\2\2\2\u0091\u009c\7)\2\2\u0092\u0097\7$\2\2\u0093"+
		"\u0096\5\27\f\2\u0094\u0096\n\3\2\2\u0095\u0093\3\2\2\2\u0095\u0094\3"+
		"\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2\2\2\u0097\u0098\3\2\2\2\u0098"+
		"\u009a\3\2\2\2\u0099\u0097\3\2\2\2\u009a\u009c\7$\2\2\u009b\u0089\3\2"+
		"\2\2\u009b\u0092\3\2\2\2\u009c\26\3\2\2\2\u009d\u009e\7^\2\2\u009e\u009f"+
		"\13\2\2\2\u009f\30\3\2\2\2\u00a0\u00a1\7^\2\2\u00a1\u00a2\t\4\2\2\u00a2"+
		"\32\3\2\2\2\u00a3\u00a5\t\5\2\2\u00a4\u00a6\t\6\2\2\u00a5\u00a4\3\2\2"+
		"\2\u00a6\u00a7\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\34"+
		"\3\2\2\2\u00a9\u00aa\7(\2\2\u00aa\u00ab\7(\2\2\u00ab\36\3\2\2\2\u00ac"+
		"\u00ad\7~\2\2\u00ad\u00ae\7~\2\2\u00ae \3\2\2\2\u00af\u00b0\7p\2\2\u00b0"+
		"\u00b1\7q\2\2\u00b1\u00b2\7v\2\2\u00b2\"\3\2\2\2\u00b3\u00b4\7k\2\2\u00b4"+
		"\u00b5\7u\2\2\u00b5$\3\2\2\2\u00b6\u00b7\7k\2\2\u00b7\u00b8\7p\2\2\u00b8"+
		"&\3\2\2\2\u00b9\u00ba\7v\2\2\u00ba\u00bb\7t\2\2\u00bb\u00bc\7w\2\2\u00bc"+
		"\u00bd\7g\2\2\u00bd(\3\2\2\2\u00be\u00bf\7h\2\2\u00bf\u00c0\7c\2\2\u00c0"+
		"\u00c1\7n\2\2\u00c1\u00c2\7u\2\2\u00c2\u00c3\7g\2\2\u00c3*\3\2\2\2\u00c4"+
		"\u00c5\7@\2\2\u00c5,\3\2\2\2\u00c6\u00c7\7@\2\2\u00c7\u00c8\7?\2\2\u00c8"+
		".\3\2\2\2\u00c9\u00ca\7>\2\2\u00ca\60\3\2\2\2\u00cb\u00cc\7>\2\2\u00cc"+
		"\u00cd\7?\2\2\u00cd\62\3\2\2\2\u00ce\u00cf\7?\2\2\u00cf\u00d0\7?\2\2\u00d0"+
		"\64\3\2\2\2\u00d1\u00d2\7#\2\2\u00d2\u00d3\7?\2\2\u00d3\66\3\2\2\2\u00d4"+
		"\u00d5\7?\2\2\u00d5\u00d6\7\u0080\2\2\u00d68\3\2\2\2\u00d7\u00d8\7*\2"+
		"\2\u00d8:\3\2\2\2\u00d9\u00da\7+\2\2\u00da<\3\2\2\2\u00db\u00dd\7/\2\2"+
		"\u00dc\u00db\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00df\3\2\2\2\u00de\u00e0"+
		"\t\7\2\2\u00df\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00df\3\2\2\2\u00e1"+
		"\u00e2\3\2\2\2\u00e2>\3\2\2\2\u00e3\u00e5\7/\2\2\u00e4\u00e3\3\2\2\2\u00e4"+
		"\u00e5\3\2\2\2\u00e5\u00e7\3\2\2\2\u00e6\u00e8\t\7\2\2\u00e7\u00e6\3\2"+
		"\2\2\u00e8\u00e9\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea"+
		"\u00f1\3\2\2\2\u00eb\u00ed\7\60\2\2\u00ec\u00ee\t\7\2\2\u00ed\u00ec\3"+
		"\2\2\2\u00ee\u00ef\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0"+
		"\u00f2\3\2\2\2\u00f1\u00eb\3\2\2\2\u00f1\u00f2\3\2\2\2\u00f2@\3\2\2\2"+
		"\u00f3\u00f7\t\b\2\2\u00f4\u00f6\t\t\2\2\u00f5\u00f4\3\2\2\2\u00f6\u00f9"+
		"\3\2\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2\u00f8B\3\2\2\2\u00f9"+
		"\u00f7\3\2\2\2\u00fa\u00fb\7&\2\2\u00fb\u00ff\t\b\2\2\u00fc\u00fe\t\n"+
		"\2\2\u00fd\u00fc\3\2\2\2\u00fe\u0101\3\2\2\2\u00ff\u00fd\3\2\2\2\u00ff"+
		"\u0100\3\2\2\2\u0100\u010b\3\2\2\2\u0101\u00ff\3\2\2\2\u0102\u0103\7B"+
		"\2\2\u0103\u0107\t\b\2\2\u0104\u0106\t\n\2\2\u0105\u0104\3\2\2\2\u0106"+
		"\u0109\3\2\2\2\u0107\u0105\3\2\2\2\u0107\u0108\3\2\2\2\u0108\u010b\3\2"+
		"\2\2\u0109\u0107\3\2\2\2\u010a\u00fa\3\2\2\2\u010a\u0102\3\2\2\2\u010b"+
		"D\3\2\2\2\u010c\u010d\7-\2\2\u010dF\3\2\2\2\u010e\u010f\7/\2\2\u010fH"+
		"\3\2\2\2\u0110\u0111\7,\2\2\u0111J\3\2\2\2\u0112\u0113\7\61\2\2\u0113"+
		"L\3\2\2\2\u0114\u0115\7\'\2\2\u0115N\3\2\2\2\u0116\u0117\7\60\2\2\u0117"+
		"P\3\2\2\2\u0118\u0119\t\13\2\2\u0119R\3\2\2\2\u011a\u011b\7>\2\2\u011b"+
		"\u011c\7>\2\2\u011cT\3\2\2\2\u011d\u011e\7@\2\2\u011e\u011f\7@\2\2\u011f"+
		"V\3\2\2\2\u0120\u0121\7@\2\2\u0121\u0122\7@\2\2\u0122\u0123\7@\2\2\u0123"+
		"X\3\2\2\2\u0124\u0125\7(\2\2\u0125Z\3\2\2\2\u0126\u0127\7`\2\2\u0127\\"+
		"\3\2\2\2\u0128\u0129\7~\2\2\u0129^\3\2\2\2\u012a\u012b\7\f\2\2\u012b`"+
		"\3\2\2\2\u012c\u012d\4\62;\2\u012db\3\2\2\2\u012e\u0130\t\f\2\2\u012f"+
		"\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u012f\3\2\2\2\u0131\u0132\3\2"+
		"\2\2\u0132\u0133\3\2\2\2\u0133\u0134\b\62\2\2\u0134d\3\2\2\2\31\2lqy\u0082"+
		"\u0084\u008c\u008e\u0095\u0097\u009b\u00a7\u00dc\u00e1\u00e4\u00e9\u00ef"+
		"\u00f1\u00f7\u00ff\u0107\u010a\u0131\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}