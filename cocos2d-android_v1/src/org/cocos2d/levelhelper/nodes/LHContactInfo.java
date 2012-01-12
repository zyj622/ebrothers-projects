package org.cocos2d.levelhelper.nodes;

import org.cocos2d.nodes.CCNode;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.Manifold;

public class LHContactInfo {
	public Body bodyA; // week ptr;
	public Body bodyB; // week ptr;
	public Contact contact; // available at both pre and post solve
	public Manifold oldManifold;// available at pre solve - else is nil
	public ContactImpulse impulse; // available at post solve - else is nil

	public static LHContactInfo contactInfo(Body _bodyA, Body _bodyB,
			Contact _contact, Manifold _manifold, ContactImpulse _impulse) {
		LHContactInfo pobContact = new LHContactInfo();
		pobContact.initWithInfo(_bodyA, _bodyB, _contact, _manifold, _impulse);
		return pobContact;
	}

	public boolean initWithInfo(Body _bodyA, Body _bodyB, Contact _contact,
			Manifold _manifold, ContactImpulse _impulse) {
		bodyA = _bodyA;
		bodyB = _bodyB;
		contact = _contact;
		oldManifold = _manifold;
		impulse = _impulse;
		return true;
	}

	// //////////////////////////////////////////////////////////////////////////////
	public LHSprite spriteA() {
		CCNode spr = (CCNode) bodyA.getUserData();
		if (null != spr) {
			if (spr instanceof LHSprite) {
				return (LHSprite) spr;
			}
		}
		return null;
	}

	public LHSprite spriteB() {
		CCNode spr = (CCNode) bodyB.getUserData();
		if (null != spr) {
			if (spr instanceof LHSprite) {
				return (LHSprite) spr;
			}
		}
		return null;
	}
	// //////////////////////////////////////////////////////////////////////////////

}
