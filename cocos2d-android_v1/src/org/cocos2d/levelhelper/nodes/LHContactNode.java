package org.cocos2d.levelhelper.nodes;

import java.util.HashMap;

import org.cocos2d.nodes.CCNode;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class LHContactNode extends CCNode {
	HashMap<Integer, HashMap<Integer, LHContactNodeInfo>> preCollisionMap;
	HashMap<Integer, HashMap<Integer, LHContactNodeInfo>> postCollisionMap;

	private ContactListener lhContactListener = new ContactListener() {
		@Override
		public void beginContact(Contact contact) {

		}

		@Override
		public void endContact(Contact contact) {

		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
			preSolve(contact, oldManifold);
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
			postSolve(contact, impulse);
		}
	};

	boolean initContactNodeWithWorld(World world) {
		if (world == null)
			return false;
		world.setContactListener(lhContactListener);
		return true;
	}

	public static LHContactNode contactNodeWithWorld(World world) {
		LHContactNode pobNode = new LHContactNode();
		pobNode.initContactNodeWithWorld(world);
		return pobNode;
	}

	public LHContactNode() {
		preCollisionMap = new HashMap<Integer, HashMap<Integer, LHContactNodeInfo>>();
		postCollisionMap = new HashMap<Integer, HashMap<Integer, LHContactNodeInfo>>();
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void registerPreColisionCallbackBetweenTagA(int tagA, int tagB,
			ContactNodeNotifier _notifier) {
		HashMap<Integer, LHContactNodeInfo> tableA = preCollisionMap.get(tagA);
		if (tableA == null) {
			LHContactNodeInfo info = LHContactNodeInfo.contactInfoWithTag(tagB,
					_notifier);
			HashMap<Integer, LHContactNodeInfo> map = new HashMap<Integer, LHContactNodeInfo>();
			map.put(tagB, info);
			preCollisionMap.put(tagA, map);
		} else {
			LHContactNodeInfo info = LHContactNodeInfo.contactInfoWithTag(tagB,
					_notifier);
			tableA.put(tagB, info);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void cancelPreColisionCallbackBetweenTagA(int tagA, int tagB) {
		HashMap<Integer, LHContactNodeInfo> tableA = preCollisionMap.get(tagA);

		if (null != tableA) {
			tableA.remove(tagB);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void registerPostColisionCallbackBetweenTagA(int tagA, int tagB,
			ContactNodeNotifier _notifier) {
		HashMap<Integer, LHContactNodeInfo> tableA = postCollisionMap.get(tagA);
		if (tableA == null) {
			LHContactNodeInfo info = LHContactNodeInfo.contactInfoWithTag(tagB,
					_notifier);
			HashMap<Integer, LHContactNodeInfo> map = new HashMap<Integer, LHContactNodeInfo>();
			map.put(tagB, info);
			postCollisionMap.put(tagA, map);
		} else {
			LHContactNodeInfo info = LHContactNodeInfo.contactInfoWithTag(tagB,
					_notifier);
			tableA.put(tagB, info);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void cancelPostColisionCallbackBetweenTagA(int tagA, int tagB) {
		HashMap<Integer, LHContactNodeInfo> tableA = postCollisionMap.get(tagA);

		if (null != tableA) {
			tableA.remove(tagB);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void preSolve(Contact contact, Manifold oldManifold) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		CCNode nodeA = (CCNode) bodyA.getUserData();
		CCNode nodeB = (CCNode) bodyB.getUserData();
		HashMap<Integer, LHContactNodeInfo> info = preCollisionMap.get(nodeA
				.getTag());
		if (info != null) {
			LHContactNodeInfo contactInfo = (LHContactNodeInfo) info.get(nodeB
					.getTag());
			if (null != contactInfo) {
				contactInfo.callListenerWithBodyA(bodyA, bodyB, contact,
						oldManifold, null);
			}
		} else {
			info = preCollisionMap.get(nodeB.getTag());
			if (null != info) {
				LHContactNodeInfo contactInfo = (LHContactNodeInfo) info
						.get(nodeA.getTag());
				if (null != contactInfo) {
					contactInfo.callListenerWithBodyA(bodyB, bodyA, contact,
							oldManifold, null);
				}
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////////
	public void postSolve(Contact contact, ContactImpulse impulse) {
		Body bodyA = contact.getFixtureA().getBody();
		Body bodyB = contact.getFixtureB().getBody();
		CCNode nodeA = (CCNode) bodyA.getUserData();
		CCNode nodeB = (CCNode) bodyB.getUserData();
		HashMap<Integer, LHContactNodeInfo> info = preCollisionMap.get(nodeA
				.getTag());
		if (info != null) {
			LHContactNodeInfo contactInfo = info.get(nodeB.getTag());
			if (null != contactInfo) {
				contactInfo.callListenerWithBodyA(bodyA, bodyB, contact, null,
						impulse);
			}
		} else {
			info = preCollisionMap.get(nodeB.getTag());
			if (null != info) {
				LHContactNodeInfo contactInfo = info.get(nodeA.getTag());
				if (null != contactInfo) {
					contactInfo.callListenerWithBodyA(bodyB, bodyA, contact,
							null, impulse);
				}
			}
		}
	}

	public static class LHContactNodeInfo {
		int tagB;
		ContactNodeNotifier notifier;

		LHContactNodeInfo() {
		}

		boolean initcontactInfoWithTag(int _tagB, ContactNodeNotifier _notifier) {
			tagB = _tagB;
			notifier = _notifier;
			return true;
		}

		static LHContactNodeInfo contactInfoWithTag(int tagB,
				ContactNodeNotifier _notifier) {
			LHContactNodeInfo pobInfo = new LHContactNodeInfo();
			pobInfo.initcontactInfoWithTag(tagB, _notifier);
			return pobInfo;
		}

		int getTagB() {
			return tagB;
		}

		public void callListenerWithBodyA(Body A, Body B, Contact contact,
				Manifold oldManifold, ContactImpulse impulse) {
			LHContactInfo info = LHContactInfo.contactInfo(A, B, contact,
					oldManifold, impulse);
			if (notifier != null) {
				notifier.onContactNodeNotify(info);
			}
		}
	}

	public interface ContactNodeNotifier {
		public void onContactNodeNotify(LHContactInfo info);
	}

}
