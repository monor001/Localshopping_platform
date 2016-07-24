package localshoppingplatform.server.crawlertextpreprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

public class TextFilter {
	final static String regex_priceln = "(\\s?|\\n?)[0-9A-z\\s\\.\\-ÄäöÖÜüß&%´/,\"“„\\(\\)]+(\\s| )?[\\d, \\d]+( )?(€|$)";

	// Formulate product's price into a new line

	public String extractProductsList(String productText) {
		String productExtractor = "";
		String[] productExtractorarr;
		StringBuilder sb_exProlist;
		Pattern pattern = Pattern.compile(regex_priceln, Pattern.DOTALL);
		Matcher match;
		productExtractorarr = productText.split("\n");
		int countProducts = 0;
		for (int i = 0; i < productExtractorarr.length; i++) {
			// remove it later
			match = pattern.matcher(productExtractorarr[i]);
			if (match.find()) {
				sb_exProlist = new StringBuilder(productExtractorarr[i]);
				// Check if there is no space between price and product title
				int charIndicator = 0;
				charIndicator = sb_exProlist.indexOf("€");
				if (charIndicator != -1 && charIndicator != 0) {
					// Check if there is no new line after €
					if (charIndicator + 1 != sb_exProlist.length()) {
						if (sb_exProlist.charAt(charIndicator + 1) != '\n') {
							sb_exProlist.insert(charIndicator + 1, '\n');
						}
					}
					// Check if there is no digits before €
					if (sb_exProlist.charAt(charIndicator - 3) == ','
							|| sb_exProlist.charAt(charIndicator - 4) == ',') {
						char a = ' ', b = ' ', c = ' ', d = ' ';
						boolean flagA = false, flagB = false, flagC = false, flagD = false;
						// Work around to solve the problem of length out-bound
						if (charIndicator - 5 > -1) {
							a = sb_exProlist.charAt(charIndicator - 5);
						}
						if (charIndicator - 6 > -1) {
							b = sb_exProlist.charAt(charIndicator - 6);
						}
						if (charIndicator - 7 > -1) {
							c = sb_exProlist.charAt(charIndicator - 7);
						}
						if (charIndicator - 8 > -1) {
							d = sb_exProlist.charAt(charIndicator - 8);
						}
						for (int j = 0; j < 10; j++) {
							String jValueworkaround = String.valueOf(j);
							StringBuilder jValueworkaroundsb = new StringBuilder(
									jValueworkaround);
							if (a == jValueworkaroundsb.charAt(0) && a != ' ') {
								// Check if there are a digit at this position
								flagA = true;
							}
							if (b == jValueworkaroundsb.charAt(0) && b != ' ') {
								// Check if there are a digit at this position
								flagB = true;
							}
							if (c == jValueworkaroundsb.charAt(0) && c != ' ') {
								// Check if there are a digit at this position
								flagC = true;
							}
							if (d == jValueworkaroundsb.charAt(0) && d != ' ') {
								// Check if there are a digit at this position
								flagD = true;
							}
						}
						if (flagA == false) {
							// Check if the price interpreted alone in the line
							if (a != '\n' && charIndicator - 5 > -1) {
								sb_exProlist.insert(charIndicator - 4,
										'\n');
								productExtractorarr[i] = sb_exProlist
										.toString();
								countProducts = countProducts + 1;
								productExtractor = productExtractor
										+ productExtractorarr[i];
							} else {
								productExtractorarr[i] = sb_exProlist
										.toString();
								productExtractor = productExtractor
										+ productExtractorarr[i - 1] + "\n"
										+ productExtractorarr[i] + "\n";
							}
						} else if (flagB == false) {
							// Check if the price interpreted alone in the line
							if (b != '\n' && charIndicator - 6 > -1) {
								sb_exProlist.insert(charIndicator - 5,
										'\n');
								productExtractorarr[i] = sb_exProlist
										.toString();
								countProducts = countProducts + 1;
								productExtractor = productExtractor
										+ productExtractorarr[i];
							} else {
								productExtractorarr[i] = sb_exProlist
										.toString();
								productExtractor = productExtractor
										+ productExtractorarr[i - 1] + "\n"
										+ productExtractorarr[i] + "\n";
							}
						} else if (flagC == false) {
							// Check if the price interpreted alone in the line
							if (c != '\n' && charIndicator - 7 > -1) {
								sb_exProlist.insert(charIndicator - 6,
										'\n');
								productExtractorarr[i] = sb_exProlist
										.toString();
								countProducts = countProducts + 1;
								productExtractor = productExtractor
										+ productExtractorarr[i];
							} else {
								productExtractorarr[i] = sb_exProlist
										.toString();
								productExtractor = productExtractor
										+ productExtractorarr[i - 1] + "\n"
										+ productExtractorarr[i] + "\n";
							}
						} else if (flagD == false) {
							// Check if the price interpreted alone in the line
							if (d != '\n' && charIndicator - 8 > -1) {
								sb_exProlist.insert(charIndicator - 7,
										'\n');
								productExtractorarr[i] = sb_exProlist
										.toString();
								countProducts = countProducts + 1;
								productExtractor = productExtractor
										+ productExtractorarr[i];
							} else {
								productExtractorarr[i] = sb_exProlist
										.toString();
								productExtractor = productExtractor
										+ productExtractorarr[i - 1] + "\n"
										+ productExtractorarr[i] + "\n";
							}
						}
					} else {
						List<String> list = new ArrayList<String>(
								Arrays.asList(productExtractorarr));
						list.remove(productExtractorarr[i]);
						productExtractorarr = list.toArray(new String[0]);
					}
				}
			}
		}
		// System.out.print(productExtractor);
		return productExtractor;
	}

	public static void main(String[] args) {
	}

}
