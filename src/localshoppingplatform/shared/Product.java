package localshoppingplatform.shared;

import java.util.ArrayList;
import java.util.List;

public class Product {
	int id;
	String title, vendor_name, link_url, date_of_visit, original_key,
			fake_titleProduct_matcher, originalPage, relatedUrls, platform_name, vendor_contact, location, parentUrl;
	double price;
	boolean sale;
	public List<String> getProductRelatedurls() {
		return productRelatedurls;
	}

	public void setProductRelatedurls(String productRelatedurls) {
		this.productRelatedurls.add(productRelatedurls);
	}

	List<String> productRelatedurls = new ArrayList<String>();

	public String getParentUrl() {
		return parentUrl;
	}

	public void setParentUrl(String parentUrl) {
		this.parentUrl = parentUrl;
	}


	public String getFake_titleProduct_matcher() {
		return fake_titleProduct_matcher;
	}

	public void setFake_titleProduct_matcher(String fake_titleProduct_matcher) {
		this.fake_titleProduct_matcher = fake_titleProduct_matcher;
	}	

	public String getRelatedUrls() {
		return relatedUrls;
	}

	public void setRelatedUrls(String relatedUrls) {
		this.relatedUrls = relatedUrls;
	}

	public String getOriginalPage() {
		return originalPage;
	}

	public void setOriginalPage(String originalPage) {
		this.originalPage = originalPage;
	}


	public String getFake_product_title() {
		return fake_titleProduct_matcher;
	}

	public void setFake_product_title(String fake_product_title) {
		this.fake_titleProduct_matcher = fake_product_title;
	}

	public String getVendor_contact() {
		return vendor_contact;
	}

	public void setVendor_contact(String vendor_contact) {
		this.vendor_contact = vendor_contact;
	}

	public String getPlatform_name() {
		return platform_name;
	}

	public void setPlatform_name(String platform_name) {
		this.platform_name = platform_name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLink_url() {
		return link_url;
	}

	public String getOriginal_key() {
		return original_key;
	}

	public void setOriginal_key(String original_key) {
		this.original_key = original_key;
	}

	public void setLink_url(String link_url) {
		this.link_url = link_url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getVendor_name() {
		return vendor_name;
	}

	public void setVendor_name(String vendor_name) {
		this.vendor_name = vendor_name;
	}

	public boolean isSale() {
		return sale;
	}

	public void setSale(boolean sale) {
		this.sale = sale;
	}

	public String getDate_of_visit() {
		return date_of_visit;
	}

	public void setDate_of_visit(String date_of_visit) {
		this.date_of_visit = date_of_visit;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
