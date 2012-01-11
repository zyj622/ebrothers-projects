package org.cocos2d.levelhelper.nodes;

import org.cocos2d.nodes.CCNode;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;

public class LHContactNode extends CCNode {
	CCMutableDictionary<int> preCollisionMap;
    CCMutableDictionary<int> postCollisionMap;

    LHContactListener lhContactListener;
	boolean initContactNodeWithWorld(World world){
	    if(world == null)
	        return false;
	    lhContactListener = new LHContactListener();
	    world.setContactListener(lhContactListener);
	    lhContactListener.nodeObject = this;
	    lhContactListener.preSolveSelector = &lhContact_CallPreSolveMethod;
	    lhContactListener.postSolveSelector = &lhContact_CallPostSolveMethod;

	    return true;
	}
	LHContactNode contactNodeWithWorld(World world){
	    
	    LHContactNode pobNode = new LHContactNode();
	    if (pobNode != null && pobNode.initContactNodeWithWorld(world))
	    {
	        return pobNode;
	    }
	    return null;
	}
	////////////////////////////////////////////////////////////////////////////////
	void registerPreColisionCallbackBetweenTagA(int tagA, 
	                                                           int tagB,
	                                                           SelectorProtocol* obj, 
	                                                           SEL_CallFuncO sel)
	{
	    CCMutableDictionary<int>* tableA = preCollisionMap.objectForKey(tagA);
	    
	    
	    if(tableA == NULL){
	        LHContactNodeInfo* info = LHContactNodeInfo::contactInfoWithTag(tagB, obj, sel);
	        
	        CCMutableDictionary<int>* map = new CCMutableDictionary<int>();
	        map.setObject(info, tagB);
	        preCollisionMap.setObject(map, tagA);
	    }
	    else{
	        LHContactNodeInfo* info = LHContactNodeInfo::contactInfoWithTag(tagB, obj, sel);
	        tableA.setObject(info, tagB);
	    }   
	}
	////////////////////////////////////////////////////////////////////////////////
	void cancelPreColisionCallbackBetweenTagA(int tagA,
	                                                         int tagB){
	    CCMutableDictionary<int>* tableA = (CCMutableDictionary<int>*)preCollisionMap.objectForKey(tagA);

	    if(NULL != tableA)
	    {
	        tableA.removeObjectForKey(tagB);
	    }
	}
	////////////////////////////////////////////////////////////////////////////////
	void registerPostColisionCallbackBetweenTagA(int tagA,
	                                                            int tagB,
	                                                            SelectorProtocol* obj, 
	                                                            SEL_CallFuncO sel)
	{
	    
	    CCMutableDictionary<int>* tableA = (CCMutableDictionary<int>*)postCollisionMap.objectForKey(tagA);
	    
	    
	    if(tableA == NULL){
	        LHContactNodeInfo info = LHContactNodeInfo.contactInfoWithTag(tagB, obj, sel);
	        
	        CCMutableDictionary<int>* map = new CCMutableDictionary<int>();
	        map.setObject(info, tagB);
	        postCollisionMap.setObject(map, tagA);
	    }
	    else{
	        LHContactNodeInfo* info = LHContactNodeInfo::contactInfoWithTag(tagB, obj, sel);
	        tableA.setObject(info, tagB);
	    }   
	}
	////////////////////////////////////////////////////////////////////////////////
	void cancelPostColisionCallbackBetweenTagA(int tagA,
	                                                          int tagB)
	{
	    CCMutableDictionary<int>* tableA = (CCMutableDictionary<int>*)postCollisionMap.objectForKey(tagA);
	    
	    if(NULL != tableA)
	    {
	        tableA.removeObjectForKey(tagB);
	    }
	}
	////////////////////////////////////////////////////////////////////////////////
	void preSolve(Contact contact,                     
	                             const b2Manifold* oldManifold)
	{
	    b2Body *bodyA = contact.GetFixtureA().GetBody();
		b2Body *bodyB = contact.GetFixtureB().GetBody();
		
	    
	    CCNode* nodeA = (CCNode*)bodyA.GetUserData();
	    CCNode* nodeB = (CCNode*)bodyB.GetUserData();
	        
	    CCMutableDictionary<int>* info = (CCMutableDictionary<int>*)preCollisionMap.objectForKey(nodeA.getTag());
	    
	    if(info != NULL){

	        LHContactNodeInfo* contactInfo = (LHContactNodeInfo*)info.objectForKey(nodeB.getTag());
	        
	        if(NULL != contactInfo)
	        {
	            contactInfo.callListenerWithBodyA(bodyA,bodyB,contact,oldManifold,0);
	        }
	    }
	    else
	    {
	        info = (CCMutableDictionary<int>*)preCollisionMap.objectForKey(nodeB.getTag());
	        
	        if(NULL != info){
	            
	            LHContactNodeInfo* contactInfo = (LHContactNodeInfo*)info.objectForKey(nodeA.getTag());
	            
	            if(NULL != contactInfo)
	            {
	                contactInfo.callListenerWithBodyA(bodyB, bodyA, contact, oldManifold, 0);
	            }
	        }        
	    }
	}
	////////////////////////////////////////////////////////////////////////////////
	void postSolve(Contact contact,
	                              const b2ContactImpulse* impulse){
	    
	    b2Body *bodyA = contact.GetFixtureA().GetBody();
		b2Body *bodyB = contact.GetFixtureB().GetBody();
		
	    
	    CCNode* nodeA = (CCNode*)bodyA.GetUserData();
	    CCNode* nodeB = (CCNode*)bodyB.GetUserData();
	    
	    CCMutableDictionary<int>* info = (CCMutableDictionary<int>*)preCollisionMap.objectForKey(nodeA.getTag());
	    
	    if(info != NULL){
	        
	        LHContactNodeInfo* contactInfo = (LHContactNodeInfo*)info.objectForKey(nodeB.getTag());
	        
	        if(NULL != contactInfo)
	        {
	            contactInfo.callListenerWithBodyA(bodyA,bodyB,contact,0,impulse);
	        }
	    }
	    else
	    {
	        info = (CCMutableDictionary<int>*)preCollisionMap.objectForKey(nodeB.getTag());
	        
	        if(NULL != info){
	            
	            LHContactNodeInfo* contactInfo = (LHContactNodeInfo*)info.objectForKey(nodeA.getTag());
	            
	            if(NULL != contactInfo)
	            {
	                contactInfo.callListenerWithBodyA(bodyB, bodyA, contact, 0, impulse);
	            }
	        }        
	    }    
	}

}
