import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.swizzle.confluence.Page;

/**
 * Performs basic syntax checks on Confluence wiki code
 * 
 * @author Anna Chu
 */
public class SyntaxChecker {
	// indicator of check results
	public static final int TYPE_VALID = 0; // No errors or warnings
	public static final int TYPE_ERROR = 1;
	public static final int TYPE_WARNING = 2;
	public static final String DELIMITER = "<li>"; // Error message delimiter

	// popular macro tags that must be paired (with itself) to be complete
	private static final List<String> POPULAR_PAIRED_MACROS = Arrays.asList(
			"align", "center", "chart", "cloak", "code", "color", "column",
			"composition-setup", "excerpt", "float", "footnote",
			"google-calendar", "info", "multi-excerpt", "multi-excerpt-append",
			"noformat", "note", "panel", "quote", "section", "span",
			"table-plus", "text-extractor", "tip", "vote", "warning");

	// regex to pull macro type from wiki code
	// <b>Translation</b>: retrieve word character (i.e. letter, number or
	// underscore) or dash between curly braces or between { and : (for
	// start/single macro tags)
	private static final String MACRO_REGEX = "\\{([\\w-]*?)[:|\\}]";
	private static final Pattern MACRO_PATTERN = Pattern.compile(MACRO_REGEX,
			Pattern.CASE_INSENSITIVE);

	// ordered list of macros in wiki code
	private List<String> parsedMacros;
	// ordered list of multi-excerpt names in wiki code
	private List<String> parsedMultiExcerpts;
	// Key: macro name, Value: occurrences of macro
	private Map<String, Integer> macroCounts;
	// resulting error or warning message
	private String message;
	// resulting error or warning code
	private int resultType;
	// wiki code accumulated into a string
	private StringBuffer wikiCode;

	/**
	 * Constructor. Runs all checks.
	 * 
	 * @param wikiPage
	 *            Wiki page with contents for syntax checking
	 */
	public SyntaxChecker(Page wikiPage) {
		// Set up page contents into buffer
		BufferedReader wikiReader = new BufferedReader(new StringReader(
				wikiPage.getContent()));

		try {

			// Run error and warning checks
			runChecks(wikiReader);
			// Close buffer stream
			wikiReader.close();

		} catch (IOException e) {
			System.out.println("Unable to read wiki page content");
			e.printStackTrace();
		}
	}

	/**
	 * Checks if wiki code is valid, i.e. has no detectable syntactical errors
	 * or warnings
	 * 
	 * @return true if no syntax errors are detected; otherwise, false
	 */
	public boolean isValid() {
		return resultType == TYPE_VALID;
	}

	/**
	 * Returns error or warning message
	 * 
	 * @return String containing error or warning message. If no errors/warnings
	 *         exist, return null
	 */
	public String getResultMessage() {
		// Append blanket message to specific message
		if (resultType == TYPE_ERROR) {
			return message
					+ "<br>Please modify your wiki code and try saving again.";
		} else if (resultType == TYPE_WARNING) {
			return message
					+ "<br>Would you like to apply an automatic resolution?";
		} else {
			return null;
		}
	}

	/**
	 * Returns result type:
	 * <ul>
	 * <li>0 = valid (i.e. no errors or warnings)
	 * <li>1 = error
	 * <li>2 = warning
	 * </ul>
	 * 
	 * @return
	 */
	public int getResultType() {
		return resultType;
	}

	/**
	 * Returns the wiki code that was buffered in as a String
	 * 
	 * @return wiki code as string. If code has not been buffered in yet, null
	 *         is returned
	 */
	public StringBuffer getWikiCode() {
		return wikiCode;
	}

	/**
	 * Runs error and warning checks
	 * 
	 * @param wikiReader
	 *            Buffered reader of wiki code for syntax checking
	 * @throws IOException
	 */
	private void runChecks(BufferedReader wikiReader) throws IOException {
		// Initialize values for error/warning checks
		parsedMacros = new Vector<String>();
		parsedMultiExcerpts = new Vector<String>();
		wikiCode = new StringBuffer();
		message = "";
		resultType = TYPE_VALID;
		Matcher macroMatcher, multiExcerptMatcher;

		// Parse macros and multi-excerpt names from wiki code
		String line = wikiReader.readLine();
		while (line != null) {
			// Apply matchers to line
			macroMatcher = MACRO_PATTERN.matcher(line);
			multiExcerptMatcher = ExcerptUtils.MULTI_EXCERPT_PATTERN
					.matcher(line);

			// Search for matches to regexes and parse content if appropriate
			while (macroMatcher.find()) {
				parsedMacros.add(macroMatcher.group(1).trim().toLowerCase());
			}

			while (multiExcerptMatcher.find()) {
				parsedMultiExcerpts
						.add(StringUtils.equalsIgnoreCase(
								multiExcerptMatcher.group(0), "{multi-excerpt}") ? multiExcerptMatcher
								.group(2) : multiExcerptMatcher.group(2).trim()
								.toLowerCase());
			}

			// Simultaneously store buffered read in
			wikiCode.append(line);
			// TODO Remove next line when complete
			wikiCode.append("\n");

			line = wikiReader.readLine();
		}

		// Run error checks - stop checking if any error exists
		if (hasIncompleteMacros() || hasInterleavedMacros()
				|| hasMultipleExcerpts() || hasRepeatMultiExcerpts()
				|| hasIncompleteMultiExcerpts()) {
			return;
		}

		// Run warning checks - stop checking if any warning exists
		if (hasNestedExcerpts()) {
			return;
		}
	}

	/**
	 * Checks if popular paired macro tags are incomplete/unpaired. For example:
	 * {multi-excerpt} wraps a block of excerpted text with the same tag.
	 * 
	 * @category error-checker
	 * @return true if incomplete macros exist; otherwise false
	 */
	private boolean hasIncompleteMacros() {
		// Key: macro, Value: occurrences of macro
		macroCounts = new HashMap<String, Integer>();
		// Names of unpaired macros
		Vector<String> unpaired = new Vector<String>();

		// Tally the occurrences of each tag
		for (String macro : parsedMacros) {
			// 'value' stored but never read; required as HashMap.put() returns
			// a value
			@SuppressWarnings("unused")
			Integer value = macroCounts.containsKey(macro) ? macroCounts.put(
					macro, (Integer) (macroCounts.get(macro).intValue() + 1))
					: macroCounts.put(macro, new Integer(1));
		}

		// Check popular macros against extracted macros
		for (String popularMacro : POPULAR_PAIRED_MACROS) {
			if (macroCounts.containsKey(popularMacro)
					&& macroCounts.get(popularMacro).intValue() % 2 > 0) {
				// A macro that should have a match is single
				unpaired.add(popularMacro);
			}
		}

		if (unpaired.isEmpty()) {
			// No errors
			return false;
		} else {
			// Create error message
			message = "The following macro(s) are missing start or closing tags:<ul><li>"
					+ StringUtils.join(unpaired, DELIMITER) + "</ul>";
			resultType = TYPE_ERROR;
			return true;
		}
	}

	/**
	 * Checks if any macro tags are interleaved. For example: <br>
	 * {macro_1} <br>
	 * {macro_2}<br>
	 * {macro_1}<br>
	 * {macro_2}<br>
	 * is an error and would return true.
	 * 
	 * @category error-checker
	 * @return true if interleaved macros exist; otherwise false
	 */
	private boolean hasInterleavedMacros() {
		// Stack of macros
		Stack<String> interlaced = new Stack<String>();

		// Iterate through all macros in wiki page, pushing into stack if it is
		// meant to be paired, popping if a pair is made. Any remaining macros
		// will be considered interlaced
		for (String macro : parsedMacros) {
			if (POPULAR_PAIRED_MACROS.contains(macro)) {
				if (interlaced.isEmpty()) {
					// First element in stack
					interlaced.push(macro);
				} else if (interlaced.peek().equals(macro)) {
					// Current macro matches macro at top of stack. Pop the top
					// of the stack
					interlaced.pop();
				} else {
					// No match to the top of stack. Push this macro in.
					interlaced.push(macro);
				}
			}
		}

		if (interlaced.isEmpty()) {
			// If stack is empty, no interlaced macros exist
			return false;
		} else {
			// Compress into a unique list of interleaving macros
			ArrayList<String> unique = new ArrayList<String>(
					new HashSet<String>(interlaced));
			// Create error message
			message = "The following macro(s) are interlaced:<ul><li>"
					+ StringUtils.join(unique, DELIMITER) + "</ul>";
			resultType = TYPE_ERROR;
			return true;
		}
	}

	/**
	 * A wiki page cannot have more than one {excerpt} macro. Checks if this
	 * rule is violated.
	 * 
	 * @return true if {excerpt} is used more than once; otherwise false
	 */
	private boolean hasMultipleExcerpts() {
		// Convert Integer count to int. If null, set count as 0
		int excerptCount = macroCounts.get("excerpt") == null ? 0 : macroCounts
				.get("excerpt").intValue();
		if (excerptCount > 2) {
			// Create error message
			message = "The {excerpt} macro can only be used once.";
			resultType = TYPE_ERROR;
			return true;
		} else if (excerptCount == 1) {
			// Single excerpt tag exists - making it incomplete
			message = "The {excerpt} macro is missing a tag.";
			resultType = TYPE_ERROR;
			return true;
		} else {
			// One or zero sets of {excerpt} macros exist
			return false;
		}

	}

	/**
	 * A multi-excerpt name cannot be used more than once. Checks if this rule
	 * is violated
	 * 
	 * @return true if a multi-excerpt name is used more than once; otherwise
	 *         false
	 */
	private boolean hasRepeatMultiExcerpts() {
		Vector<String> repeats = new Vector<String>();

		// Discover all repeating excerpt names. A name is repeated if its first
		// and last occurrence indices differ
		for (String name : parsedMultiExcerpts) {
			if (name != null
					&& parsedMultiExcerpts.indexOf(name) != parsedMultiExcerpts
							.lastIndexOf(name)) {
				// Repeat discovered; add to list
				repeats.add(name);
			}
		}

		if (repeats.isEmpty()) {
			// No repeats exist
			return false;
		} else {
			// Compress into a unique list of interleaving macros
			ArrayList<String> unique = new ArrayList<String>(
					new HashSet<String>(repeats));
			// Create error message
			message = "The following {multi-excerpt} names have been used more than once:<ul><li>"
					+ StringUtils.join(unique, DELIMITER) + "</ul>";
			resultType = TYPE_ERROR;
			return true;
		}
	}

	/**
	 * Checks that there is an even number of multi-excerpt start and close
	 * tags. The hasIncompleteMacros() is not rigorous enough to detect this.
	 * 
	 * @return true if the number of start and close {multi-excerpt} tags do not
	 *         match up; otherwise false
	 */
	private boolean hasIncompleteMultiExcerpts() {
		// Stack of unmatched multi-excerpt names
		Stack<String> unmatched = new Stack<String>();

		// Iterate through all multi-excerpts, pushing into stack if it is a
		// part of a pair, popping if a pair is complete. Any remaining names
		// will be considered unmatched/incomplete
		for (String name : parsedMultiExcerpts) {
			if (unmatched.isEmpty()) {
				// First element in stack
				unmatched.push(name);
			} else if (name == null && unmatched.peek() != null) {
				// Close tag matches start tag at top of stack. Pop the top
				// of the stack
				unmatched.pop();
			} else {
				// No match to the top of stack. Push this tag in.
				unmatched.push(name);
			}
		}

		if (unmatched.isEmpty()) {
			// If stack is empty, no unmatched tags exist
			return false;
		} else {
			// Compress into a unique list of interleaving macros
			ArrayList<String> unique = new ArrayList<String>(
					new HashSet<String>(unmatched));
			// Find out if one of these unmatched tags is a close tag
			boolean hasUnmatchedCloseTag = unique.remove(null);

			// Create error messages
			if (!unique.isEmpty()) {
				// List all unclosed tags
				message = "The following multi-excerpt(s) are missing their closing tags:<ul><li>"
						+ StringUtils.join(unique, DELIMITER) + "</ul>";
			}
			// Append to error message if there are extra closing tags
			if (hasUnmatchedCloseTag) {
				message += "One or more multi-excerpts are missing start tags, e.g. {multi-excerpt:name=...}.";
			}

			resultType = TYPE_ERROR;
			return true;
		}
	}

	/**
	 * Checks if excerpts are nested. For example:<br>
	 * {multi-excerpt:name=a}<br>
	 * {multi-excerpt:name=b}<br>
	 * {multi-excerpt}<br>
	 * {multi-excerpt}<br>
	 * would produce a warning.
	 * 
	 * @category warning-checker
	 * @return true if nested excerpts exist; otherwise false
	 */
	private boolean hasNestedExcerpts() {
		Vector<String> nesters = new Vector<String>();
		String firstStart = null; // First start tag in a series of start tags

		for (int i = 0; i < parsedMultiExcerpts.size(); i++) {

			String name = parsedMultiExcerpts.get(i); // current tag

			if (name != null && firstStart == null) {
				// First start tag in a series
				firstStart = name;
			} else if (name == null) {
				if (firstStart != parsedMultiExcerpts.get(i - 1)) {
					// firstStart nests other multi-excerpts
					nesters.add(firstStart);
				}
				firstStart = null; // reset sequence
			}
		}

		if (nesters.isEmpty()) {
			// No multi-excerpts are nested
			return false;
		} else {
			// Create warning message
			message = "The following multi-excerpts nest other multi-excerpts:<ul><li>"
					+ StringUtils.join(nesters, DELIMITER) + "</ul>";
			resultType = TYPE_WARNING;
			return true;
		}
	}
}