package org.cocos2d.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.cocos2d.types.CGPoint;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class GB2ShapeCache {

	private static final int MAX_POLYGON_VERTICES = 8;
	private static GB2ShapeCache shapeCache;

	public static GB2ShapeCache sharedShapeCache() {
		if (shapeCache == null) {
			shapeCache = new GB2ShapeCache();
		}
		return shapeCache;
	}

	private HashMap<String, GBBodyDef> shapeObjects_;
	private float ptmRatio_;

	private GB2ShapeCache() {
		shapeObjects_ = new HashMap<String, GBBodyDef>();
	}

	public void addFixturesToBody(Body body, String shape) {
		GBBodyDef so = shapeObjects_.get(shape);
		assert (so != null);

		ArrayList<GBFixtureDef> fix = so.fixtures;
		int size = fix.size();
		GBFixtureDef def;
		for (int i = 0; i < size; i++) {
			def = fix.get(i);
			if (def != null) {
				body.createFixture(def.fixture);
			}
		}
	}

	public CGPoint anchorPointForShape(String shape) {
		GBBodyDef bodyDef = shapeObjects_.get(shape);
		assert (bodyDef != null);
		return bodyDef.anchorPoint;
	}

	@SuppressWarnings("unchecked")
	public void addShapesWithFile(String plist) {
		HashMap<String, Object> dictionary = PlistParser.parse(plist);
		HashMap<String, Object> metadataDict = (HashMap<String, Object>) dictionary
				.get("metadata");
		int format = (Integer) metadataDict.get("format");
		ptmRatio_ = ((Number) metadataDict.get("ptm_ratio")).floatValue();
		assert (format == 1) : "Format not supported";

		HashMap<String, Object> bodyDict = (HashMap<String, Object>) dictionary
				.get("bodies");
		for (Entry<String, Object> entry : bodyDict.entrySet()) {
			// get the body data
			String bodyName = entry.getKey();
			HashMap<String, Object> bodyData = (HashMap<String, Object>) entry
					.getValue();

			// create body object
			GBBodyDef bodyDef = new GBBodyDef();

			bodyDef.anchorPoint = stringToCGPoint((String) bodyData
					.get("anchorpoint"));

			// iterate through the fixtures
			ArrayList<?> fixtureList = (ArrayList<?>) bodyData.get("fixtures");
			int size = fixtureList.size();
			for (int i = 0; i < size; i++) {
				HashMap<String, Object> fixtureData = (HashMap<String, Object>) fixtureList
						.get(i);

				FixtureDef basicData = new FixtureDef();

				basicData.filter.categoryBits = ((Number) fixtureData
						.get("filter_categoryBits")).shortValue();
				basicData.filter.maskBits = ((Number) fixtureData
						.get("filter_maskBits")).shortValue();
				basicData.filter.groupIndex = ((Number) fixtureData
						.get("filter_groupIndex")).shortValue();
				basicData.friction = ((Number) fixtureData.get("friction"))
						.floatValue();
				basicData.density = ((Number) fixtureData.get("density"))
						.floatValue();
				basicData.restitution = ((Number) fixtureData
						.get("restitution")).floatValue();
				basicData.isSensor = (Boolean) fixtureData.get("isSensor");
				int callbackData = 0;
				if (fixtureData.containsKey("userdataCbValue")) {
					callbackData = (Integer) fixtureData.get("userdataCbValue");
				}
				String fixtureType = (String) fixtureData.get("fixture_type");

				// read polygon fixtures. One convave fixture may consist of
				// several convex polygons
				if ("POLYGON".equals(fixtureType)) {
					ArrayList<?> polygonsArray = (ArrayList<?>) fixtureData
							.get("polygons");
					for (Object object : polygonsArray) {
						ArrayList<?> polygonArray = (ArrayList<?>) object;
						GBFixtureDef fix = new GBFixtureDef();
						fix.fixture = basicData; // copy basic data
						fix.callbackData = callbackData;
						PolygonShape polyshape = new PolygonShape();
						int verticesCount = polygonArray.size();
						assert (verticesCount <= MAX_POLYGON_VERTICES);
						Vector2[] vertices = new Vector2[polygonArray.size()];
						for (int j = 0; j < verticesCount; j++) {
							Object pointString = polygonArray.get(j);
							CGPoint offset = stringToCGPoint((String) pointString);
							vertices[j] = new Vector2(offset.x / ptmRatio_,
									offset.y / ptmRatio_);
						}
						polyshape.set(vertices);
						fix.fixture.shape = polyshape;

						bodyDef.fixtures.add(fix);
					}
				} else if ("CIRCLE".equals(fixtureType)) {
					GBFixtureDef fix = new GBFixtureDef();
					fix.fixture = basicData; // copy basic data
					fix.callbackData = callbackData;

					HashMap<String, Object> circleData = (HashMap<String, Object>) fixtureData
							.get("circle");

					CircleShape circleShape = new CircleShape();
					circleShape.setRadius(((Number) circleData.get("radius"))
							.floatValue() / ptmRatio_);
					if (fixtureData.containsKey("center")) {
						CGPoint p = stringToCGPoint((String) fixtureData
								.get("center"));
						circleShape.setPosition(new Vector2(p.x / ptmRatio_,
								p.y / ptmRatio_));
					}
					fix.fixture.shape = circleShape;

					bodyDef.fixtures.add(fix);
				}
			}

			// add the body element to the hash
			shapeObjects_.put(bodyName, bodyDef);
		}
	}

	private CGPoint stringToCGPoint(String str) {
		String theString = str;
		theString = theString.replace("{ ", "");
		theString = theString.replace(" }", "");
		String[] array = theString.split(",");
		return CGPoint.make(Float.parseFloat(array[0]),
				Float.parseFloat(array[1]));
	}

	public float ptmRatio() {
		return ptmRatio_;
	}

	class GBFixtureDef {
		FixtureDef fixture;
		int callbackData;
	}

	class GBBodyDef {
		ArrayList<GBFixtureDef> fixtures;
		CGPoint anchorPoint;

		GBBodyDef() {
			fixtures = new ArrayList<GBFixtureDef>();
		}
	}
}
