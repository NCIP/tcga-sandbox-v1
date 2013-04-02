import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;

import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.ConfluenceException;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.SwizzleException;

public class Main {
	// URL of the xwiki instance
	final static String url = "https://wiki.nci.nih.gov/rpc/xmlrpc";
	// Confluence RPC connection
	static Confluence rpc;
	final static int SAVE = 1;
	final static int READ = 2;
	final static int RETRIEVE = 3;
	final static int DEPENDENCIES = 4;
	static Page wikiPage;
	static Page repoPage;
	static BufferedReader input;

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// Excerpt repository space name
		final String repositorySpace = "TCGAUI";
		// Excerpt repository page name
		final String repositoryName = "Excerpt Repository";
		// User name
		final String username;
		// User Password
		final String password;

		rpc = new Confluence(url);

		// Perform Login & Authentication
		try {
			// TODO read in username and password
			username = "";
			password = "";
			// TODO display wait/processing while connecting
			rpc.login(username, password);

			// TODO - user input into dialogue boxes
			input = new BufferedReader(new InputStreamReader(System.in));

			// TODO call actual wiki page
			wikiPage = new Page();
			repoPage = new Page();
			wikiPage.setId("ABC123");
			repoPage.setSpace("TCGAUI");
			repoPage.setTitle("Excerpt Repository");

			// TODO - REMOVE - Action type: [1]save, [2] read
			System.out.println("What would you like to test?");
			System.out.println("[1] Save wiki page");
			System.out.println("[2] Read wiki page");
			System.out
					.println("[3] Retrieve code for {multi-excerpt-include}s");
			System.out.println("[4] List dependencies for an excerpt");

			// TODO - REMOVE
			int action = Integer.valueOf(input.readLine().trim()).intValue();

			// TODO - REMOVE - Repository page
			StringBuffer content = new StringBuffer();
			BufferedReader br = new BufferedReader(new FileReader("repo.txt"));
			String line = br.readLine();
			while (line != null) {
				content.append(line + "\n");
				line = br.readLine();
			}
			repoPage.setContent(content.toString());

			if (action == SAVE || action == RETRIEVE || action == DEPENDENCIES) {
				// TODO - REMOVE - wiki page (user POV)
				content = new StringBuffer();
				br = new BufferedReader(new FileReader("wikiPage_user.txt"));
				line = br.readLine();
				while (line != null) {
					content.append(line + "\n");
					line = br.readLine();
				}
				wikiPage.setContent(content.toString());

				// SAVE ACTION
				if (action == SAVE) {
					save();
				} else if (action == RETRIEVE) {
					retrieve();
				} else if (action == DEPENDENCIES) {
					dependencies();
				}

			} else if (action == READ) {
				// TODO - REMOVE - wiki page (storage POV)
				content = new StringBuffer();
				br = new BufferedReader(new FileReader("wikiPage_stored.txt"));
				line = br.readLine();
				while (line != null) {
					content.append(line + "\n");
					line = br.readLine();
				}
				wikiPage.setContent(content.toString());

				// READ ACTION
				read();
			}

			input.close();
			rpc.logout();
		} catch (ConfluenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SwizzleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Translates user code to storage code and saves page
	 * 
	 * @throws ConfluenceException
	 * @throws SwizzleException
	 * @throws IOException
	 */
	private static void save() throws ConfluenceException, SwizzleException,
			IOException {
		SyntaxChecker sc = new SyntaxChecker(wikiPage);
		boolean continueSave = true;

		if (!sc.isValid()) {
			// // Errors/Warnings exist
			// // TODO complain to users
			System.out.println("Valid?: " + sc.isValid());
			System.out.println("Error: " + sc.getResultMessage());
			if (sc.getResultType() == SyntaxChecker.TYPE_WARNING) {
				continueSave = input.readLine().equalsIgnoreCase("y");
			}
		}

		if (sc.getResultType() == SyntaxChecker.TYPE_ERROR || !continueSave) {
			return;
		}

		// ON SAVE - no errors
		StringBuffer wikiCode = sc.getWikiCode();

		// [A] Replace excerpts with multi-excerpts
		if (ExcerptUtils.containsExcerpt(wikiCode.toString())) {
			// Ask user for excerpt Name
			// TODO read in name
			System.out
					.println("Your wiki code contains an {excerpt} that will be converted into a {multi-excerpt}.\nPlease enter a name for this new {multi-excerpt}:");
			String excerptName = input.readLine().trim();

			boolean isValidName = ExcerptUtils.isValidExcerptName(excerptName);
			boolean isExistingName = ExcerptUtils.isExistingName(
					wikiCode.toString(), excerptName);
			while (!isValidName || isExistingName) {
				if (!isValidName) {
					// TODO pass error back to user - allow cancel
					System.out
							.println("This is not a valid excerpt name. Please provide another:");
					excerptName = input.readLine().trim();
				} else {
					// TODO pass error back to user - allow cancel
					System.out
							.println("This name already exists for another {multi-excerpt}. Please select another name:");
					excerptName = input.readLine().trim();
				}
			}

			// Continue with conversion
			ExcerptUtils.convertExcerptToMultiExcerpt(wikiCode, excerptName);
			System.out.println(wikiCode);

		}

		// [B] Save page
		// TODO runnable
		HashMap<String, Vector<String>> affectedPageIds = ExcerptUtils
				.getReferencesToDroppedExcerpts(wikiCode, wikiPage.getId(),
						repoPage, rpc);
		if (!affectedPageIds.isEmpty()) {
			// TODO dialogue box with links to Pages that open in new tab
			String msg = "The following pages are referencing one or more excerpts that will be deleted if this page saves:<br>";
			// Print each excerpt and their affected page(s)
			for (Map.Entry<String, Vector<String>> entry : affectedPageIds
					.entrySet()) {
				msg += "The excerpt named '" + entry.getKey()
						+ "' was removed and affects pages<ul>";
				for (String pageId : entry.getValue()) {
					msg += "<li>" + rpc.getPage(pageId).getTitle();
				}
				msg += "</ul>";
			}
			msg += "<br>Please remove these references before trying to save again.";
			System.out.println(msg);
			return;
		}

		if (ExcerptUtils.containsMultiExcerpt(wikiCode.toString())) {
			// Extract excerpts into Excerpt Repository
			try {
				String condensedCode = ExcerptUtils.condenseCode(wikiCode,
						wikiPage.getId(), repoPage, rpc);
				System.out.println("=========== Condensed Code ============");
				System.out.println(condensedCode);

			} catch (SwizzleException e) {
				System.out
						.println("ERROR: Trouble connecting and/or saving to the excerpt repository!");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads stored code and displays in user format
	 */
	private static void read() {
		try {
			System.out.println(ExcerptUtils.expandCode(
					new StringBuffer(wikiPage.getContent()), wikiPage.getId(),
					repoPage, rpc));
		} catch (ConfluenceException e) {
			System.out.println("ERROR: Trouble reading from excerpt repository");
			e.printStackTrace();
		} catch (SwizzleException e) {
			System.out.println("ERROR: Trouble reading from excerpt repository");
			e.printStackTrace();
		}
	}

	/**
	 * Displays excerpts for all {multi-excerpt-include}s. Useful when user
	 * wants to expand on text by clicking
	 * 
	 * Test with native includes that are user-stated
	 */
	private static void retrieve() {
		Matcher matcher = ExcerptUtils.MULTI_EXCERPT_INCLUDE_PATTERN
				.matcher(wikiPage.getContent());

		while (matcher.find()) {
			String excerptName = matcher.group(3);
			System.out.println("===== Code for " + excerptName + "======");
			System.out.println(ExcerptUtils.getExcerpt(repoPage, "64685470",
					excerptName));
		}
	}

	/**
	 * Displays all pages dependent on a given excerpt. Useful when user wants
	 * to see which pages are affected by edits to page
	 * 
	 * @throws IOException
	 */
	private static void dependencies() throws IOException {
		try {
			String excerptName = "go";
			HashMap<String, String> dependencies = ExcerptUtils
					.getExcerptDependencies(wikiPage.getId(), excerptName, rpc);
			if (dependencies.isEmpty()) {
				System.out.println("No dependencies exist!");
			} else {
				System.out.println("The following pages depend on '"
						+ excerptName + "':");
				System.out.println("Page ID\tPage Name");
				for (Map.Entry<String, String> entry : dependencies.entrySet()) {
					System.out
							.println(entry.getKey() + "\t" + entry.getValue());
				}
			}
		} catch (ConfluenceException e) {
			System.out.println("Cannot perform search on excerpt dependencies");
			e.printStackTrace();
		} catch (SwizzleException e) {
			System.out.println("Cannot perform search on excerpt dependencies");
			e.printStackTrace();
		}
	}

}
