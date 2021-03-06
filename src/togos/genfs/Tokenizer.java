package togos.genfs;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer
{
	static class TokenizationException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
		String text;
		int pos;
		public TokenizationException(String msg, String text, int pos) {
			super(msg);
			this.text = text;
			this.pos = pos;
		}
		public String getMessage() {
			return super.getMessage() + " at " + pos + " in:\n"+text;
		}
	}
	
	public static String[] tokenize( String line ) {
		List tokens = new ArrayList();
		String token = null;
		boolean quoteMode = false;
		boolean bsMode = false;
		
		for( int i = 0; i<line.length(); ++i ) {
			char c = line.charAt(i);
			
			if( c == '"' && !bsMode ) {
				if( quoteMode ) {
					quoteMode = false;
					continue;
				} else if( token == null ) {
					token = "";
					quoteMode = true;
					continue;
				} else {
					throw new TokenizationException("Found open quote after token started", line, i);
				}
			} else if( c == '\\' && !bsMode ) {
				if( !quoteMode ) {
					throw new TokenizationException("Misplaced backslash (outside quoted token)", line, i);
				} else {
					bsMode = true;
					continue;
				}
			} else if( !quoteMode && (c == ' ' || c == '\r' || c == '\n' || c == '\t') ) {
				if( token != null ) {
					tokens.add(token);
					token = null;
				}
				continue;
			}
			
			if( bsMode ) {
				switch( c ) {
				case( 'r' ): c = '\r'; break;
				case( 'n' ): c = '\n'; break;
				case( 't' ): c = '\t'; break;
				case( '"' ): c = '"'; break;
				case( '\\' ): c = '\\'; break;
				default:
					throw new TokenizationException("Invalid backslash character '"+c+"'", line, i);
				}
				bsMode = false;
			}
			token = ((token == null) ? "" : token) + c;
		}
		if( token != null ) {
			tokens.add(token);
			token = null;
		}
		return (String[])tokens.toArray(new String[tokens.size()]);
	}
	
	public static String detokenize( String[] args ) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for( int i=0; i<args.length; ++i ) {
			if( !first ) sb.append(' ');
			boolean needQuotes = false;
			for( int j=0; j<args[i].length(); ++j ) {
				char c = args[i].charAt(j);
				if( (c >= 'a' && c <= 'z') ||
				    (c >= 'A' && c <= 'Z') ||
				    (c >= '0' && c <= '9') ||
				    c == '_' || c == '-' || c == '.'
				) {
					
				} else {
					needQuotes = true;
					break;
				}
			}
			if( needQuotes ) {
				sb.append('"');
				for( int j=0; j<args[i].length(); ++j ) {
					char c = args[i].charAt(j);
					switch( c ) {
					case('\\'): sb.append("\\\\"); break;
					case('"' ): sb.append("\\\""); break;
					case('\r'): sb.append("\\r");   break;
					case('\n'): sb.append("\\n");   break;
					case('\t'): sb.append("\\t");   break;
					default:    sb.append(c);      break;
					}
				}
				sb.append('"');
			} else {
				sb.append(args[i]);
			}
			first = false;
		}
		return sb.toString();
	}
}
