//  This file was generated by LevelHelper
//  http://www.levelhelper.org
//
//  LevelHelperLoader.mm
//  Created by Bogdan Vladu
//  Copyright 2011 Bogdan Vladu. All rights reserved.
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
//
////////////////////////////////////////////////////////////////////////////////
#import "LHSprite.h"
#import "LHSettings.h"
#import "LHPathNode.h"
#import "LHParallaxNode.h"
#import "LHAnimationNode.h"
#import "LevelHelperLoader.h"
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
@interface LevelHelperLoader (LH_LOADER_SPRITE_EXT) 
-(LHAnimationNode*)animationNodeWithUniqueName:(NSString*)animName;
@end
@implementation LevelHelperLoader (LH_LOADER_SPRITE_EXT)
-(LHAnimationNode*)animationNodeWithUniqueName:(NSString*)animName{
   return [animationsInLevel objectForKey:animName];
}
@end

@interface LHSprite (Private)

@end
////////////////////////////////////////////////////////////////////////////////
@implementation LHSprite
@synthesize realScale;
////////////////////////////////////////////////////////////////////////////////
//-(oneway void) release{
//    
//    NSLog(@"LH Sprite RELEASE %@", uniqueName);
//    
//    [super release];
//}
-(void) dealloc{		
    
    //NSLog(@"LH Sprite Dealloc %@", uniqueName);
    parentLoader = nil;
    
    if(NULL != parallaxFollowingThisSprite)
        [parallaxFollowingThisSprite followSprite:NULL 
                                changePositionOnX:false 
                                changePositionOnY:false];

    if(NULL != spriteIsInParallax){
        [spriteIsInParallax removeChild:self];
        spriteIsInParallax = nil;
    }
    
    if(nil != pathNode){
        [pathNode removeFromParentAndCleanup:YES];
        pathNode = nil;
    }
    
    [self stopAllActions];
    [self removeBodyFromWorld];

    [uniqueName release];
    [customUserValues release];
	[super dealloc];
}
////////////////////////////////////////////////////////////////////////////////
-(void) generalLHSpriteInit{
    
    if(nil != uniqueName)
        return; //compatibility with cocos2d 2.0
        
    body = NULL;
    uniqueName = [[NSMutableString alloc] init];
    customUserValues = [[NSMutableDictionary alloc] init];
    
    currentFrame = 0;
    pathNode = nil;
    spriteIsInParallax = nil;
}
-(id) init{
    self = [super init];
    if (self != nil)
    {
        [self generalLHSpriteInit];
    }
    return self;
}
////////////////////////////////////////////////////////////////////////////////
+(id) spriteWithTexture:(CCTexture2D*)texture{
    return [[[self alloc] initWithTexture:texture] autorelease];
}
+(id) spriteWithTexture:(CCTexture2D*)texture rect:(CGRect)rect{
    return [[[self alloc] initWithTexture:texture rect:rect] autorelease];
}
+(id) spriteWithSpriteFrame:(CCSpriteFrame*)spriteFrame{
    return [[[self alloc] initWithSpriteFrame:spriteFrame] autorelease];
}
+(id) spriteWithSpriteFrameName:(NSString*)spriteFrameName{
    return [[[self alloc] initWithSpriteFrameName:spriteFrameName] autorelease];
}
+(id) spriteWithFile:(NSString*)filename{
    return [[[self alloc] initWithFile:filename] autorelease];
}
+(id) spriteWithFile:(NSString*)filename rect:(CGRect)rect{
    return [[[self alloc] initWithFile:filename rect:rect] autorelease];
}
+(id) spriteWithCGImage: (CGImageRef)image key:(NSString*)key{
    return [[[self alloc] initWithCGImage:image key:key] autorelease];
}
+(id) spriteWithBatchNode:(CCSpriteBatchNode*)batchNode rect:(CGRect)rect{
    return [[[self alloc] initWithBatchNode:batchNode rect:rect] autorelease];
}
////////////////////////////////////////////////////////////////////////////////
-(id) initWithTexture:(CCTexture2D*)texture{

    self = [super initWithTexture:texture];
	if (self != nil)
	{
		[self generalLHSpriteInit];
	}
	return self;
}
-(id) initWithTexture:(CCTexture2D*)texture rect:(CGRect)rect{

    self = [super initWithTexture:texture rect:rect];
	if (self != nil)
	{
		[self generalLHSpriteInit];
	}
	return self;
}
-(id) initWithSpriteFrame:(CCSpriteFrame*)spriteFrame{

    self = [super initWithSpriteFrame:spriteFrame];
	if (self != nil)
	{
		[self generalLHSpriteInit];
	}
	return self;
}
-(id) initWithSpriteFrameName:(NSString*)spriteFrameName{
    self = [super initWithSpriteFrameName:spriteFrameName];
	if (self != nil)
	{
		[self generalLHSpriteInit];
	}
	return self;    
}
-(id) initWithFile:(NSString*)filename{
    self = [super initWithFile:filename];
    if (self != nil)
    {
        [self generalLHSpriteInit];        
    }
    return self;
}
-(id) initWithFile:(NSString*)filename rect:(CGRect)rect{
    self = [super initWithFile:filename rect:rect];
    if (self != nil)
    {
        [self generalLHSpriteInit];
    }
    return self;
}
-(id) initWithCGImage:(CGImageRef)image key:(NSString*)key{
    self = [super initWithCGImage:image key:key];
    if (self != nil)
    {
        [self generalLHSpriteInit];
    }
    return self;
}
-(id) initWithBatchNode:(CCSpriteBatchNode*)batchNode rect:(CGRect)rect{
    self = [super initWithBatchNode:batchNode rect:rect];
    if (self != nil)
    {
        [self generalLHSpriteInit];
    }
    return self;
}
-(id) initWithBatchNode:(CCSpriteBatchNode*)batchNode rectInPixels:(CGRect)rect{
    self = [super initWithBatchNode:batchNode rectInPixels:rect];
    if (self != nil)
    {
        [self generalLHSpriteInit];
    }
    return self;
}
////////////////////////////////////////////////////////////////////////////////
-(void) setUniqueName:(NSString*)name{
    NSAssert(name!=nil, @"UniqueName must not be nil");
    [uniqueName setString:name];
}
//------------------------------------------------------------------------------
-(NSString*)uniqueName{
    return uniqueName;   
}
//------------------------------------------------------------------------------
-(void) setBody:(b2Body*)bd{
    NSAssert(bd!=nil, @"b2Body must not be nil");
    
    body = bd;
}
//------------------------------------------------------------------------------
-(b2Body*)body{
    return body;
}
//------------------------------------------------------------------------------
-(bool) removeBodyFromWorld{
    if(NULL != body){
		b2World* _world = body->GetWorld();
		if(0 != _world){
                                    
			_world->DestroyBody(body);
			body = NULL;
            return true;
		}
	}
    return false;
}
//------------------------------------------------------------------------------
-(void) setCustomValue:(id)value withKey:(NSString*)key{
    
    NSAssert(value!=nil, @"Custom value object must not be nil");    
    NSAssert(key!=nil, @"Custom value key must not be nil");    
    
    [customUserValues setObject:value forKey:key];
}
//------------------------------------------------------------------------------
-(id) customValueWithKey:(NSString*)key{
    NSAssert(key!=nil, @"Custom value key must not be nil");    
    return [customUserValues objectForKey:key];
}
////////////////////////////////////////////////////////////////////////////////
-(void) transformPosition:(CGPoint)pos{
    [super setPosition:pos];
    if(0 != body){
        b2Vec2 boxPosition = [LevelHelperLoader pointsToMeters:pos];
        float angle = CC_DEGREES_TO_RADIANS(-1*super.rotation);
        body->SetTransform(boxPosition, angle);
    }
}
//------------------------------------------------------------------------------
-(CGPoint)position{
    return super.position;
}
//------------------------------------------------------------------------------
-(void)transformRotation:(float)rot{
    
    [super setRotation:rot];
    if(0 != body){
        b2Vec2 boxPosition = [LevelHelperLoader pointsToMeters:super.position];
        float angle = CC_DEGREES_TO_RADIANS(-1*rot);
        body->SetTransform(boxPosition, angle);
    }
}
//------------------------------------------------------------------------------
-(float)rotation{
    return super.rotation;
}
////////////////////////////////////////////////////////////////////////////////
-(void) startAnimationNamed:(NSString*)animName 
             endObserverObj:(id)obj  
             endObserverSel:(SEL)sel  
  shouldObserverLoopForever:(bool)observeLooping{

    [self startAnimationNamed:animName
                    startingFromFrame:0
                       endObserverObj:obj
                       endObserverSel:sel
    shouldObserverLoopForever:observeLooping];
}
//------------------------------------------------------------------------------
-(void) startAnimationNamed:(NSString*)animName 
          startingFromFrame:(int)startFrame
             endObserverObj:(id)obj
             endObserverSel:(SEL)sel
  shouldObserverLoopForever:(bool)observeLooping{

    if(parentLoader == nil)
        return;
    
    LHAnimationNode* animNode = [parentLoader animationNodeWithUniqueName:animName];
    if(nil != animNode)
    {
        LHBatch* batch = [parentLoader batchNodeForFile:[animNode imageName]];
        if(batch)
        {
            [animNode setBatchNode:[batch spriteBatchNode]];
            [animNode computeFrames];
            
            [animNode runAnimationOnSprite:self 
                         startingFromFrame:startFrame
                           withNotifierObj:obj 
                               notifierSel:sel 
                               notifOnLoop:observeLooping];
        }
    }
}
//------------------------------------------------------------------------------
-(void) startAnimationNamed:(NSString*)animName 
          startingFromFrame:(int)startFrame{
    
    [self startAnimationNamed:animName
            startingFromFrame:startFrame
               endObserverObj:nil
               endObserverSel:nil
    shouldObserverLoopForever:false];
}
//------------------------------------------------------------------------------
-(void) startAnimationNamed:(NSString*)animName{

    [self startAnimationNamed:animName
            startingFromFrame:0
               endObserverObj:nil
               endObserverSel:nil
    shouldObserverLoopForever:false];
}
//------------------------------------------------------------------------------
-(void) prepareAnimationNamed:(NSString*)animName{

    if(parentLoader == nil)
        return;

    LHAnimationNode* animNode = [parentLoader animationNodeWithUniqueName:animName];
    if(animNode == nil)
        return;
    
    LHBatch* batch = [parentLoader batchNodeForFile:[animNode imageName]];
    
    if(batch){
        [animNode setBatchNode:[batch spriteBatchNode]];
        [animNode computeFrames];
        [self setAnimation:animNode];
    }
}
//------------------------------------------------------------------------------
-(void) stopAnimation{
    [self stopActionByTag:LH_ANIM_ACTION_TAG];
    [self setAnimation:nil];
}
//------------------------------------------------------------------------------
-(void) setAnimation:(LHAnimationNode*)anim{
    animation = anim;
    if(nil != anim){
        [anim setAnimationTexturePropertiesOnSprite:self];
        [self setFrame:0];
    }
}
//------------------------------------------------------------------------------
-(LHAnimationNode*)animation{
    return animation;
}
//------------------------------------------------------------------------------
-(NSString*) animationName{
    if(nil != animation)
        return [animation uniqueName];
    return @"";
}
//------------------------------------------------------------------------------
-(int) numberOfFrames{
    if(nil != animation)
        return [animation numberOfFrames];    
    return -1;
}
//------------------------------------------------------------------------------
-(void) setFrame:(int)frmNo{    
    if(animation == nil)
        return;
    [animation setFrame:frmNo onSprite:self];
    currentFrame = frmNo;
}
//------------------------------------------------------------------------------
-(int) currentFrame{
    if(nil != animation){
        NSArray* frames = [animation frames];
        if(nil != frames){
            for(int i = 0; i < (int)[frames count]; ++i){
                CCSpriteFrame* frame = [frames objectAtIndex:i];
                
                if(CGRectEqualToRect([frame rect], [self textureRect])){
                    return i;
                }
            }
        }
    }
    return 0;
}
//------------------------------------------------------------------------------
-(void) nextFrame{
    int curFrame = [self currentFrame];
    curFrame +=1;
        
    if(curFrame >= 0 && curFrame < [self numberOfFrames]){
        [self setFrame:curFrame];
    }    
}
//------------------------------------------------------------------------------
-(void) prevFrame{

    int curFrame = [self currentFrame];
    curFrame -=1;
        
    if(curFrame >= 0 && curFrame < (int)[self numberOfFrames]){
        [self setFrame:curFrame];
    }        
}
//------------------------------------------------------------------------------
-(void) nextFrameAndRepeat{
    
    int curFrame = [self currentFrame];
    curFrame +=1;
    
    if(curFrame >= [self numberOfFrames]){
        curFrame = 0;
    }
    
    if(curFrame >= 0 && curFrame < [self numberOfFrames]){
        [self setFrame:curFrame];
    }    
}
//------------------------------------------------------------------------------
-(void) prevFrameAndRepeat{
 
    int curFrame = [self currentFrame];
    curFrame -=1;
    
    if(curFrame < 0){
        curFrame = [self numberOfFrames] - 1;        
    }
    
    if(curFrame >= 0 && curFrame < (int)[self numberOfFrames]){
        [self setFrame:curFrame];
    }        
}
//------------------------------------------------------------------------------
-(bool) isAtLastFrame{
    return ([self numberOfFrames]-1 == [self currentFrame]);
}
////////////////////////////////////////////////////////////////////////////////
-(void) moveOnPathWithUniqueName:(NSString*)pathName 
                           speed:(float)pathSpeed 
                 startAtEndPoint:(bool)startAtEndPoint
                        isCyclic:(bool)isCyclic
               restartAtOtherEnd:(bool)restartOtherEnd
                 axisOrientation:(int)axis
                           flipX:(bool)flipx
                           flipY:(bool)flipy
                   deltaMovement:(bool)dMove
                  endObserverObj:(id)obj 
                  endObserverSel:(SEL)sel
{
    
    if(pathName == nil)
        return;

    if(parentLoader == nil)
        return;

	//already moving on a path so lets cancel that path movement
    [self cancelPathMovement];
    
	LHBezierNode* node = [parentLoader bezierNodeWithUniqueName:pathName];
	
	if(nil != node)
	{
		LHPathNode* pNode = [node addSpriteOnPath:self
                                            speed:pathSpeed
                                  startAtEndPoint:startAtEndPoint
                                         isCyclic:isCyclic 
                                restartAtOtherEnd:restartOtherEnd
                                  axisOrientation:axis
                                            flipX:flipx
                                            flipY:flipy
                                    deltaMovement:dMove];
        
        if(nil != pNode){
            [pNode setPathNotifierObject:obj];
            [pNode setPathNotifierSelector:sel];
        }
        pathNode = pNode;
	}
}
//------------------------------------------------------------------------------
-(void) cancelPathMovement{
    if(nil != pathNode){
        [pathNode removeFromParentAndCleanup:YES];
        pathNode = nil;
    }
}
//------------------------------------------------------------------------------
-(void) pausePathMovement:(bool)pauseStatus
{
    if(nil != pathNode){
        [pathNode setPaused:pauseStatus];
    }
}
//------------------------------------------------------------------------------
-(void) setPathSpeed:(float)value{
    if(pathNode != nil){
        [pathNode setSpeed:value];
    }
}
//------------------------------------------------------------------------------
-(float) pathSpeed{
    if(pathNode != nil){
        return [pathNode speed];
    }
    return 0;
}
//------------------------------------------------------------------------------
-(void) setPathNode:(LHPathNode*)node{
    //NSAssert(node!=nil, @"LHPathNode must not be nil");    
    pathNode = node;
}
//------------------------------------------------------------------------------
-(LHPathNode*)pathNode{
    return pathNode;
}
////////////////////////////////////////////////////////////////////////////////
+(NSString*) uniqueNameForBody:(b2Body*)body{
    
    id spr = (id)body->GetUserData();
    
    if([LHSprite isLHSprite:spr])
        return [spr uniqueName];
    
    if([LHBezierNode isLHBezierNode:spr])
        return [spr uniqueName];
    
    return nil;
}
//------------------------------------------------------------------------------
+(LHSprite*) spriteForBody:(b2Body*)body
{
    if(0 == body)
        return nil;
            
    id spr = (id)body->GetUserData();
    
    if([LHSprite isLHSprite:spr])
        return spr;
    
    return nil;    
}
//------------------------------------------------------------------------------
+(int) tagForBody:(b2Body*)body{
    if(0 != body){
        CCNode* spr = (CCNode*)body->GetUserData();
        if(nil != spr){
            return [spr tag];
        }
    }
    return -1;
}
//------------------------------------------------------------------------------
+(bool) isLHSprite:(id)object{
    if([object isKindOfClass:[LHSprite class]]){
        return true;
    }
    return false;
}
////////////////////////////////////////////////////////////////////////////////
@end