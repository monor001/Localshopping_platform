package localshoppingplatform.server.crawlertextpreprocess;

import java.util.Iterator;

/**
 * 
 * @author Mohamed Elsayed <eng.moh.nas@gmail.com>
 * 
 */

abstract class IterableText implements Iterator<String>, Iterable<String> {
	public Iterator<String> iterator() {
		return this;
	}
}
