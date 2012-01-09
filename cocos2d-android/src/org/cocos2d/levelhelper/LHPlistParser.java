package org.cocos2d.levelhelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.utils.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Plist parser.
 * Supports:
 *  - string     -> String
 *  - integer    -> Integer
 *  - real       -> Double
 *  - date       -> Date
 *  - true/false -> Boolean
 *  - data       -> byte[] 
 *  - dict       -> HashMap<String, Object>
 *  - array      -> ArrayList<Object>
 */

public class LHPlistParser extends DefaultHandler {

	public static LHObject parse(String filename) {
		try {
			InputStream in = CCDirector.theApp.getAssets().open(filename);
			return parsePlist(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static LHObject parsePlist(InputStream in) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			LHPlistParser handler = new LHPlistParser();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in), 8192);
			parser.parse(new InputSource(reader), handler);
			return handler.rootDict;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// type is used in characters()
	private static final int TYPE_UNKNOWN = 0;
	private static final int TYPE_KEY = 1;
	private static final int TYPE_STRING = 2;
	private static final int TYPE_INTEGER = 3;
	private static final int TYPE_DATA = 4;
	private static final int TYPE_DATE = 5;
	private static final int TYPE_REAL = 6;

	// for tag name
	private static final String EL_KEY = "key";
	private static final String EL_STRING = "string";
	private static final String EL_INTEGER = "integer";
	private static final String EL_DATA = "data";
	private static final String EL_DATE = "date";
	private static final String EL_REAL = "real";
	private static final String EL_TRUE = "true";
	private static final String EL_FALSE = "false";

	private static final String EL_DICT = "dict";
	private static final String EL_ARRAY = "array";

	private static final int COLL_UNKNOWN = 0;
	private static final int COLL_DICT = 1;
	private static final int COLL_ARRAY = 2;

	// root hashmap which should be returned finally
	private LHObject rootDict;

	private String currentKey;

	private int currentElement = TYPE_UNKNOWN;

	private int currentCollectionType = COLL_UNKNOWN;

	private LHObject currentCollection;

	private ArrayList<LHObject> depthStack = new ArrayList<LHObject>();

	private LHPlistParser() {
	}

	private void setupCurrentCollection(LHObject coll) {
		currentCollection = coll;
		if (coll == null) {
			currentCollectionType = COLL_UNKNOWN;
		} else if (LHObject.TYPE_LH_DICT == coll.type()) {
			currentCollectionType = COLL_DICT;
		} else { // array
			currentCollectionType = COLL_ARRAY;
		}
	}

	private void depthUp(LHObject newcoll) {
		addToCollection(newcoll);
		depthStack.add(newcoll);
		setupCurrentCollection(newcoll);
	}

	private void depthDown() {
		int s = depthStack.size();
		if (s > 0) {
			depthStack.remove(s - 1);

			LHObject currCol = null;
			if (s > 1) {
				currCol = depthStack.get(s - 2);
			}
			setupCurrentCollection(currCol);
		}
	}

	private void addToCollection(LHObject obj) {
		switch (currentCollectionType) {
		case COLL_DICT:
			currentCollection.dictValue().put(currentKey, obj);
			break;
		case COLL_ARRAY:
			currentCollection.arrayValue().add(obj);
			break;
		case COLL_UNKNOWN: // first call could be array or dict
			rootDict = obj;
			break;
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equals(EL_KEY)) {
			currentElement = TYPE_KEY;
		} else if (localName.equals(EL_STRING)) {
			currentElement = TYPE_STRING;
		} else if (localName.equals(EL_INTEGER)) {
			currentElement = TYPE_INTEGER;
		} else if (localName.equals(EL_REAL)) {
			currentElement = TYPE_REAL;
		} else if (localName.equals(EL_DATA)) {
			currentElement = TYPE_DATA;
		} else if (localName.equals(EL_DATE)) {
			currentElement = TYPE_DATE;
		} else if (localName.equals(EL_TRUE)) {
			LHObject object = new LHObject(true);
			addToCollection(object);
		} else if (localName.equals(EL_FALSE)) {
			LHObject object = new LHObject(false);
			addToCollection(object);
			// collections
		} else if (localName.equals(EL_DICT)) {
			depthUp(new LHObject(new HashMap<String, LHObject>()));
		} else if (localName.equals(EL_ARRAY)) {
			depthUp(new LHObject(new ArrayList<LHObject>()));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		switch (currentElement) {
		case TYPE_KEY:
			currentKey = characterData.toString().trim();
			break;
		case TYPE_STRING:
			addToCollection(new LHObject(characterData.toString().trim()));
			break;
		case TYPE_DATA:
			try {
				addToCollection(new LHObject(Base64.decode(characterData
						.toString().trim())));
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		case TYPE_DATE:
			try {
				addToCollection(new LHObject(dateFormat.parse(characterData
						.toString().trim())));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
		case TYPE_INTEGER:
			addToCollection(new LHObject(Integer.parseInt(characterData
					.toString().trim())));
			break;
		case TYPE_REAL:
			addToCollection(new LHObject(Float.parseFloat(characterData
					.toString().trim())));
			break;
		default:
			break;
		}

		currentElement = TYPE_UNKNOWN;
		characterData.setLength(0);

		if (localName.equals(EL_DICT) || localName.equals(EL_ARRAY)) {
			depthDown();
		}
	}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss'Z'");

	private StringBuilder characterData = new StringBuilder();

	@Override
	public void characters(char[] _chars, int _start, int _len) {

		characterData.append(_chars, _start, _len);
	}

}
