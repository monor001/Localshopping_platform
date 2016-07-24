package localshoppingplatform.server.crawlertextpreprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import localshoppingplatform.server.crawleremarket.Controller;
import localshoppingplatform.shared.Product;

public class ProductListFilter {

	/* ========Attributes======== */
	/*
	 * These attributes are meant to match price of any a sale or a product
	 * represented per page
	 */
	// Data entry (Start)

	final String regexSaleprice = "[[0-9]?[A-z]\\s\\.\\-ÈèÄäöÖÜüß,“„]+ (SALE|ab|Sale|AB|Ab) [\\d, \\d]+ €|(SALE|ab|Sale|AB|Ab) [\\d, \\d]+ €";
	final String regexProductprice = "(\\s?|\\n?)[0-9A-z\\s\\.\\-ÑñÈèÔôÄäöÖÜüß&%´/,\"“„\\(\\)#]+(\\s| )?[\\d, \\d]+( )?(€|$)";
	// Data entry (End)
	// Pattern elements (Start)
	private Pattern pricePattern;
	private Matcher priceMatcher;
	// Pattern elements (End)
	// Store elements (Start)
	public String salePriceholder;
	public String productPriceholder;
	public String saleMatcher;
	private String original_key;
	private List<String> seededUrls = new ArrayList<String>();
	// Pattern elements (End)
	// Counters (For only TESTING, Delete them later)(Start)
	public static int priceCounter = 0;
	public static int productPriceCounter = 0;

	// The following attribute is for saving Product title in addition to its
	// Price

	/* ========Methods======== */

	// This method concerned to extract price of each product presented per page
	public Map<String, Product> extractPriceSale(String productListholder,
			Map<String, Product> productMap, String parentUrl) {
		TextFilter productTextfilter = new TextFilter();
		ProductListFilter productObjholder = new ProductListFilter();
		TextFilter text_filter = new TextFilter();

		// Text reformation

		// Check if there are sales
		pricePattern = Pattern.compile(regexSaleprice, Pattern.DOTALL);
		priceMatcher = pricePattern.matcher(productListholder);
		boolean flag_sale_out = false;
		while (priceMatcher.find()) {
			priceCounter++;
			productObjholder.priceCounter = priceCounter;
			salePriceholder = priceMatcher.group();
			productObjholder.salePriceholder = salePriceholder;
			saleMatcher = productListholder.replaceAll(regexSaleprice, "");
			// Just for test (Delete later)
			// System.out.println(CountSalePrice + ". Sale price: "
			// + StoreSalePrice);
			flag_sale_out = true;
		}
		// System.out.println("After extracting Sale price: "+StoreAfterRegex

		// Extract product prices
		if (saleMatcher != null && flag_sale_out == true) {
			productListholder = text_filter.extractProductsList(saleMatcher);
			productMap = extractProductPrices(productListholder, productMap,
					parentUrl);

		} else {
			productListholder = text_filter
					.extractProductsList(productListholder);// problem in this
															// line
			productMap = extractProductPrices(productListholder, productMap,
					parentUrl);

		}
		return productMap;
	}

	Map<String, Product> extractProductPrices(String ProductPriceExtraction,
			Map<String, Product> productMap, String parentUrl) {
		pricePattern = Pattern.compile(regexProductprice);
		priceMatcher = pricePattern.matcher(ProductPriceExtraction);
		while (priceMatcher.find()) {
			productPriceCounter++;
			this.productPriceCounter = productPriceCounter;
			productPriceholder = priceMatcher.group();
			this.productPriceholder = productPriceholder;
			// Print Products before proceeding
			// System.out.println(productPriceholder_test);
			// The following part is to indexing extracted products and its
			// relative price into a hashmap
			if (productPriceholder.startsWith("\n")) {
				productPriceholder = productPriceholder.substring(1);
			}
			String[] hashStringspliter = productPriceholder.split("\n");
			for (int i = 0; i < hashStringspliter.length; i++) {
				if (hashStringspliter.length > 2) {
					i = i + 1;
				}
				int j = i;
				Product p_t = new Product();
				if (++j != hashStringspliter.length) {
					hashStringspliter[i] = hashStringspliter[i].replaceAll(
							"(\\.){2,100}", "");
					p_t.setTitle(hashStringspliter[i]);
					p_t.setFake_product_title(hashStringspliter[i]);
					p_t.setParentUrl(parentUrl);
					String price_temp = hashStringspliter[j];
					price_temp = price_temp.replaceAll("€", "");
					price_temp = price_temp.substring(0,
							price_temp.length() - 1);
					price_temp = price_temp.replaceAll("\n", "");
					price_temp = price_temp.replaceAll(",", ".");
					if (Double.isNaN((Double.valueOf(price_temp))) == false) {
						p_t.setPrice(Double.valueOf(price_temp));
					}
					// productAttributes_test.add(p_t);
					original_key = hashStringspliter[i];
					original_key = original_key.replaceAll("\\.{2}", "");
					p_t.setOriginal_key(original_key);
					hashStringspliter[i] = hashStringspliter[i].replaceAll(
							",|\"|\\+|-|\\.", " ");
					hashStringspliter[i] = hashStringspliter[i].replaceAll(
							"(\\s{2})", " ");
					String original_key_temp = hashStringspliter[i];

					// Check the length of the product title
					if (productMap.containsKey(hashStringspliter[i]) == false) {
						if (hashStringspliter[i].length() > 4) {
							productMap.put(hashStringspliter[i], p_t);
							System.out
									.println("--------------------------------------------\nKey:\n"
											+ hashStringspliter[i]
											+ "\nProduct title:\n"
											+ productMap.get(
													hashStringspliter[i])
													.getTitle());
							Controller.newProduct.add(hashStringspliter[i]);
						}
					}
				}
				i = j;
			}

		}
		// Just for test (Delete later)
		// System.out.println(CountProductPrice + ". Product price: "
		// + StoreProductPrice);
		// }
		return productMap;
	}

	/*
	 * ================= Main Part =================
	 */
	public static void main(String[] args) {
		Map<String, Product> productMap_main = new HashMap<String, Product>();
		ProductListFilter pnt = new ProductListFilter();
		LinkAnalysis link_analyz = new LinkAnalysis();
		Product pro = new Product();
		pro.setTitle("Title");
		pro.setFake_product_title("Title");
		pro.setPrice(69.95);// Price
		pro.setOriginal_key("key");
		// productMap_main.put("key", pro);
		// extract product info.
		productMap_main = pnt.extractPriceSale(pnt.page, productMap_main,
				pnt.parent_url);
		productMap_main = link_analyz.filterUrls(pnt.urls, productMap_main,
				pnt.parent_url);
	}

	String page = "";
	String parent_url = "";
	String urls = "";
}
