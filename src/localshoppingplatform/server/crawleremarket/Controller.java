package localshoppingplatform.server.crawleremarket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import localshoppingplatform.server.crawlerdatabase.Driver;
import localshoppingplatform.shared.Product;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

public class Controller {
	int numberOfCrawlers = 1;
	CrawlConfig config;
	PageFetcher pageFetcher;
	RobotstxtConfig robotstxtConfig;
	RobotstxtServer robotstxtServer;
	CrawlController controller;
	public static List<String> extra_urls = new ArrayList<String>();
	public static Map<String, Product> productMap_main = new HashMap<String, Product>();
	public static List<String> newProduct = new ArrayList<String>();
	public static int productsHash=0;
	int extra_urls_counter;
	public static CrawlController passController;

	public static void main(String[] args) throws Exception {
		// Delete integer value after testing
		int numberofcrawling = 1;
		CrawlController controllerStart;
		Controller c = new Controller();
		// Add starting seed page
		//Main Test URL
		String parent_urls = "https://atalanda.com/wuppertal/vendors/festartikel-hirschfeld";
		//Changable test URL
//		String parent_urls = "https://atalanda.com/wuppertal/products/teufelchen-rock-und-oberteil-gr-40-42?abs_pos=129&total=357";
		
		controllerStart = c.intitController(parent_urls);
		passController = controllerStart;
		controllerStart.start(MyCrawler.class, numberofcrawling);

		// This part is to extend the crawling process when there are no more
		// seed pages
		while (controllerStart.isFinished()
				&& Controller.extra_urls.isEmpty() == false) {
			Driver db_drive = new Driver();
			if (productMap_main.isEmpty() == false) {
				db_drive.insertTest(productMap_main, newProduct);
				newProduct.clear();
			}
			controllerStart = c.intitController(Controller.extra_urls.get(0));
			Controller.extra_urls.remove(0);
			passController = controllerStart;
			controllerStart.start(MyCrawler.class, numberofcrawling);
		}
	}

	// Initialize crawling process
	CrawlController intitController(String seed_page) throws Exception {
		String crawlStorageFolder = "H:\\Eclipse Work Space\\e_Market\\Crawloutput";
		config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxDepthOfCrawling(numberOfCrawlers);

		/*
		 * Instantiate the controller for this crawl.
		 */
		pageFetcher = new PageFetcher(config);
		robotstxtConfig = new RobotstxtConfig();
		robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		controller = new CrawlController(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		controller.addSeed(seed_page);

		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		return controller;
	}
	public HashMap<String, Product> startCrawl(String url){
		//Do somthing
		
		return null;
		
	}
}
