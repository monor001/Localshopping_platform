package localshoppingplatform.server.crawleremarket;

import java.util.Set;
import java.util.regex.Pattern;

import localshoppingplatform.server.crawlertextpreprocess.MainIngine;
import localshoppingplatform.shared.Product;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

public class MyCrawler extends WebCrawler {

	/* ========Attributes======== */

	MainIngine m_ingine = new MainIngine();
	int shutdownCount = 0;

	/* ========Methods======== */

	/**
	 * This method receives two parameters. The first parameter is the page in
	 * which we have discovered this new url and the second parameter is the new
	 * url. You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic). In this example,
	 * we are instructing the crawler to ignore urls that have css, js, git, ...
	 * extensions and to only accept urls that start with
	 * "http://www.ics.uci.edu/". In this case, we didn't need the referringPage
	 * parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		final Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
				+ "|png|mp3|mp3|zip|gz))$");
		String href = url.getURL().toLowerCase();
		return !FILTERS.matcher(href).matches()
				&& href.startsWith("http://shop.kauf.in/|https://atalanda.com|https://www.fruugo.de"
						+ "|https://www.locafox.de|https://www.kiezkaufhaus.de|"
						+ "http://osos-sales.de|https://www.simply-local.de");
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		Product productAnalyzeObj = new Product();
		String parent_url = page.getWebURL().getURL();
		System.out
				.println("===========================================>New URL<===========================================\nURL: "
						+ parent_url);
		if (page.getParseData() instanceof HtmlParseData) {
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = htmlParseData.getHtml();
			Set<WebURL> links = htmlParseData.getOutgoingUrls();

			// Reformlate the extracted text
			String page_text = reformulateExtractedText(text);
			// Delete the following line (test line)
			// System.out.println(page_text);
			// urls of interest
			// Delete the following line (test line)
			String page_urls = links.toString();
			page_urls = reformulateExtractedUrls(page_urls);
			// Delete the following line (test line)
			// System.out.println(links.toString());

			// Start to analyze
			Controller.productMap_main = m_ingine.startAnalyze(page_text,
					page_urls, parent_url, Controller.productMap_main);
			if (shutdownCount > 600) {
				Controller.passController.shutdown();
				System.out.println("Shutdown after: "+shutdownCount);
			}

			shutdownCount++;
			// Check if the page is revealing categories
			// if (url.matches("(https://(.*)categories(.*))[\r\n]?")) {
			// Extract text for analysis and testing with class Tester logic
			// }
		}
	}

	String reformulateExtractedText(String s) {
		String temp;
		temp = s.replaceAll("\\p{Space}{2,}", "\n");
		return (temp);
	}

	String reformulateExtractedUrls(String page_urls) {
		page_urls = page_urls.replaceAll("(\\[|\\])", "");
		return page_urls;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
