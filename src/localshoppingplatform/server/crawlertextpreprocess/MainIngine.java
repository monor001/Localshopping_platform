package localshoppingplatform.server.crawlertextpreprocess;

import java.util.HashMap;
import java.util.Map;

import localshoppingplatform.shared.Product;

import com.google.common.collect.Maps;

public class MainIngine {

	// Map<String, Product> productMap_main = new HashMap<String, Product>();
	Map<String, Product> productMap_main_temp = new HashMap<String, Product>();
	ProductListFilter pnt = new ProductListFilter();
	SingleProductPageFilter single_prod_filter = new SingleProductPageFilter();
	LinkAnalysis link_analyz = new LinkAnalysis();

	public Map<String, Product> startAnalyze(String page_text, String urls,
			String parent_url, Map<String, Product> productMap_main) {
		{
			// Start with page full of products and check if the map is clean
			if (productMap_main.isEmpty()) {
				productMap_main.putAll(pnt.extractPriceSale(page_text,
						productMap_main, parent_url));
				productMap_main = link_analyz.filterUrls(urls, productMap_main,
						parent_url);
			}
			// Check if map is not clean and if the seeded page url matches
			// a product on the map
			else {
				// extract vendor info.
				// for (String key : productMap_main.keySet()) {
				// System.out.println("before: "+productMap_main.get(key).getLink_url());
				// }
				productMap_main = single_prod_filter.extractProductSection(
						productMap_main, parent_url, page_text, urls);
				// extract product info.
				productMap_main = pnt.extractPriceSale(page_text,
						productMap_main, parent_url);
				// for (String key : productMap_main.keySet()) {
				// System.out.println("After Extract prices: "+productMap_main.get(key).getLink_url());
				// }
				productMap_main = link_analyz.filterUrls(urls, productMap_main,
						parent_url);
				// for (String key : productMap_main.keySet()) {
				// System.out.println("After extract links: "+productMap_main.get(key).getLink_url());
				// }
			}
		}
		return productMap_main;
	}

	// Delete the following method (Test method)
	String reformulateExtractedUrls(String page_urls) {
		page_urls = page_urls.replaceAll("(\\[|\\])", "");
		return page_urls;
	}

	public static void main(String[] args) {
	}

}
