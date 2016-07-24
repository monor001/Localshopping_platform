package localshoppingplatform.server.crawlertextpreprocess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import localshoppingplatform.server.crawleremarket.Controller;
import localshoppingplatform.shared.Product;
import edu.uci.ics.crawler4j.crawler.CrawlController;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

/*
 * This class is created to test the functionalty of storing text data into
 * HashMap for tokanization processing
 */
public class LinkAnalysis {

	/* ========Attributes======== */
	/*
	 * These attributes identifying the links need to be extracted for more
	 * analysis (categories/vendors)
	 */
	CrawlController seed;
	Controller c;
	Product p_t_obj = new Product();
	private List<String> seededUrls = new ArrayList<String>();
	private List<String> seededUrlsTemp = new ArrayList<String>();
	static int countProductsperPage = 0;
	static int countTotalproducts = 0;

	/* ========Methods======== */

	// This method used to reprocess the sequence of extracted urls for the
	// purpose of analysis
	// This method concerned to separate urls of (categories and vendors) from
	// other urls presented per page
	public Map<String, Product> filterUrls(String links,
			Map<String, Product> productobj, String parent_url) {
		seed = c.passController;
		// Separate interested urls
		// Variable temp used for url cleaning
		String tempUrl1 = links;
		String[] tempUrl2;
		String tempUrl3, tempUrl4;
		String[] temp_paret_url = new String[1];
		if (parent_url.contains(".com")) {
			temp_paret_url = parent_url.split(".com");
			temp_paret_url[0] = temp_paret_url[0] + ".com";
		} else if (parent_url.contains(".de")) {
			temp_paret_url = parent_url.split(".de");
			temp_paret_url[0] = temp_paret_url[0] + ".de";
		}
		// Separating Urls for breaking down the one of interest(Fishing
		// technique)
		tempUrl1 = tempUrl1.replaceAll(" ", "\n");
		// Clean un-wanted urls
		tempUrl1 = tempUrl1.replaceAll(
				".*.((png|jpg|css|js|gif|mp3|zip|gz|svg|ico)).*", "");
		// Reformat urls to add the selected ones to the frontier
		tempUrl1 = tempUrl1.replaceAll("(\n{2})+", "");
		tempUrl1 = tempUrl1.replaceAll(",https:", ",\nhttps:");
		tempUrl1 = tempUrl1.replaceAll(",http:", ",\nhttp:");
		tempUrl1 = tempUrl1.replaceAll(",|\\[|\\]", "");
		// System.out.println(tempUrl1);
		tempUrl2 = tempUrl1.split("\n");
		// Add extra urls to prevent crawler from running out of seed pages
		// Add selected urls to the frontier
		for (int i = 0; i < tempUrl2.length; i++) {
			if (tempUrl2[i].startsWith(temp_paret_url[0])) {
				if (Controller.extra_urls.contains(tempUrl2[i]) == false
						&& seededUrlsTemp.contains(tempUrl2[i]) == false) {
					Controller.extra_urls.add(tempUrl2[i]);
					seededUrlsTemp.add(tempUrl2[i]);
				}
			}

			if (tempUrl2[i].equals("") == false) {
				tempUrl2[i] = tempUrl2[i].replaceAll(",", "");
				/*
				 * ==============================================================
				 * This part responsible for guiding Crawler to one product page
				 */
				// Matching Product URL with extracted Product name
				tempUrl3 = tempUrl2[i];
				tempUrl4 = tempUrl3;
				tempUrl4 = tempUrl3.replaceAll("\\[|\\]", "");
				tempUrl3 = tempUrl3.replaceAll("-dot-|-slash-|-number-", " ");
				tempUrl3 = tempUrl3.replaceAll("-|\\+", " ");
				// System.out.println("..................URL.............\n"+tempUrl3);
				for (String key : productobj.keySet()) {
					// System.out.println("\n..................key.............\n"+key);
					// boolean url_producttitle_matchflag = false;
					// Work around for matching (Ü/Ä/Ö/ß)
					key = key.replaceAll(",|\\*|:|;|$|!|\\.|\\(|\\)|\"|-", "");
					String tempKey = key;
					tempKey = tempKey.toUpperCase();
					if (tempKey.contains("Ü") || tempKey.contains("Ä")
							|| tempKey.contains("Ö") || tempKey.contains("Ø")) {
						tempKey = tempKey.replaceAll("Ü", "U");
						if (tempKey.contains("Ä")) {
							tempKey = tempKey.replaceAll("Ä", "A");
						}
						if (tempKey.contains("Ö")) {
							tempKey = tempKey.replaceAll("Ö", "O");
						}
						if (tempKey.contains("Ø")) {
							tempKey = tempKey.replaceAll("Ø", "O");
						}
					}
					tempKey = tempKey.toLowerCase();
					String tempKeywithspace = tempKey;
					// Work around to match url
					String[] tempUrl3arr = tempUrl3.split("/"), tempUrl3arroriginal = tempUrl3
							.split("/");
					Pattern pattern;
					Matcher matchertempKeyflag = null, matchertempUrl3arrflag = null;
					boolean tempKeyflag = false;
					for (int l = 2; l < tempUrl3arr.length; l++) {
						tempKey = tempKey.replaceAll(" ", "");
						tempUrl3arr[l] = tempUrl3arr[l].replaceAll(" ", "");
						if (tempUrl3arr[l].length() >= 3) {
							if (tempUrl3arr[l].contains("?")) {
								int indexSubstring = 0;
								indexSubstring = tempUrl3arr[l].indexOf("?");
								tempUrl3arr[l] = tempUrl3arr[l].substring(0,
										indexSubstring);
								tempUrl3arr[l] = tempUrl3arr[l].replaceAll("/",
										"");
							}
							if (tempUrl3arroriginal[l].contains("?")) {
								int indexSubstring = 0;
								indexSubstring = tempUrl3arroriginal[l]
										.indexOf("?");
								tempUrl3arroriginal[l] = tempUrl3arroriginal[l]
										.substring(0, indexSubstring);
								tempUrl3arroriginal[l] = tempUrl3arroriginal[l]
										.replaceAll("/", "");
							}
							// In case the url-title larger than product-title
							if (tempUrl3arr[l].length() > tempKey.length()) {
								// if there are (/ or ?) choose this pattern
								pattern = Pattern.compile(".*(?i)" + tempKey
										+ "(?i).*");
								matchertempKeyflag = pattern
										.matcher(tempUrl3arr[l]);
								if (matchertempKeyflag.find() == true) {
									productobj.get(key).setRelatedUrls(
											tempUrl2[i]);
									tempKeyflag = true;
								} else {
									if (urlMatcher(tempKeywithspace,
											tempUrl3arroriginal[l])) {
										productobj.get(key).setRelatedUrls(
												tempUrl2[i]);
										tempKeyflag = true;
									}
								}
							}
							// In case the url-title smaller than
							// product-title
							if (tempKey.length() > tempUrl3arr[l].length()) {
								// if there are (/ or ?) choose this pattern
								pattern = Pattern.compile(".*(?i)"
										+ tempUrl3arr[l] + "(?i).*");
								matchertempUrl3arrflag = pattern
										.matcher(tempKey);
								if (matchertempUrl3arrflag.find() == true) {

									tempKeyflag = true;
								} else {
									if (urlMatcher(tempKeywithspace,
											tempUrl3arroriginal[l])) {
										tempKeyflag = true;
									}

								}
							}

						}
						if (tempKeyflag) {							
							// Match if the url is matched to the title or visa
							// versa
							// p_t_obj = productobj.get(key);
							// // p_t_obj.setLink_url(tempUrl2[i]);
							// p_t_obj.setPlatform_name(temp_paret_url[0]);
							// productobj.replace(key, p_t_obj);
							// Check if the URL has been redundant
							// Add URL to the foruntier
							if (seededUrls.equals(null)) {
								seededUrls.add(tempUrl4);
								seed.addSeed(tempUrl4);
								// Just for test (Delete later)
								// Just for test (Delete later)
								System.out.println("===============\n"
										+ "Seeded Page: " + tempUrl4);
								// System.out.println("=================\n" +
								// "Key: "
								// + key);
								// System.out.println("=================\n"
								// + "Title: "
								// + productobj.get(key).getTitle()
								// + "\nPrice: "
								// + productobj.get(key).getPrice()
								// + "\nURL: "
								// + productobj.get(key).getLink_url());
							} else {
								if (seededUrls.contains(tempUrl4) == false) {
									this.countProductsperPage++;
									countTotalproducts++;
									seededUrls.add(tempUrl4);
									seed.addSeed(tempUrl4);
									Controller.extra_urls.remove(tempUrl4);
									// Just for test (Delete later)
									// Just for test (Delete later)
									// System.out
									// .println("........................................................Product Number("
									// + this.countProductsperPage
									// +
									// ")....................................................");
									System.out.println("===============\n"
											+ "Seeded Page: " + tempUrl4);
									// System.out.println("=================\n"
									// + "Key: " + key);
									// System.out.println("=================\n"
									// + "Title: "
									// + productobj.get(key).getTitle()
									// + "\nPrice: "
									// + productobj.get(key).getPrice()
									// + "\nURL: "
									// + productobj.get(key).getLink_url()
									// + "\nPlatform Name: "
									// + productobj.get(key)
									// .getPlatform_name());
								}
							}

							break;
						}
					}
					if (tempKeyflag) {
						break;
					}
				}
			}
		}
		/*
		 * ============================================================== This
		 * part responsible for guiding Crawler extract category and vendor
		 * pages
		 */

		// for (String keys : productobj.keySet()) {
		//
		// if (productobj.get(keys).getLink_url() == null)
		// System.out.println("Key with no url: (" + keys
		// + ") --- Product Title: "
		// + productobj.get(keys).getTitle() + "\nParent URL: \n"
		// + productobj.get(keys).getParentUrl());
		//
		// }
		// this.countProductsperPage = 0;
		// System.out.println("...........\nTotal discovered products: "
		// + this.countTotalproducts);
		return productobj;
	}

	boolean urlMatcher(String tempkey, String url) {
		String[] tempKeyarr, urlArr;
		tempKeyarr = tempkey.split(" ");
		urlArr = url.split(" ");
		int numberOfmatch = 0;
		float matchPercent = 0;
		
		if (tempKeyarr.length > urlArr.length) {
			for (int x = 0; x < urlArr.length; x++) {
				for (int y = 0; y < tempKeyarr.length; y++) {
					if (urlArr[x].equals(tempKeyarr[y])) {
						numberOfmatch++;
					}
				}
			}
			matchPercent = numberOfmatch / urlArr.length;
			matchPercent = matchPercent * 100;
			if (matchPercent > 30) {
				return true;
			}
		}
		if (urlArr.length  > tempKeyarr.length) {
			for (int x = 0; x < urlArr.length; x++) {
				for (int y = 0; y < tempKeyarr.length; y++) {
					Pattern pattern=Pattern.compile(".*"+tempKeyarr[y]+".*");
					if(pattern.matcher(urlArr[x]).find()){
						numberOfmatch++;
					}
				}
			}

			matchPercent = numberOfmatch / tempKeyarr.length;
			matchPercent = matchPercent * 100;
			if (matchPercent > 30) {
				return true;
			}
		}

		return false;
	}
}
