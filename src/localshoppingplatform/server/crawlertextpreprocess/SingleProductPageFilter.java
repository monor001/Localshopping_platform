package localshoppingplatform.server.crawlertextpreprocess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import localshoppingplatform.shared.Product;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

public class SingleProductPageFilter {

	/*
	 * ================= Test hashing =================
	 */
	private final Map<String, String> items = new HashMap<String, String>();
	private String[] hashStringspliter;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	// The following variable contains a value of product followed/preceded by

	/*
	 * ================= Part 1 ================= Matching Url with hashmap key
	 * by using percentage technique
	 */
	// Matching Url with hash keys to red flag the single product page

	/*
	 * ================= Part 2 ================= This part aiming to find
	 * vendor name by following three steps:
	 * 
	 * 1. Adding words before/after(backword/forword) the product-hash-key into
	 * an array1 by splitting the spacing character between them (maximum size
	 * of forword addition= 50, maximum size of backword addition= 20).
	 * 
	 * 2. Adding words before/after (backword/forword) the vendor contacts into
	 * an array2 by splitting the spacing character between them (maximum size
	 * of forword addition= 20, maximum size of backword addition= 30).
	 * 
	 * 3. Match the hash key with both arrays (array1, array2).
	 */
	// ================= Part 2.1 =================
	// Cleaning text and applying first and second steps

	// Extract subString which includes both Product title + Vendor Name from
	// product section
	Map<String, Product> extractProductSection(
			Map<String, Product> product_list, String parent_url,
			String textSingleProduct, String pagerelatedurls) {
		String[] subText2arr;
		Pattern pro_title_pattern_matcher = null;
		subText2arr = textSingleProduct.split("\n");
		Matcher match_matcher;
		String textProductsection = "";
		boolean foundMatch = false;
		String keyPass = "";
		int countEntry = 0, firstTimeloopentry = 0;
		boolean flagForward = false, flagbackward = false;
		StringBuilder sbKey = null;
		for (String key : product_list.keySet()) {
			if (textSingleProduct.contains(product_list.get(key)
					.getOriginal_key())) {
				int i = textSingleProduct.indexOf(product_list.get(key)
						.getOriginal_key());
				sbKey = new StringBuilder(textSingleProduct);
				foundMatch = true;
				if (sbKey.charAt(i - 1) != ' ') {
					sbKey.insert(i, " ");
					textSingleProduct = sbKey.toString();
					if (i - 120 > -1 && i + 350 < textSingleProduct.length())
						textProductsection = textSingleProduct.substring(
								i - 120, i + 350);
				} else {
					if (i - 120 > -1 && i + 350 < textSingleProduct.length())
						textProductsection = textSingleProduct.substring(
								i - 120, i + 350);
				}
			}
			if (foundMatch == true) {
				foundMatch = false;
				String temptextProductsection = textProductsection.replaceAll(
						"\n", "");
				Pattern pattern = Pattern.compile("[\\d, \\d]+( )?€",
						Pattern.DOTALL);
				Matcher matcher = pattern.matcher(temptextProductsection);
				if (matcher.find()) {
					String priceSinglepage = matcher.group();
					priceSinglepage = priceSinglepage.replaceAll("€", "");
					priceSinglepage = priceSinglepage.substring(0,
							priceSinglepage.length() - 1);
					priceSinglepage = priceSinglepage.replaceAll("\n", "");
					priceSinglepage = priceSinglepage.replaceAll(",", ".");
					if (Double.isNaN((Double.valueOf(priceSinglepage))) == false) {
						if (product_list.get(key).getPrice() == Double
								.valueOf(priceSinglepage)) {
							product_list.get(key).setProductRelatedurls(
									parent_url);
							if (textSingleProduct.contains("www")) {
								keyPass = key;
								product_list = extractContactSection(
										textSingleProduct, textProductsection,
										product_list, keyPass);
							}
						}

					}
				}
			}
		}

		return product_list;

	}

	// Extract subString which includes both Vendor Contact + Vendor Name from
	// contact section
	Map<String, Product> extractContactSection(String textSingleProduct,
			String textProductsection, Map<String, Product> product_list,
			String key) {
		String subText_Contact;
		StringBuilder sb;
		boolean flagVendorname = false;
		if (textSingleProduct.contains("www")) {
			int i = textSingleProduct.indexOf("www");
			sb = new StringBuilder(textSingleProduct);
			if (sb.charAt(i - 1) != ' ') {
				sb.insert(i, " ");
				textSingleProduct = sb.toString();
				subText_Contact = textSingleProduct.substring(i - 140, i + 100);
				// Print the substring after spacing
				// System.out.println(subContactText2);
				return extractVendorName(textSingleProduct, textProductsection,
						subText_Contact, product_list, key);
			} else {
				subText_Contact = textSingleProduct.substring(i - 140, i + 100);
				// Print out the variable that holds a substring of the Vendor
				// Contact + Vendor name
				// Print the substring without spacing
				// System.out.println(subContactText2);
				return extractVendorName(textSingleProduct, textProductsection,
						subText_Contact, product_list, key);
			}
		}
		return product_list;
	}

	// ================= Part 2.2 =================
	// Apply step 3 as a signal flag for vendor name
	Map<String, Product> extractVendorName(String textSingleProduct,
			String textProductsection, String subText_Contact,
			Map<String, Product> product_list, String key) {
		String temp = "";
		String[] productSubtextarr, contctSubtextarr;
		productSubtextarr = textProductsection.split("\n");
		contctSubtextarr = subText_Contact.split("\n");
		for (int i = 0; i < productSubtextarr.length; i++)
			for (int j = 0; j < contctSubtextarr.length; j++) {
				if (productSubtextarr[i].equals(contctSubtextarr[j])) {
					temp = temp + contctSubtextarr[j];
					Date date = new Date();
					product_list.get(key).setVendor_name(temp);
					product_list.get(key).setVendor_contact(subText_Contact);
					product_list.get(key).setDate_of_visit(
							dateFormat.format(date));
					product_list.get(key).setOriginalPage(textSingleProduct);
					// Delet later
					try {
						System.out.println("Product title: "
								+ product_list.get(key).getTitle()
								+ "\nProduct price: "
								+ product_list.get(key).getPrice()
								+ "\nVendor name: "
								+ product_list.get(key).getVendor_name()
								+ "\nDate of visit: "
								+ product_list.get(key).getDate_of_visit());
						for (int d = 0; d < product_list.get(key)
								.getProductRelatedurls().size(); d++) {
							System.out.println("\nUrls: \n"

									+ "- "
									+ product_list.get(key)
											.getProductRelatedurls().get(d));
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Found exception: " + e);
					}

					return product_list;
				}
			}
		if (product_list.get(key).getVendor_name() == null) {
			for (int i = 0; i < productSubtextarr.length; i++)
				for (int j = 0; j < contctSubtextarr.length; j++) {
					try {
						if (productSubtextarr[i].length() > contctSubtextarr[j]
								.length()) {
							if (productSubtextarr[i].matches("(.*)?(?i)"
									+ contctSubtextarr[j] + "(?i)(.*)?")
									&& product_list
											.get(key)
											.getTitle()
											.matches(
													"(.*)?(?i)"
															+ contctSubtextarr[j]
															+ "(?i)(.*)?") == false) {
								temp = temp + productSubtextarr[i];
								Date date = new Date();
								product_list.get(key).setVendor_name(temp);
								product_list.get(key).setVendor_contact(
										subText_Contact);
								product_list.get(key).setDate_of_visit(
										dateFormat.format(date));
								product_list.get(key).setOriginalPage(
										textSingleProduct);
								// Delet later
								try {
									System.out.println("Product title: "
											+ product_list.get(key).getTitle()
											+ "\nProduct price: "
											+ product_list.get(key).getPrice()
											+ "\nVendor name: "
											+ product_list.get(key)
													.getVendor_name()
											+ "\nDate of visit: "
											+ product_list.get(key)
													.getDate_of_visit());
									for (int d = 0; d < product_list.get(key)
											.getProductRelatedurls().size(); d++) {
										System.out
												.println("\nUrls: \n"

														+ "- "
														+ product_list
																.get(key)
																.getProductRelatedurls()
																.get(d));
									}
								} catch (Exception e) {
									// TODO: handle exception
									System.out.println("Found exception: " + e);
								}

								return product_list;
							}
						} else if (productSubtextarr[i].length() < contctSubtextarr[j]
								.length()) {
							if (contctSubtextarr[j].matches(".*(?i)"
									+ productSubtextarr[i] + "(?i).*")
									&& product_list
											.get(key)
											.getTitle()
											.matches(
													"(.*)?(?i)"
															+ contctSubtextarr[j]
															+ "(?i)(.*)?") == false) {
								temp = temp + contctSubtextarr[j];
								Date date = new Date();
								product_list.get(key).setVendor_name(temp);
								product_list.get(key).setVendor_contact(
										subText_Contact);
								product_list.get(key).setDate_of_visit(
										dateFormat.format(date));
								product_list.get(key).setOriginalPage(
										textSingleProduct);
								// Delet later
								try {
									System.out.println("Product title: "
											+ product_list.get(key).getTitle()
											+ "\nProduct price: "
											+ product_list.get(key).getPrice()
											+ "\nVendor name: "
											+ product_list.get(key)
													.getVendor_name()
											+ "\nDate of visit: "
											+ product_list.get(key)
													.getDate_of_visit());
									for (int d = 0; d < product_list.get(key)
											.getProductRelatedurls().size(); d++) {
										System.out
												.println("\nUrls: \n"

														+ "- "
														+ product_list
																.get(key)
																.getProductRelatedurls()
																.get(d));
									}
								} catch (Exception e) {
									// TODO: handle exception
									System.out.println("Found exception: " + e);
								}
								return product_list;
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println("Exception captured: " + e);
					}
				}
		}
		return product_list;

		// System.out.println("Vendor name: " + temp);
		// return vendor name
	}

	/*
	 * ================= Main Part Test=================
	 */
	public static void main(String[] args) {
		SingleProductPageFilter single_prod_filter = new SingleProductPageFilter();
		Map<String, Product> productMap_main = new HashMap<String, Product>();
		ProductListFilter pnt = new ProductListFilter();
		LinkAnalysis link_analyz = new LinkAnalysis();
		Product pro = new Product();
		pro.setTitle("Festartikel Hirschfeld Teufelchen Roc");
		pro.setFake_product_title("Festartikel Hirschfeld Teufelchen Roc");
		pro.setPrice(19.99);// Price
		productMap_main.put("Festartikel Hirschfeld Teufelchen Roc", pro);
		// extract product info.
		single_prod_filter.extractProductSection(productMap_main,
				single_prod_filter.parent_url, single_prod_filter.page,
				single_prod_filter.urls);
	}

	String page = "Suchen\n"
			+ "Geschenkideen\n"
			+ "Warenkorb\n"
			+ "Anmelden\n"
			+ "Suchen\n"
			+ "Taltipp des Tages:\n"
			+ "Ein loser Brillant\n"
			+ "in\n"
			+ "versiegelter\n"
			+ "Geschenkbox\n"
			+ "mit\n"
			+ "Klarsichtdeckel\n"
			+ "nur noch 3 Stück!\n"
			+ "199,00 €\n"
			+ " \n"
			+ "SALE\n"
			+ "139,00 €\n"
			+ "Kategorien\n"
			+ "Kategorien\n"
			+ "Händler\n"
			+ "talTIPP\n"
			+ "Essen, Trinken & Genuss\n"
			+ "Sportartikel\n"
			+ "Fashion & Accessoires\n"
			+ "Kunst & Unterhaltung\n"
			+ "Gutscheine\n"
			+ "mehr\n"
			+ "Festartikel Hirschfeld Teufelchen Rock und Oberteil Gr. 40-42\n"
			+ "Marke: Festartikel Hirschfeld\n"
			+ "verkauft von\n"
			+ "Festartikel Hirschfeld\n"
			+ "Impressum\n"
			+ "·\n"
			+ "AGB & Kundeninformationen\n"
			+ "·\n"
			+ "Widerrufsbelehrung / Muster-Widerrufsformular\n"
			+ "·\n"
			+ "Datenschutzerklärung\n"
			+ "·\n"
			+ "Zahlung & Versand\n"
			+ "19,99 €\n"
			+ "Alle Preisangaben inkl. MwSt.\n"
			+ "zzgl. Versandkosten\n"
			+ "In den Warenkorb\n"
			+ "Sie müssen angemeldet sein, um die Sofortbestellung zu nutzen.\n"
			+ "Anmelden\n"
			+ "Lieferung heute zwischen 18:00 und 21:00 Uhr für nur\n"
			+ "5,95 €\n"
			+ "bei einer Bestellung in den nächsten\n"
			+ "12 Std. : 15 Min.\n"
			+ "Teufelchen Rock und Oberteil Gr. 40-42\n"
			+ "Festartikel Hirschfeld\n"
			+ "Winklerstraße 4242275 Wuppertal\n"
			+ "0202 - 6627240202 - 64 06 57http://www.festartikel-hirschfeld.comMo–Di & Do–Fr 10–18 • Mi & Sa 10–13 • ab Okt. Mo–Fr 10–18 • Sa 10–13 • ab Jan. bis Weiberfastnacht: Mo–Fr 10–20 • Sa 10–18 Uhr\n"
			+ "Weitere Produkte von Festartikel Hirschfeld (Alle anzeigen)\n"
			+ "Perücke John braun Neu Karneval Fasching\n"
			+ "12,99 €\n"
			+ "Festartikel Müller Lampion Ø = 25cm 7...\n"
			+ "14,36 €\n"
			+ "Festartikel Müller Blutimitation Thea...\n"
			+ "2,99 €\n"
			+ "Festartikel Hirschfeld Kunststoff - P...\n"
			+ "1,99 €\n"
			+ "Festartikel Müller Ein Paar Engelsflü...\n"
			+ "8,99 €\n"
			+ "Festartikel Müller Aqua Malkasten Kin...\n"
			+ "11,99 €\n"
			+ "Festartikel Müller echte Wimpern schw...\n"
			+ "5,49 €\n"
			+ "Festartikel Müller Perücke Marilyn we...\n"
			+ "14,99 €\n"
			+ "Festartikel Müller Afro Perücke schwa...\n"
			+ "9,99 €\n"
			+ "Festartikel Müller Perücke Oma Grossm...\n"
			+ "13,99 €\n"
			+ "Festartikel Müller Perücke Esmeralda ...\n"
			+ "12,99 €\n"
			+ "Festartikel Hirschfeld Kunststoff Pla...\n"
			+ "4,99 €\n"
			+ "Festartikel Müller Rasta Mädchen schw...\n"
			+ "15,99 €\n"
			+ "Festartikel Müller Raster Perücke GoG...\n"
			+ "23,50 €\n"
			+ "Festartikel Müller Perücke Arabella l...\n"
			+ "14,99 €\n"
			+ "Festartikel Hirschfeld Kunststoff - P...\n"
			+ "9,50 €\n"
			+ "Festartikel Hirschfeld Kunststoff - P...\n"
			+ "1,99 €\n"
			+ "Festartikel Hirschfeld Kunststoff - P...\n"
			+ "6,50 €\n"
			+ "Festartikel Hirschfeld Kunststoff - P...\n"
			+ "8,99 €\n"
			+ "Festartikel Hirschfeld Kunststoff - P...\n"
			+ "4,99 €\n"
			+ "Festartikel Müller 35x50cm Jutesack m...\n"
			+ "2,99 €\n"
			+ "Auf atalanda kaufen Einwohner in Wuppertal bei ihren lokalen Händlern online ein – die Lieferung wird dann wenn möglich noch am gleichen Tag geliefert (»Same Day Delivery«) oder der Kunde kann sich die Ware selbst abholen (»Click & Collect«). In Wuppertal gibt es vor allem eine reichhaltige Auswahl an\n"
			+ "Nahrungsmittel, Getränke & Tabak\n"
			+ ".\n"
			+ "Sportartikel\n"
			+ ".\n"
			+ "Bekleidung & Accessoires\n"
			+ ".\n"
			+ "Kunst & Unterhaltung\n"
			+ ",\n"
			+ "Natürlich finden Sie hier auch viele Sonderangebote Ihrer lokalen Händler.\n"
			+ "Alle Preise inkl. der gesetzl. MwSt. Die durchgestrichenen Preise entsprechen dem bisherigen Preis auf atalanda bzw. im stationären Geschäft.\n"
			+ "Hinweis zur Streitschlichtung:\n"
			+ "Die EU-Kommission stellt zum 15.2.2016 eine Plattform zur Online-Streitbeilegung bereit. Diese Plattform ist unter folgendem Link erreichbar: http://ec.europa.eu/consumers/odr/\n"
			+ "Newsletter ihrer Händler\n"
			+ "in Wuppertal\n"
			+ "Neue Produkte, Angebote und Neuigkeiten Ihrer Händler\n"
			+ "in Wuppertal\n"
			+ "einfach per Email erhalten! Wir geben Ihre Daten selbstverständlich nicht weiter. Im Schnitt 1x pro Monat. Abmelden ist jederzeit möglich.\n"
			+ "Stadt wählen!AachenAalenAchernAchim b. BremenAhausAhlenAhrensburgAichachAlbstadtAlfeld/LeineAlfterAlsdorf/RheinlandAltenburgAmbergAndernachAnnaberg-BuchholzAnsbachApoldaArnsbergArnstadtAschaffenburgAscherslebenAttendornAuerbach/VogtlandAugsburgAurichBacknangBad HarzburgBad HersfeldBad Homburg v.d. HöheBad HonnefBad KissingenBad KreuznachBad MergentheimBad NauheimBad Neuenahr-AhrweilerBad OeynhausenBad OldesloeBad PyrmontBad RappenauBad SalzuflenBad Soden am TaunusBad VilbelBad ZwischenahnBaden-BadenBaesweilerBalingenBambergBarsinghausenBaunatalBautzenBayreuthBeckumBedburgBensheimBergheim/ErftBergisch GladbachBergkamenBerlinBernau bei BerlinBernburg (Saale)Biberach an der RißBielefeldBietigheim-BissingenBingen am RheinBitterfeld-WolfenBlankenburg (Harz)Blankenfelde-MahlowBlieskastelBöblingenBocholtBochumBonnBorken/WestfalenBorna b. LeipzigBornheim/RheinBörßumBottropBramscheBrandenburgBraunschweigBremenBremerhavenBrettenBrilonBruchköbelBruchsalBrühl/RheinlandBuchholz/NordheideBückeburgBüdingenBühlBündeBürenBurg b. MagdeburgBurgdorf (Hannover)BurgwedelButzbachBuxtehudeCalwCastrop-RauxelCelleChemnitzCloppenburgCoburgCoesfeldCoswig b. DresdenCottbusCrailsheimCrimmitschauCuxhavenDachauDarmstadtDattelnDeggendorfDelbrückDelitzschDelmenhorstDessau-RoßlauDetmoldDietzenbachDillenburgDillingen/SaarDinslakenDitzingenDöbelnDonaueschingenDormagenDorstenDortmundDreieichDresdenDuderstadtDuisburgDülmenDürenDüsseldorfEberswaldeEckernfördeEdewechtEhingen/DonauEinbeckEisenachEisenhüttenstadtEislingen/FilsEllwangen/JagstElmshornElsdorf/RheinlandEmdenEmmendingenEmmerich am RheinEmsdettenEngerEnnepetalEppingenErdingErftstadtErfurtErkelenzErkrathErlangenEschbornEschweilerEspelkampEssen/RuhrEsslingen am NeckarEttlingenEuskirchenFalkenseeFellbachFilderstadtFlensburgFlörsheim am MainForchheim/OberfrankenForst (Lausitz)FrankenthalFrankfurt (Oder)Frankfurt am MainFrechenFreiberg/SachsenFreiburg im BreisgauFreisingFreitalFreudenstadtFriedberg/BayernFriedberg/HessenFriedrichsdorfFriedrichshafenFriesoytheFröndenberg/RuhrFuldaFürstenfeldbruckFürstenwalde/SpreeFürthGaggenauGanderkeseeGarbsenGardelegenGarmisch-PartenkirchenGautingGeesthachtGeilenkirchenGeislingen an der SteigeGeldernGelnhausenGelsenkirchenGeorgsmarienhütteGeraGeretsriedGermeringGermersheimGersthofenGesekeGevelsbergGießenGifhornGladbeckGlauchauGochGöppingenGörlitzGoslarGothaGöttingenGreifswaldGreizGreven/WestfalenGrevenbroichGriesheimGrimmaGronau/WestfalenGroß-GerauGroß-UmstadtGummersbachGüstrowGüterslohHaanHagenHalberstadtHalle (Saale)Halle/WestfalenHaltern am SeeHamburgHamelnHammHamminkelnHanauHannoverHannoversch MündenHaren/EmsHarsewinkelHaßlochHattersheim am MainHattingenHeideHeidelbergHeidenheim an der BrenzHeilbronnHeiligenhausHeinsbergHelmstedtHemerHennef/SiegHennigsdorfHenstedt-UlzburgHeppenheim/BergstraßeHerborn/HessenHerdeckeHerfordHerneHerrenbergHertenHerzogenaurachHerzogenrathHildenHildesheimHockenheimHofHofheim am TaunusHohen NeuendorfHolzmindenHomburgHorb am NeckarHöxterHoyerswerdaHückelhovenHürthHusum/NordseeIbbenbürenIdar-ObersteinIdsteinIlmenauIngelheim am RheinIngolstadtIserlohnIsernhagenItzehoeJenaJüchenJülichKaarstKaiserslauternKaltenkirchenKamenKamp-LintfortKarbenKarlsruheKasselKaufbeurenKehlKelkheim/TaunusKempenKempten (Allgäu)Kerpen/Rhein-ErftKevelaerKielKirchheim unter TeckKitzingenKleve/NiederrheinKoblenzKölnKönigs WusterhausenKönigsbrunnKönigswinterKonstanzKorbachKornwestheimKorschenbroichKöthen (Anhalt)KrefeldKreuztalKulmbachLaatzenLage/LippeLahr/SchwarzwaldLampertheimLandau/PfalzLandsberg a. LechLandshutLangen/HessenLangenfeld/RheinlandLangenhagenLauf a.d. PegnitzLeer/OstfrieslandLehrteLeichlingen/RheinlandLeimen/BadenLeinfelden-EchterdingenLeipzigLemgoLengerich/WestfalenLennestadtLeonberg/WürttembergLeutkirch im AllgäuLeverkusenLichtenfels/BayernLimbach-OberfrohnaLimburg a.d. LahnLindau/BodenseeLindlarLingen/EmsLippstadtLohmarLöhneLohne/OldenburgLörrachLübbeckeLübeckLuckenwaldeLüdenscheidLüdinghausenLudwigsburg/WürttembergLudwigsfeldeLudwigshafenLüneburgLünenLutherstadt EislebenLutherstadt WittenbergMagdeburgMaintalMainzMannheimMarburgMarkkleebergMarl/WestfalenMarsbergMechernichMeckenheim/RheinMeerbuschMeinerzhagenMeiningenMeißenMelleMemmingenMenden/SauerlandMeppenMerseburgMerzigMeschedeMettmannMetzingenMinden/WestfalenMoersMönchengladbachMonheim am RheinMoormerlandMörfelden-WalldorfMosbachMössingenMühlackerMühlhausen/ThüringenMühlheim am MainMülheim an der RuhrMünchenMünsterNagoldNaumburg (Saale)NeckarsulmNetphenNettetalNeu WulmstorfNeubrandenburgNeuburg a.d. DonauNeu-IsenburgNeukirchen-VluynNeumarkt i.d. OberpfalzNeumünsterNeunkirchen/SaarNeunkirchen-SeelscheidNeuruppinNeusäßNeussNeustadt am RübenbergeNeustadt/WeinstraßeNeustrelitzNeu-UlmNeuwiedNiederkasselNienburg/WeserNordenNordenhamNorderstedtNordhausenNordhornNortheimNürnbergNürtingenOberhausenObertshausenOberursel/TaunusOeldeOer-ErkenschwickOffenbach/MainOffenburgÖhringenOlchingOldenburgOlpeOranienburgOschersleben/BodeOsnabrückOsterholz-ScharmbeckOsterode am HarzOstfildernOttobrunnOverathPaderbornPapenburgPassauPeinePetershagen/WeserPfaffenhofen a.d. IlmPforzheimPfungstadtPinnebergPirmasensPirnaPlauenPlettenbergPorta WestfalicaPotsdamPrenzlauPulheimQuedlinburgQuickborn/Kr. PinnebergRadebeulRadevormwaldRadolfzell am BodenseeRastattRastedeRathenowRatingenRavensburgRecklinghausenReesRegensburgReichenbach im VogtlandReinbekRemscheidRemseck am NeckarRendsburgReutlingenRheda-WiedenbrückRheinbachRheinbergRheineRheinfelden/BadenRheinstettenRiedstadtRiesaRietbergRintelnRödermarkRodgauRonnenbergRosenheimRösrathRostockRotenburg/WümmeRoth/MittelfrankenRottenburg am NeckarRottweilRudolstadtRüsselsheimSaalfeld/SaaleSaarbrückenSaarlouisSalzgitterSalzkottenSalzwedelSangerhausenSankt AugustinSankt IngbertSankt WendelSchleswigSchloß Holte-StukenbrockSchmallenbergSchönebeck (Elbe)Schorndorf/WürttembergSchortensSchrambergSchwabachSchwäbisch GmündSchwäbisch HallSchwandorfSchwedt/OderSchweinfurtSchwelmSchwerinSchwerteSchwetzingenSeelzeSeesenSeevetalSehndeSeligenstadtSelmSenden/IllerSenden/WestfalenSenftenbergSiegburgSiegenSindelfingenSingen/HohentwielSinsheimSoestSolingenSoltauSondershausenSonnebergSonthofenSpeyerSprembergSpringeSprockhövelStadeStadtallendorfStadthagenStadtlohnStarnbergStaßfurtSteinfurtStendalStolberg/RheinlandStralsundStraubingStrausbergStuhrStutenseeStuttgartSuhlSundern/SauerlandSykeTaunussteinTeltowTönisvorstTraunreutTrierTroisdorfTübingenTuttlingenÜbach-PalenbergÜberlingenUelzenUetzeUlmUnnaUnterhachingUnterschleißheimVaihingen an der EnzVarelVaterstettenVechtaVelbertVerden/AllerVerlVersmoldViernheimViersenVillingen-SchwenningenVoerde/NiederrheinVölklingenVredenWachtbergWaghäuselWaiblingenWaldkirchWaldkraiburgWaldshut-TiengenWallenhorstWalsrodeWaltropWandlitzWangen im AllgäuWarburgWaren/MüritzWa...";
	String parent_url = "https://atalanda.com/wuppertal/products/teufelchen-rock-und-oberteil-gr-40-42?abs_pos=129&total=357";
	String urls = "https://atalanda.com/wuppertal/contact, https://atalanda.com/wuppertal/products/perucke-john-braun-neu-karneval-fasching, https://atalanda.com/assets/store/all-295f2cd173492b8d99a5ede23b0ca0b3.css, https://atalanda.com/wuppertal, https://atalanda.com/wuppertal/t/sportartikel, https://atalanda.com/spree/products/104819/product/2052-Blutimitation-Theaterblut-Helloween-Party-.jpg?1434984734, https://atalanda.com/wuppertal/login, https://atalanda.com/spree/products/87146/product/86369.jpg?1418807896, https://maps.google.com/maps/api/staticmap?markers=color%3Ared%7Clabel%3AA%7CWinklerstra%C3%9Fe%2042%2C%2042275%20Wuppertal%2C%20Deutschland&size=350x188&zoom=15, https://atalanda.com/spree/products/105335/product/2323-Afro-Peruecke-schwarz-Karneval-Fasching.jpg?1435075217, https://atalanda.com/wuppertal/presse, https://atalanda.com/wuppertal/products/kunststoff-plastik-becher-weiss-300ml-80-stuck-made-in-europe, https://atalanda.com/wuppertal/products/teufelchen-rock-und-oberteil-gr-40-42, https://atalanda.com/wuppertal/t/nahrungsmittel-getranke-and-tabak, https://atalanda.com/wuppertal/ideen, https://atalanda.com/wuppertal/products/kunststoff-plastik-messer-essbesteck-100-stuck-made-in-europa, https://atalanda.com/spree/products/104825/product/2259.jpg?1434984740, https://atalanda.com/wuppertal/products/kunststoff-plastik-pommesgabeln-essbesteck-2000-stuck, https://atalanda.com/wuppertal/products/raster-perucke-gogo-schwarz-braun-karneval-fasching, https://atalanda.com/spree/city_zones/wuppertal/logo/standard/talmarkt_logo.png?1446844568, https://atalanda.com/spree/products/104821/product/2062-2062a.jpg?1434984736, https://atalanda.com/spree/products/105338/product/M30020001.jpg?1435075220, https://atalanda.com/wuppertal/, https://atalanda.com/spree/products/105340/product/2374.jpg?1435075221, http://www.festartikel-hirschfeld.com/, https://atalanda.com/wuppertal/privacy, https://atalanda.com/wuppertal/categories, https://atalanda.com/spree/products/105375/large/102433-M119203.jpg?1435075346, https://atalanda.com/wolfenbuettel, https://atalanda.com/wuppertal/t/gutscheine, https://atalanda.com/wuppertal/jobs, https://atalanda.com/wuppertal/products/kunststoff-plastik-becher-weiss-400ml-75-stuck-made-in-europe, https://atalanda.com/wuppertal/products/35x50cm-jutesack-mit-kordel-weihnachten-dekoration, https://atalanda.com/heilbronn, https://atalanda.com/wuppertal/vendors, https://atalanda.com/spree/products/105339/product/2373-Raster-Peruecke-GoGo-schwarz-braun-Karneval-Fas.jpg?1435075220, https://atalanda.com/wuppertal/products/afro-perucke-schwarz-karneval-fasching, https://atalanda.com/wuppertal/offers, https://atalanda.com/spree/products/104836/product/2522.jpg?1434984749, http://www.buylocal.de/, https://atalanda.com/spree/products/104832/product/2513.jpg?1434984746, http://ec.europa.eu/consumers/odr/, https://atalanda.com/spree/products/104824/product/2233.jpg?1434984739, https://atalanda.com/assets/footer/buy_local-01b954977a7bcea688f5343cff782ba6.png, https://atalanda.com/wuppertal/cart, https://atalanda.com/wuppertal/products/blutimitation-theaterblut-helloween-party, https://atalanda.com/spree/products/104823/product/2073.jpg?1434984738, https://atalanda.com/spree/products/104830/product/2479.jpg?1434984745, https://atalanda.com/wuppertal/t/bekleidung-and-accessoires, https://atalanda.com/wuppertal/t/kunst-and-unterhaltung, https://atalanda.com/assets/atalanda_logo-0a624f738323134dc60c091813b5be94.png, https://atalanda.com/wuppertal/agb, https://atalanda.com/wuppertal/products/perucke-arabella-lockig-schwarz-karneval-fasching, https://atalanda.com/spree/products/104829/product/2478.jpg?1434984744, https://atalanda.com/wuppertal/vendors/festartikel-hirschfeld/info, https://atalanda.com/wuppertal/products/aqua-malkasten-kindergeburtstag-party-karneval, https://atalanda.com/assets/footer/innovationspreis-26939bef18000472bd4d56b31ce71090.png, https://atalanda.com/assets/footer/dhl-eff51781f983b46c4a21fd00d6b2513b.png, https://atalanda.com/spree/products/104812/product/2019.jpg?1434984727, https://atalanda.com/wuppertal/cityinfos, https://atalanda.com/wuppertal/products/ein-brillant-in-versiegelter-geschenkbox-mit-klarsichtdeckel-1, https://atalanda.com/wuppertal/products/ein-paar-engelsflugel-rot-engel-engelchen, https://atalanda.com/wuppertal/products/perucke-esmeralda-mit-haar-und-stirnband-karneval, https://atalanda.com/wuppertal/products/lampion-o-equals-25cm-7-stuck-im-set-st-martin-laternen, https://atalanda.com/assets/favicon-9e3455dda4c33dfd8b60fc3550dbfcaa.ico, https://atalanda.com/spree/products/105336/product/2346-Peruecke-Oma-Grossmutter-Silber-grau-Karneval_1.jpg?1435075218, https://atalanda.com/wuppertal/products/perucke-oma-grossmutter-silber-grau-karneval-fasching, https://atalanda.com/spree/products/105337/product/2348-Peruecke-Esmeralda-mit-Haar-und-Stirnband-Karne.jpg?1435075219, http://www.dhl.de/de/paket/geschaeftskunden/ab-200-pakete/kurier.html, https://atalanda.com/assets/footer/hiwi-4b5430a9013f10575b02ebd3c5218990.jpg, https://atalanda.com/spree/products/104838/product/2701-35x50cm-Jutesack-mit-Kordel-Weihnachten-Dekorat.jpg?1434984751, https://atalanda.com/wuppertal/products/kunststoff-plastik-sektglas-fuss-24-stuck-klar-sektflote, https://atalanda.com/spree/products/105334/product/2301-Peruecke-Marilyn-weiss-blond-Karneval-Fasching.jpg?1435075216, https://atalanda.com/wuppertal/products/kunststoff-plastik-ruhrstabchen-essbesteck-1000-stuck-kaffeeruhrstabchen-made-in-europe, https://atalanda.com/assets/footer/paypal-fcaa3198be81a4e291321130ab11056d.png, https://atalanda.com/assets/footer/lastschrift-e847290d1ca619667329ceaadf948262.png, https://atalanda.com/wuppertal/products/echte-wimpern-schwarz-ein-paar-schminke-karneval, https://atalanda.com/wuppertal/impressum, https://atalanda.com/wuppertal/vendors/festartikel-hirschfeld, https://atalanda.com/wuppertal/products/rasta-madchen-schwarz-perucke-m-bunten-perlen-karneval, https://atalanda.com/goeppingen, https://atalanda.com/wuppertal/products/kunststoff-plastik-loffel-essbesteck-100-stuck-made-in-europa, https://atalanda.com/spree/products/104827/product/2389.jpg?1434984742, https://atalanda.com/wuppertal/pressematerial, https://atalanda.com/wuppertal/stylesheets/current_city_style-1c321a52fbbd9ab7f0900ae09c195380e0da77a8.css, https://atalanda.com/spree/products/104831/product/2512.jpg?1434984746, https://atalanda.com/attendorn, https://atalanda.com/spree/products/87439/product/Brilliant_0_12ct.jpg?1419007830, https://atalanda.com/wuppertal/products/teufelchen-rock-und-oberteil-gr-40-42?abs_pos=129&total=357, https://atalanda.com/wuppertal/products/perucke-marilyn-weiss-blond-karneval-fasching";
}
