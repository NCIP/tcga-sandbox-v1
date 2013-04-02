import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.ConfluenceException;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.SearchResult;
import org.codehaus.swizzle.confluence.SwizzleException;

/**
 * Stores and retrieves {excerpt} and {multi-excerpt} wiki code so that it can
 * be rendered properly in Confluence. This class also supports the nesting of
 * {multi-excerpts} and provides an interface by translating easy-to-edit code
 * and the actual wiki code.
 * 
 * @author Anna Chu
 */
public class ExcerptUtils {
	// Maximum number of results returned for a wiki search
	private static final int MAX_RESULTS = 10000; // TODO - transfer out?
	// Regex for a legal Confluence space/page name
	// TODO - transfer out?
	private static final String CONFLUENCE_NAME_REGEX = "[\\w\\s-\\!\\$\\%\\&\\*\\(\\),]+";
	// Regex for a legal excerpt name
	private static final String EXCERPT_NAME_REGEX = "\\w+[\\w\\s]*";
	// Regex for extracting space and page names from a {multi-excerpt-include}
	private static final String MULTI_EXCERPT_INCLUDE_REGEX = "\\{multi-excerpt-include:("
			+ CONFLUENCE_NAME_REGEX
			+ "):("
			+ CONFLUENCE_NAME_REGEX
			+ ")\\|\\s*name\\s*=\\s*("
			+ EXCERPT_NAME_REGEX
			+ ")+[\\s\\w\\|=]*\\}";
	public static final Pattern MULTI_EXCERPT_INCLUDE_PATTERN = Pattern
			.compile(MULTI_EXCERPT_INCLUDE_REGEX, Pattern.CASE_INSENSITIVE);
	// Regex for extracting multi-excerpt macros and their names
	// <br><b>Translation</b>: retrieve multi-excerpt name (along with the
	// extraneous information before the name) that trails '{multi-excerpt',
	// followed by a closed curly brace or colon
	private static final String MULTI_EXCERPT_REGEX = "\\{multi-excerpt:*\\s*(name\\s*=\\s*([\\w\\s]+))*[\\s\\w\\|=]*\\}";
	public static final Pattern MULTI_EXCERPT_PATTERN = Pattern.compile(
			MULTI_EXCERPT_REGEX, Pattern.CASE_INSENSITIVE);
	// Regex for extracting excerpt macro attributes from the start tag
	// <br><b>Translation</b>: retrieve excerpt along with any attributes
	private static final String EXCERPT_REGEX = "\\{excerpt:*\\s*([\\s\\w\\|=]*)\\}";
	private static final Pattern EXCERPT_PATTERN = Pattern.compile(
			EXCERPT_REGEX, Pattern.CASE_INSENSITIVE);

	/**
	 * Checks whether {excerpt} is used in the wiki code
	 * 
	 * @param wikiCode
	 *            Confluence wiki code
	 * @return boolean
	 */
	static boolean containsExcerpt(String wikiCode) {
		return StringUtils.containsIgnoreCase(wikiCode, "{excerpt}");
	}

	/**
	 * Checks whether {multi-excerpt} is used in the wiki code
	 * 
	 * @param wikiCode
	 *            Confluence wiki code
	 * @return boolean
	 */
	static boolean containsMultiExcerpt(String wikiCode) {
		return StringUtils.containsIgnoreCase(wikiCode, "{multi-excerpt}");
	}

	/**
	 * Checks if a name exists for a multi-excerpt
	 * 
	 * @param wikiCode
	 *            Confluence wiki code
	 * @param excerptName
	 *            Name to check against multi-excerpt names
	 * @return boolean
	 */
	static boolean isExistingName(String wikiCode, String excerptName) {
		// Regex to seek multi-excerpt with specific name
		final String nameRegex = "\\{multi-excerpt:\\s*name\\s*=\\s*"
				+ excerptName + "\\s*(\\}|\\|[\\w\\s\\|=]*)";
		// Pattern containing name regex
		final Pattern namePattern = Pattern.compile(nameRegex,
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = namePattern.matcher(wikiCode);
		return matcher.find();
	}

	/**
	 * Checks if a provided name is a valid excerpt name. A valid name is
	 * alphanumeric and can contain underscores and spaces
	 * 
	 * @param name
	 *            Excerpt name
	 * @return boolean
	 */
	static boolean isValidExcerptName(String name) {
		return name.matches(EXCERPT_NAME_REGEX);
	}

	/**
	 * Converts the excerpt macro to a multi-excerpt macro and labels it with
	 * the provided name
	 * 
	 * @param wikiCode
	 *            Confluence wiki code for which {excerpt}s will be relabelled
	 *            as {multi-excerpt}s
	 * @param name
	 *            Name to associate with the converted excerpt
	 * @return A modified version of the wiki code, with {excerpt} replaced by
	 *         {multi-excerpt}
	 */
	static void convertExcerptToMultiExcerpt(StringBuffer wikiCode, String name) {

		// TODO - Convert {excerpt-include}s

		Matcher matcher = EXCERPT_PATTERN.matcher(wikiCode);

		if (matcher.find()) {
			// Search first occurrence of tag and split attributes
			String[] attributes = matcher.group(1).split("\\|");
			String multiExcerpt = "{multi-excerpt:name="
					+ name
					+ (attributes.length > 0 ? "|"
							+ StringUtils.join(attributes, "|") : "") + "}";
			int excerptIdx = matcher.end();
			// Replace start tag of {excerpt} with a named {multi-excerpt}.
			// Transfer any attributes that there may be
			wikiCode.replace(matcher.start(), excerptIdx, multiExcerpt);

			// Replace ending tag of {excerpt} with {multi-excerpt}
			String excerpt = "{excerpt}";
			excerptIdx = StringUtils.indexOfIgnoreCase(wikiCode, excerpt,
					excerptIdx);
			wikiCode.replace(excerptIdx, excerptIdx + excerpt.length(),
					"{multi-excerpt}");

		}
	}

	/**
	 * Takes wiki code and stores any {multi-excerpt}s into the provided excerpt
	 * repository page. The original {multi-excerpt}s are then replaced by calls
	 * to the corresponding code in the repository using
	 * {multi-excerpt-include}.
	 * 
	 * This makes excerpts nestable and renderable at all times. Call function
	 * before saving code to page.
	 * 
	 * Function also converts user-created {multi-excerpt-include}s to reference
	 * the corresponding {multi-excerpt} in the repository
	 * 
	 * @param wikiCode
	 *            Confluence wiki code containing {multi-excerpt}s for
	 *            extraction to the excerpt repository
	 * @param pageID
	 *            ID of the wiki page that wikiCode is derived. This will be
	 *            used to identify excerpts in the repository associated with
	 *            this page
	 * @param repositoryPage
	 *            page which stores extracted {multi-excerpt}s
	 * @param rpc
	 *            Remote Procedure Call connection
	 * @return transformed wiki code
	 * @throws SwizzleException
	 * @throws ConfluenceException
	 * @throws IOException
	 */
	static String condenseCode(StringBuffer wikiCode, String contentPageId,
			Page repositoryPage, Confluence rpc) throws ConfluenceException,
			SwizzleException {
		// wiki space of repository
		String spaceName = repositoryPage.getSpace();
		// page name of repository page
		String repositoryName = repositoryPage.getTitle();

		// Stores {multi-excerpt} code blocks as they are being built
		Stack<StringBuffer> buildStack = new Stack<StringBuffer>();
		// Stores {multi-excerpt} code blocks in the order they should be stored
		// in the repository
		Stack<StringBuffer> repositoryStack = new Stack<StringBuffer>();
		// Current excerpt being built
		StringBuffer currExcerpt = new StringBuffer();
		// Indices of current and last {multi-excerpt} events
		int currIdx = 0;
		int lastIdx = 0;

		// [A] Update user-created {multi-excerpt-include}s to reference the
		// repository
		Matcher matcher = MULTI_EXCERPT_INCLUDE_PATTERN.matcher(wikiCode);
		while (matcher.find()) {
			// Get the pageID of the referenced page
			String refPageId = rpc.getPage(matcher.group(1).trim(),
					matcher.group(2).trim()).getId();
			// Build {multi-excerpt-include} code that references repository
			// TODO - Allow for other repositories?
			String includeCode = "{multi-excerpt-include:" + spaceName + ":"
					+ repositoryName + "|name=" + refPageId + "_"
					+ matcher.group(3).trim() + "|nopanel=true}";
			// Replace user-created {multi-excerpt-include}
			wikiCode.replace(matcher.start(), matcher.end(), includeCode);
		}

		matcher = MULTI_EXCERPT_PATTERN.matcher(wikiCode);

		while (matcher.find()) {
			// [B] Create part of excerpt block by copying over the original
			// code from the last multi-excerpt event (or if none, beginning of
			// page) up to this point (the current multi-excerpt event)

			// Get index of the multi-excerpt
			currIdx = matcher.start();
			// Copy over the code
			currExcerpt.append(wikiCode.substring(lastIdx, currIdx));
			// Set end of multi-excerpt to be new last index
			lastIdx = matcher.end();

			if (StringUtils.equalsIgnoreCase(matcher.group(0),
					"{multi-excerpt}")) {
				// [C1: Multi-excerpt end tag]
				// Finish off the current excerpt block before transferring to
				// repositoryStack. Then bring back the last excerpt block and
				// continue building on it.

				// Finish off multi-excerpt block
				currExcerpt.append("{multi-excerpt}");
				// This excerpt is done and ready for export. Push into
				// repository stack
				repositoryStack.push(currExcerpt);
				// Continue building previous excerpt block. Pop from build
				// stack
				currExcerpt = buildStack.pop();

			} else {
				// [C2: Multi-excerpt start tag]
				// Replace the original multi-excerpt with a multi-excerpt
				// call. This is done to ensure proper rendering of contents
				// (due to unexpected Confluence behaviour). Then begin the next
				// excerpt block

				// Extract multi-excerpt name
				String excerptName = matcher.group(2).trim();
				// Append the multi-excerpt call (with no panels)
				currExcerpt.append("{multi-excerpt-include:" + spaceName + ":"
						+ repositoryName + "|name=" + contentPageId + "_"
						+ excerptName + "|nopanel=true}");
				// Push current excerpt into stack
				buildStack.push(currExcerpt);
				// Begin the new excerpt block, which begins with the
				// multi-excerpt
				currExcerpt = new StringBuffer("{multi-excerpt:name="
						+ contentPageId + "_" + excerptName + "}");

			}

		}

		// [D] Export stack of excerpts to repository
		saveToRepository(repositoryStack, contentPageId, repositoryPage, rpc);

		// [E] Return condensed code for edited wiki page. Add in the last
		// portion of the page that has not yet been read
		return currExcerpt.append(wikiCode.substring(lastIdx)).toString();
	}

	/**
	 * Takes condensed wiki code and replaces it with the original code from the
	 * repository page (as indicated in the {multi-excerpt-include} tag).
	 * 
	 * This may not return functional code, but is user-editable. Call function
	 * before displaying code from page.
	 * 
	 * @param wikiCode
	 *            Confluence wiki code containing {multi-excerpt-include}s * @param
	 *            pageID ID of the wiki page that wikiCode is being restored to
	 *            with excerpts
	 * @param repositoryPage
	 *            page which stores extracted {multi-excerpt}s
	 * @param rpc
	 *            Remote Procedure Call connection
	 * @return transformed wiki code
	 * @throws SwizzleException 
	 * @throws ConfluenceException 
	 * @throws IOException
	 */
	static String expandCode(StringBuffer wikiCode, String contentPageId,
			Page repositoryPage, Confluence rpc) throws ConfluenceException, SwizzleException {
		// [A] Retrieve all excerpts from the repository related to this wiki
		// page
		Pattern pageExcerptPattern = getRepositoryExcerptPattern(contentPageId);
		String repositoryContent = repositoryPage.getContent();
		HashMap<String, String> repositoryExcerpts = new HashMap<String, String>();
		Matcher matcher = pageExcerptPattern.matcher(repositoryContent);
		while (matcher.find()) {
			// Find index of when excerpt content ends
			int endIdx = StringUtils.indexOfIgnoreCase(repositoryContent,
					"{multi-excerpt}", matcher.end());
			// Store excerpt into hash by excerpt name
			repositoryExcerpts.put(matcher.group(1).trim().toLowerCase(),
					repositoryContent.substring(matcher.end(), endIdx));
		}

		// [B] Replace all content page {multi-excerpt-include}s with the
		// original {multi-excerpt} text
		Pattern includePattern = getRepositoryIncludePattern(contentPageId);
		matcher = includePattern.matcher(wikiCode);
		int searchIdx = 0; // Start matching from this index
		while (matcher.find(searchIdx)) {
			String excerptName = matcher.group(3).trim();
			// Build expanded {multi-excerpt} from the {multi-excerpt-include}
			String expandedCode = "{multi-excerpt:name=" + excerptName + "}"
					+ repositoryExcerpts.get(excerptName.toLowerCase())
					+ "{multi-excerpt}";
			// Replace the {multi-excerpt-include} with the {multi-excerpt}
			wikiCode.replace(matcher.start(), matcher.end(), expandedCode);
			// Backup the matcher to catch any {multi-excerpt-include}s that may
			// exist in the newly replaced excerpt code
			searchIdx = matcher.start();
		}
		
		// [C] Convert the user-created {multi-excerpt-include}s to reference the
		// content wiki page rather than the repository
		matcher = MULTI_EXCERPT_INCLUDE_PATTERN.matcher(wikiCode);
		while (matcher.find()) {
			
			// Split out the foreign page ID from the excerpt name 
			String[] foreignRepositoryExcerpt = matcher.group(3).trim().split("_");
			// Retrieve the wiki page by page ID
			Page foreignPage = rpc.getPage(foreignRepositoryExcerpt[0]);
			// Rebuild the user-created {multi-excerpt-include}
			String includeCode = "{multi-excerpt-include:" + foreignPage.getSpace() + ":"
					+ foreignPage.getTitle() + "|name=" + foreignRepositoryExcerpt[1] + "|nopanel=true}";
			// Replace user-created {multi-excerpt-include}
			wikiCode.replace(matcher.start(), matcher.end(), includeCode);
		}

		// [D] Return the expanded code for the content page
		return wikiCode.toString();
	}

	/**
	 * Retrieves the wiki code for a specific excerpt
	 * 
	 * @param repositoryPage
	 *            Page in which the content page's excerpts are kept
	 * @param contentPageId
	 *            Page ID of the content page
	 * @param excerptName
	 *            Name of the excerpt
	 * @return
	 */
	static String getExcerpt(Page repositoryPage, String contentPageId,
			String excerptName) {
		Pattern excerptPattern = getRepositoryExcerptPattern(contentPageId,
				excerptName);
		String repositoryContent = repositoryPage.getContent();
		Matcher matcher = excerptPattern.matcher(repositoryContent);
		if (matcher.find()) {
			// Find the closing tag index
			int endIdx = StringUtils.indexOfIgnoreCase(repositoryContent,
					"{multi-excerpt}", matcher.end());

			// Return excerpt without the {multi-excerpt} tags
			return repositoryContent.substring(matcher.end() + 1, endIdx - 1);
		}
		return null; // Excerpt does not exist in repository
	}

	/**
	 * Returns a list of page IDs and titles for wiki pages that reference the
	 * given excerpt
	 * 
	 * @param contentPageId
	 *            Page ID of wiki page to which the excerpt belongs
	 * @param excerptName
	 *            Name of excerpt
	 * @param rpc
	 *            Confluence RPC connection
	 * @return HashMap<page ID, page title>
	 * @throws SwizzleException
	 * @throws ConfluenceException
	 */
	static HashMap<String, String> getExcerptDependencies(String contentPageId,
			String excerptName, Confluence rpc) throws ConfluenceException,
			SwizzleException {
		HashMap<String, String> dependencies = new HashMap<String, String>();
		// Create search term encapsulated by double quotes for an exact match
		String searchTerm = "\"\\{multi-excerpt-include\" \"name="
				+ contentPageId + "_" + excerptName + "\"";
		// Perform Confluence search
		@SuppressWarnings("unchecked")
		List<SearchResult> results = rpc.search(searchTerm, MAX_RESULTS);
		// Get page IDs for each result
		for (SearchResult r : results) {
			dependencies.put(r.getId(), r.getTitle());
		}
		return dependencies;
	}

	/**
	 * Returns a list of pages affected by losing an excerpt reference in the
	 * Excerpt Repository if the given wiki code is saved to its corresponding
	 * page
	 * 
	 * @param wikiCode
	 *            Updated wiki code for page identified by pageId
	 * @param contentPageId
	 *            ID of content page
	 * @param repositoryPage
	 *            Excerpt Repository page
	 * @param rpc
	 *            Confluence remote procedure call connection
	 * @return HashMap<excerpt name, vector of affected pageIDs>
	 * @throws SwizzleException
	 * @throws ConfluenceException
	 */
	static HashMap<String, Vector<String>> getReferencesToDroppedExcerpts(
			StringBuffer wikiCode, String contentPageId, Page repositoryPage,
			Confluence rpc) throws ConfluenceException, SwizzleException {

		HashMap<String, Vector<String>> affectedPages = new HashMap<String, Vector<String>>();
		// Excerpt names of multi-excerpts in wikiCode
		Vector<String> contentExcerptNames = new Vector<String>();
		// Excerpt names of multi-excerpts in repository page
		Vector<String> repositoryExcerptNames = new Vector<String>();

		// [A] Find the names of all multi-excerpts in the Content Page
		Matcher matcher = MULTI_EXCERPT_PATTERN.matcher(wikiCode);
		while (matcher.find()) {
			// Store portion that matches the excerpt name
			String name = matcher.group(2);
			if (name != null) { // Close tags don't have names
				contentExcerptNames.add(name.trim().toLowerCase());
			}
		}

		// [B] Find the names of all multi-excerpts in the repository that
		// belong to the given Content Page
		Pattern pageExcerptPattern = getRepositoryExcerptPattern(contentPageId);
		matcher = pageExcerptPattern.matcher(repositoryPage.getContent());
		while (matcher.find()) {
			// Store excerpt name
			repositoryExcerptNames.add(matcher.group(1).trim().toLowerCase());
		}

		// [C] Find out which excerpts will be deleted if the content page is
		// saved
		repositoryExcerptNames.removeAll(contentExcerptNames);

		// [D] Return a list of IDs for all pages affected by deletion of
		// excerpt from repository
		for (String name : repositoryExcerptNames) {
			// Search if any pages reference this excerpt
			@SuppressWarnings("unchecked")
			ArrayList<SearchResult> results = (ArrayList<SearchResult>) rpc
					.search("\"{multi-excerpt-include:"
							+ repositoryPage.getSpace() + ":"
							+ repositoryPage.getTitle() + "|name="
							+ contentPageId + "_" + name + "\"", MAX_RESULTS);
			// Store IDs of affected pages if they exist
			if (results.size() > 0) {
				// List of page IDs
				Vector<String> pageIds = new Vector<String>();

				for (SearchResult r : results) {
					pageIds.add(r.getId());
				}

				// Add record to affectedPages
				affectedPages.put(name, pageIds);
			}
		}

		return affectedPages;
	}

	/**
	 * Exports a stack of multi-excerpts to the Excerpt Repository in the given
	 * order (LIFO). Also checks for and deletes repository excerpts that are no
	 * longer in use
	 * 
	 * @param repositoryStack
	 *            Stack of StringBuffer excerpts
	 * @param contentPageId
	 *            ID of the wiki page from which the excerpts were extracted
	 * @param repositoryPage
	 *            Excerpt Repository Confluence page
	 * @param rpc
	 *            Remote Procedure Call connection
	 * @throws SwizzleException
	 * @throws ConfluenceException
	 */
	private static void saveToRepository(Stack<StringBuffer> repositoryStack,
			String contentPageId, Page repositoryPage, Confluence rpc)
			throws ConfluenceException, SwizzleException {

		// Pattern/regex to find all excerpts for the current wiki page in the
		// repository
		final String pageExcerptRegex = "\\{multi-excerpt:name\\s*=\\s*"
				+ contentPageId + "_" + EXCERPT_NAME_REGEX + "+\\}";
		final Pattern pageExcerptPattern = Pattern.compile(pageExcerptRegex,
				Pattern.CASE_INSENSITIVE);
		// Pattern/regex to find the closing {multi-excerpt} tag
		final Pattern closeTag = Pattern.compile("\\{multi-excerpt\\}\n+",
				Pattern.CASE_INSENSITIVE);

		// [A] Build excerpt text for export to repository
		StringBuffer excerptContent = new StringBuffer();
		while (!repositoryStack.isEmpty()) {
			excerptContent.append(repositoryStack.pop() + "\n\n");
		}

		// [B] Remove excerpts from repository
		// TODO - repositoryPage.setLocks(0);
		StringBuffer pageContent = new StringBuffer(repositoryPage.getContent());

		Matcher matcher = pageExcerptPattern.matcher(pageContent);

		// Identify and delete region from repository page containing
		// multi-excerpts for content page
		if (matcher.find()) {
			// Find index of first multi-excerpt for page
			int firstIdx = matcher.start();
			int lastIdx = matcher.end();
			// Find index of last multi-excerpt for page
			while (matcher.find()) {
				lastIdx = matcher.end();
			}
			// Find the index of the closing multi-excerpt tag for the last one
			matcher = closeTag.matcher(pageContent);
			// Start searching from index of the last multi-excerpt start tag
			matcher.find(lastIdx);
			lastIdx = matcher.end();
			// Delete multi-excerpt region for content page
			pageContent.delete(firstIdx, lastIdx);
			// [C1] Insert into deleted region the new excerpts
			pageContent.insert(firstIdx, excerptContent);

		} else {
			// [C2] Append new page excerpts to repository
			pageContent.append(excerptContent);
		}

		// TODO - REMOVE
		System.out.println("=======REPOSITORY======");
		System.out.println(pageContent);

		// [D] Save page content
		// TODO
		// PageUtils.save(repositoryPage, pageContent.toString(), rpc);
	}

	/**
	 * Returns pattern for finding content-page-specific excerpts in the Excerpt
	 * Repository page
	 * 
	 * @param contentPageId
	 *            Page ID of wiki page containing main content
	 * @return Pattern for {multi-excerpt}s belonging to the content page
	 */
	private static Pattern getRepositoryExcerptPattern(String contentPageId) {
		final String pageExcerptRegex = "\\{multi-excerpt:name\\s*=\\s*"
				+ contentPageId + "_(" + EXCERPT_NAME_REGEX + ")\\}";
		return Pattern.compile(pageExcerptRegex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Returns pattern for finding a specific excerpt in the repository
	 * 
	 * @param contentPageId
	 *            Page ID of wiki page containing main content
	 * @param excerptName
	 *            Name of the excerpt
	 * @return
	 */
	private static Pattern getRepositoryExcerptPattern(String contentPageId,
			String excerptName) {
		final String pageExcerptRegex = "\\{multi-excerpt:name\\s*=\\s*"
				+ contentPageId + "_(" + excerptName + ")\\}";
		return Pattern.compile(pageExcerptRegex, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Returns pattern for finding content-page-specific excerpt-includes (that
	 * are stored in the Excerpt Repository)
	 * 
	 * @param contentPageId
	 * @return Pattern for {multi-excerpt-include}s that originally belong to
	 *         the content page
	 */
	private static Pattern getRepositoryIncludePattern(String contentPageId) {
		final String MULTI_EXCERPT_INCLUDE_REGEX = "\\{multi-excerpt-include:("
				+ CONFLUENCE_NAME_REGEX + "):(" + CONFLUENCE_NAME_REGEX
				+ ")\\|\\s*name\\s*=\\s*" + contentPageId + "_("
				+ EXCERPT_NAME_REGEX + ")+[\\s\\w\\|=]*\\}";
		return Pattern.compile(MULTI_EXCERPT_INCLUDE_REGEX,
				Pattern.CASE_INSENSITIVE);
	}
}
