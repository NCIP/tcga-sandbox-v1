import org.codehaus.swizzle.confluence.Confluence;
import org.codehaus.swizzle.confluence.ConfluenceException;
import org.codehaus.swizzle.confluence.Page;
import org.codehaus.swizzle.confluence.SwizzleException;


/**
 * Provides support for basic Confluence Page actions
 * 
 * @author Anna Chu
 *
 */
public class PageUtils {
	/**
	 * Saves content to wiki page
	 * 
	 * @param page Confluence wiki page
	 * @param content wiki code
	 * @param rpc Confluence RPC connection
	 * @throws SwizzleException 
	 * @throws ConfluenceException 
	 */
	static void save(Page page, String content, Confluence rpc) throws ConfluenceException, SwizzleException{
		page.setContent(content);
		rpc.storePage(page);
	}
	
	static void saveOffline(){
		
	}
	
	static void delete(){
		
	}
	
	/**
	 * Properly renames a page and any excerpts
	 * 
	 * @param page
	 * @param newPageName
	 */
	static void rename(Page page, String newPageName){
		
	}
	
	static void move(){
		
	}
}
