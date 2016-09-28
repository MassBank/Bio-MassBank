/*******************************************************************************
 *
 * Copyright (C) 2014 MassBank Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *******************************************************************************
 *
 * ResponseCache.java
 *
 * ver 1.0.0 Feb. 3, 2014
 *
 ******************************************************************************/
package net.massbank.core;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ResponseCache {
	private Cache cache = null;

	/**
	 * constructor
	 */
	public ResponseCache() {
		String configFilePath = FileUtil.getAppRootPath() + "massbank_cache.xml";
		CacheManager manager = CacheManager.create(configFilePath);
		this.cache = manager.getCache("massbank");
	}

	/**
	 * get value
	 */
	public String getValue(String key) {
		if ( this.cache.isKeyInCache(key) ) {
			Element element = this.cache.get(key);
			return (String)element.getObjectValue();
		}
		else {
			return null;
		}
	}

	/**
	 * set value
	 */
	public void setValue(String key, String value) {
		this.cache.put( new Element(key, value) );
	}

	/**
	 * clear cache
	 */
	public void clear() {
		this.cache.removeAll();
	}

}
