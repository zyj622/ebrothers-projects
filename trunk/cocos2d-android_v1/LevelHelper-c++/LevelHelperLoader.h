//  This file is part of LevelHelper
//  http://www.levelhelper.org
//
//  Created by Bogdan Vladu
//  Copyright 2011 Bogdan Vladu. All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
//
//  This software is provided 'as-is', without any express or implied
//  warranty.  In no event will the authors be held liable for any damages
//  arising from the use of this software.
//  Permission is granted to anyone to use this software for any purpose,
//  including commercial applications, and to alter it and redistribute it
//  freely, subject to the following restrictions:
//  The origin of this software must not be misrepresented; you must not
//  claim that you wrote the original software. If you use this software
//  in a product, an acknowledgment in the product documentation would be
//  appreciated but is not required.
//  Altered source versions must be plainly marked as such, and must not be
//  misrepresented as being the original software.
//  This notice may not be removed or altered from any source distribution.
//  By "software" the author refers to this code file and not the application 
//  that was used to generate this file.
//  You do not have permission to use this code or any part of it if you don't
//  own a license to LevelHelper application.
////////////////////////////////////////////////////////////////////////////////
//
//  Version history
//  ...............
//  v0.1 First version for LevelHelper 1.4
//  v0.2 added setFollowSprite in the LHParallaxNode class
////////////////////////////////////////////////////////////////////////////////

#ifndef __LEVEL_HELPER_LOADER__
#define __LEVEL_HELPER_LOADER__

#include "cocos2d.h"
#include "CCNS.h"
#include "Box2D.h"

#include "LHDictionary.h"
#include "LHArray.h"
#include "LHObject.h"

#include "LHSprite.h"
#include "LHContactNode.h"
#include "LHAnimationNode.h"
#include "LHParallaxNode.h"
#include "LHJoint.h"
#include "LHBatch.h"
#include "LHPathNode.h"
#include "LHBezierNode.h"
#include "LHContactInfo.h"

using namespace cocos2d;

enum LevelHelper_TAG 
{ 
	DEFAULT_TAG 	= 0,
	NUMBER_OF_TAGS 	= 1
};

enum LH_ACTIONS_TAGS
{
    LH_PATH_ACTION_TAG,
    LH_ANIM_ACTION_TAG
};


int     intFromString(const std::string& str);
bool    boolFromString(const std::string& str);
float   floatFromString(const std::string& str);
CCPoint LHPointFromString(const std::string& str);
CCRect  LHRectFromString(const std::string& str);

class LevelHelperLoader : public SelectorProtocol, public CCObject {

private:
    LHArray* lhSprites;
    LHArray* lhJoints;
    LHArray* lhParallax;
    LHArray* lhBeziers;
    LHArray* lhAnims;
    LHDictionary* lhBatchInfo;       //key - imageName - value LHDictionary

    CCMutableDictionary<std::string>  animationsInLevel;    //key name - value LHAnimationNode*;
    CCMutableDictionary<std::string>  spritesInLevel;       //key name - value LHSprite*
    CCMutableDictionary<std::string>  jointsInLevel;        //key name - value LHJoint*
    CCMutableDictionary<std::string>  parallaxesInLevel;    //key name - value LHParallaxNode*
    CCMutableDictionary<std::string>  beziersInLevel;       //key name - value LHBezierNode*
    CCMutableDictionary<std::string>  batchNodesInLevel;    //key name - value LHBatch*

    LHDictionary*  wb;
    
    //keys LHPhysicBoundarieTop, LHPhysicBoundarieLeft, LHPhysicBoundarieBottom, LHPhysicBoundarieRight 
    //value - LHSprite*    
    CCMutableDictionary<std::string> physicBoundariesInLevel;
    //LHDictionary* physicBoundariesInLevel;
    
	bool addSpritesToLayerWasUsed;
	bool addObjectsToWordWasUsed;
    
    CCPoint safeFrame;
    CCRect  gameWorldRect;
    CCPoint gravity;
	
    SelectorProtocol* pathNotifierId;
    SEL_CallFuncN pathNotifierSel;
    
    SelectorProtocol* animNotifierId;
    SEL_CallFuncND animNotifierSel;
    bool    notifOnLoopForeverAnim;
    
	CCLayer* cocosLayer; //weak ptr
    b2World* box2dWorld; //weak ptr
    
    LHContactNode* contactNode;
    
public:

    //------------------------------------------------------------------------------
    LevelHelperLoader(const char* levelFile);
    virtual ~LevelHelperLoader();
    //------------------------------------------------------------------------------
    
    //LOADING
    void addObjectsToWorld(b2World* world, CCLayer* cocosLayer);
    void addSpritesToLayer(CCLayer* cocosLayer); //NO PHYSICS
    //------------------------------------------------------------------------------
    //UTILITIES
    static void preloadBatchNodes(void);
    static void dontStretchArtOnIpad(void);
    
    //------------------------------------------------------------------------------
    //PAUSING THE GAME
    //this will pause all path movement and all parallaxes
    //use  [[CCDirector sharedDirector] pause]; for everything else
    static bool isPaused(void);
    static void setPaused(bool value); //pass true to pause, false to unpause
    
    
    //------------------------------------------------------------------------------
    //COLLISION HANDLING
    //see API Documentation on the website to see how to use this
    void useLevelHelperCollisionHandling(void);
    void registerPreColisionCallbackBetweenTagA(enum LevelHelper_TAG tagA,
                                                enum LevelHelper_TAG tagB,
                                                SelectorProtocol* obj,
                                                SEL_CallFuncO selector);
    
    void cancelPreCollisionCallbackBetweenTagA(enum LevelHelper_TAG tagA,
                                               enum LevelHelper_TAG tagB);
    
    void registerPostColisionCallbackBetweenTagA(enum LevelHelper_TAG tagA,
                                                 enum LevelHelper_TAG tagB,
                                                 SelectorProtocol* obj,
                                                 SEL_CallFuncO selector);
    
    void cancelPostCollisionCallbackBetweenTagA(enum LevelHelper_TAG tagA,
                                                enum LevelHelper_TAG tagB);
    
    //------------------------------------------------------------------------------
    //SPRITES
    LHSprite* spriteWithUniqueName(const std::string& name); 
    CCArray* spritesWithTag(enum LevelHelper_TAG tag);
    bool removeSprite(LHSprite* sprite);
    bool removeSpritesWithTag(enum LevelHelper_TAG tag);
    bool removeAllSprites(void);
    /*More methods in LHSpitesExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //CREATION
    
    //New sprite and associated body will be released automatically
    //or you can use removeFromParentAndCleanup(true), CCSprite method, to do it at a specific time
    //you must set the desired position after creation
    
    //sprites returned needs to be added in the layer by you
    //new sprite unique name for the returned sprite will be
    //[OLDNAME]_LH_NEW__SPRITE_XX and [OLDNAME]_LH_NEW_BODY_XX
    LHSprite* newSpriteWithUniqueName(const std::string& name); //no physic body
    LHSprite* newPhysicalSpriteWithUniqueName(const std::string& name);//with physic body
    
    //sprites are added in the coresponding batch node automatically
    //new sprite unique name for the returned sprite will be
    //[OLDNAME]_LH_NEW_BATCH_SPRITE_XX and [OLDNAME]_LH_NEW_BATCH_BODY_XX
    LHSprite* newBatchSpriteWithUniqueName(const std::string& name); //no physic body
    LHSprite* newPhysicalBatchSpriteWithUniqueName(const std::string& name); //with physic body
    
    /*More methods in LHCreationExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //JOINTS
    LHJoint* jointWithUniqueName(const std::string& name);
    CCArray* jointsWithTag(enum LevelHelper_TAG tag); //returns array with LHJoint*
    void removeJointsWithTag(enum LevelHelper_TAG tag);
    bool removeJoint(LHJoint* joint);
    bool removeAllJoints(void);
    /*More methods in LHJointsExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //PARALLAX
    LHParallaxNode* paralaxNodeWithUniqueName(const std::string& uniqueName);
    /*More methods in LHParallaxExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //BEZIER
    LHBezierNode* bezierNodeWithUniqueName(const std::string& name);
    void          removeAllBezierNodes(void);
    //------------------------------------------------------------------------------
    //GRAVITY
    bool isGravityZero(void);
    void createGravity(b2World* world);
    //------------------------------------------------------------------------------
    //PHYSIC BOUNDARIES
    void createPhysicBoundaries(b2World* _world);
    
    //this method should be used when using dontStretchArtOnIpad
    //see api documentatin for more info
    void createPhysicBoundariesNoStretching(b2World * _world);
    
    CCRect physicBoundariesRect(void);
    bool hasPhysicBoundaries(void);
    
    b2Body* leftPhysicBoundary(void);
    LHSprite* leftPhysicBoundarySprite(void);
    b2Body* rightPhysicBoundary(void);
    LHSprite* rightPhysicBoundarySprite(void);
    b2Body* topPhysicBoundary(void);
    LHSprite* topPhysicBoundarySprite(void);
    b2Body* bottomPhysicBoundary(void);
    LHSprite* bottomPhysicBoundarySprite(void);
    void removePhysicBoundaries(void);
    //------------------------------------------------------------------------------
    //LEVEL INFO
    CCSize gameScreenSize(void); //the device size set in loaded level
    CCRect gameWorldSize(void); //the size of the game world
    //------------------------------------------------------------------------------
    //BATCH
    unsigned int numberOfBatchNodesUsed(void);
    void removeUnusedBatchesFromMemory(void);
    /*More methods in LHBatchExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //ANIMATION
    void startAnimationWithUniqueName(const std::string& animationName, 
                                      LHSprite* sprite, 
                                      SelectorProtocol* customAnimNotifierId = NULL,
                                      SEL_CallFuncND customAnimNotifierSel = NULL);
    
    void stopAnimationOnSprite(LHSprite* sprite);
    
    //this will not start the animation - it will just prepare it
    void prepareAnimationWithUniqueName(const std::string& animName, LHSprite* sprite);
    
    //needs to be called before addObjectsToWorld or addSpritesToLayer
    //signature for registered method should be like this: void HelloWorld::spriteAnimHasEnded(CCSprite* spr, const std::string& animName);
    //registration is done like this:  lh->registerNotifierOnAnimationEnds(this, callfuncND_selector(HelloWorld::spriteAnimHasEnded));
    //this will trigger for all type of animations even for the ones controlled by you will next/prevFrameFor...
    void registerNotifierOnAllAnimationEnds(SelectorProtocol* obj, SEL_CallFuncND sel);
    
    /*
     by default the notification on animation end works only on non-"loop forever" animations
     if you want to receive notifications on "loop forever" animations enable this behaviour
     before addObjectsToWorld by calling the following function
     */
    void enableNotifOnLoopForeverAnimations(void);
    
    /*More methods in LHAnimationsExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //PATH
    void moveSpriteOnPathWithUniqueName(LHSprite * ccsprite, 
                                        const   std::string& pathUniqueName,
                                        float   time, 
                                        bool    startAtEndPoint,
                                        bool    isCyclic,
                                        bool    restartOtherEnd,
                                        int     axis,
                                        bool    flipx,
                                        bool    flipy,
                                        bool    deltaMove);//describe path movement without setting the sprite position on the actual points on the path
    
    //DISCUSSION
    //signature for registered method should be like this: void HelloWorld::spriteMoveOnPathEnded(LHSprite* spr);
    //registration is done like this:  lh->registerNotifierOnPathEnd(this, callfuncN_selector(HelloWorld::spriteMoveOnPathEnded));
    void registerNotifierOnAllPathEndPoints(SelectorProtocol* obj, SEL_CallFuncN sel);

    
    /*More methods in LHPathExt.h - download from http://www.levelhelper.org*/
    //------------------------------------------------------------------------------
    //PHYSICS
    static void setMeterRatio(float ratio); //default is 32.0f
    static float meterRatio(void); //same as pointsToMeterRatio - provided for simplicity as static method
    
    static float pixelsToMeterRatio(void);
    static float pointsToMeterRatio(void);
    
    static b2Vec2 pixelToMeters(CCPoint point); //Cocos2d point to Box2d point
    static b2Vec2 pointsToMeters(CCPoint point); //Cocos2d point to Box2d point
    
    static CCPoint metersToPoints(b2Vec2 vec); //Box2d point to Cocos2d point
    static CCPoint metersToPixels(b2Vec2 vec); //Box2d point to Cocos2d pixels
    //------------------------------------------------------------------------------
    //needed when deriving this class
    virtual void setSpriteProperties(LHSprite* ccsprite, LHDictionary* spriteProp);
    
    virtual void setCustomAttributesForPhysics(LHDictionary* spriteProp, b2Body* body, LHSprite* sprite);

    virtual void setCustomAttributesForNonPhysics(LHDictionary* spriteProp, LHSprite* sprite);

    virtual void setCustomAttributesForBezierBodies(LHDictionary* bezierProp, CCNode* sprite, b2Body* body);
    ////////////////////////////////////////////////////////////////////////////
    
    
private:
    void initObjects(void);
    
    
    
    //------------------------------------------------------------------------------
    void createAllAnimationsInfo(void);
    void createAnimationFromDictionary(LHDictionary* spriteProp, LHSprite* ccsprite);

    //------------------------------------------------------------------------------
    void createAllBeziers(void);
    //------------------------------------------------------------------------------
    void createPathOnSprite(LHSprite* ccsprite, LHDictionary* spriteProp);
    //------------------------------------------------------------------------------
    void createSpritesWithPhysics(void);
    void setFixtureDefPropertiesFromDictionary(LHDictionary* spritePhysic, b2FixtureDef* shapeDef);

    b2Body* b2BodyFromDictionary(LHDictionary* spritePhysic,
                                 LHDictionary* spriteProp,
                                 LHSprite* ccsprite,
                                 b2World* _world);
    
    void releaseAllSprites(void);
    //------------------------------------------------------------------------------
    void createParallaxes(void);
    LHParallaxNode* parallaxNodeFromDictionary(LHDictionary* parallaxDict, CCLayer* layer);
    void releaseAllParallaxes(void);
    //------------------------------------------------------------------------------
    void createJoints(void);
    LHJoint* jointFromDictionary(LHDictionary* joint, b2World* world);
    void releaseAllJoints(void);
    //------------------------------------------------------------------------------
    void releasePhysicBoundaries(void);
    b2Body* physicBoundarieForKey(const std::string& key);
    //------------------------------------------------------------------------------
    void loadLevelHelperSceneFile(const char* levelFile,
                                  const char* subfolder, 
                                  const char* imgFolder);
    
    LHBatch* loadBatchNodeWithImage(const std::string& image);
    void addBatchNodesToLayer(CCLayer* _cocosLayer);
    void addBatchNodeToLayer(CCLayer* _cocosLayer, LHBatch* info);
    LHBatch* batchNodeForFile(const std::string& image);
    void releaseAllBatchNodes(void);
    
    void loadLevelHelperSceneFromDictionary(const LHDictionary& levelDictionary, 
                                            const std::string& imgFolder);
        
    void processLevelFileFromDictionary(LHDictionary* dictionary);
        
    void createPhysicBoundariesHelper(b2World* _world,
                                      const CCPoint& wbConv,
                                      const CCPoint& pos_offset);

    
    LHSprite* spriteFromDictionary(LHDictionary* spriteProp);
    LHSprite* spriteWithBatchFromDictionary(LHDictionary* spriteProp, LHBatch* lhBatch);
};

#endif


