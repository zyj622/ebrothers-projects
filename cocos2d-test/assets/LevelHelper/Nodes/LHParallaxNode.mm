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
#import "LHParallaxNode.h"
#import "LHSettings.h"
#import "LHSprite.h"
#import "LevelHelperLoader.h"
////////////////////////////////////////////////////////////////////////////////

@interface LHSprite (LH_PARALLAX_SPRITE_EXT) 
-(void)setParallaxFollowingThisSprite:(LHParallaxNode*)par;
-(void)setSpriteIsInParallax:(LHParallaxNode*)node;
@end
@implementation LHSprite (LH_PARALLAX_SPRITE_EXT)
-(void)setParallaxFollowingThisSprite:(LHParallaxNode*)par{
    parallaxFollowingThisSprite = par;
}
-(void)setSpriteIsInParallax:(LHParallaxNode*)node{
    spriteIsInParallax = node;
}
@end

@interface LHParallaxPointObject : NSObject
{
	CGPoint position;
	CGPoint	ratio;
	CGPoint offset;
	CGPoint initialPosition;
	CCNode *ccsprite;	//weak ref
	b2Body *body;		//weak ref
}
@property (readwrite) CGPoint ratio;
@property (readwrite) CGPoint offset;
@property (readwrite) CGPoint initialPosition;
@property (readwrite) CGPoint position;
@property (readwrite,assign) CCNode *ccsprite;
@property (readwrite,assign) b2Body *body;

+(id) pointWithCGPoint:(CGPoint)point;
-(id) initWithCGPoint:(CGPoint)point;
@end

@implementation LHParallaxPointObject
@synthesize ratio;
@synthesize initialPosition;
@synthesize offset;
@synthesize position;
@synthesize ccsprite;
@synthesize body;

-(void) dealloc{
	
	//NSLog(@"LH PARALLAX POINT OBJ DEALLOC");
	[super dealloc];
}
+(id) pointWithCGPoint:(CGPoint)_ratio{
	return [[[self alloc] initWithCGPoint:_ratio] autorelease];
}
-(id) initWithCGPoint:(CGPoint)_ratio{
	if( (self=[super init])) {
		ratio = _ratio;
	}
	return self;
}
@end

////////////////////////////////////////////////////////////////////////////////
@interface LHParallaxNode (Private)

@end

@implementation LHParallaxNode

@synthesize isContinuous;
@synthesize direction;
@synthesize speed;
@synthesize paused;

-(void) dealloc{	
	
    for(LHParallaxPointObject* pt in sprites){
        if(pt.ccsprite){
            [(LHSprite*)pt.ccsprite setSpriteIsInParallax:nil];
        }
	}
	//NSLog(@"LHParallaxNode DEALLOC");
    [uniqueName release];
	[sprites release];
	[super dealloc];
}
////////////////////////////////////////////////////////////////////////////////
-(id) initWithDictionary:(NSDictionary*)parallaxDict loader:(LevelHelperLoader*)loader;
{
	if( (self=[super init])) {

		sprites = [[NSMutableArray alloc] init];
		isContinuous = [[parallaxDict objectForKey:@"ContinuousScrolling"] boolValue];
		direction = [[parallaxDict objectForKey:@"Direction"] intValue];
		speed = [[parallaxDict objectForKey:@"Speed"] floatValue];
		lastPosition = CGPointMake(-100,-100);
        paused = false;
		winSize = [[CCDirector sharedDirector] winSize];
		screenNumberOnTheRight = 1;
		screenNumberOnTheLeft = 0;
		screenNumberOnTheTop = 0;
        
        movedEndListenerObj = nil;
        movedEndListenerSEL = nil;
        
        lhLoader = loader;
        
        uniqueName  = [[NSString alloc] initWithString:[parallaxDict objectForKey:@"UniqueName"]];
		if(!isContinuous)
			speed = 1.0f;
	}
	return self;
}
////////////////////////////////////////////////////////////////////////////////
+(id) nodeWithDictionary:(NSDictionary*)properties loader:(LevelHelperLoader*)loader
{
	return [[[self alloc] initWithDictionary:properties loader:loader] autorelease];
}
//////////////////////////////////////////////////////////////////////////////////
-(void) addChild:(LHSprite*)sprite 
   parallaxRatio:(CGPoint)ratio
{
	NSAssert( sprite != NULL, @"Argument must be non-nil");
	
	LHParallaxPointObject *obj = [LHParallaxPointObject pointWithCGPoint:ratio];
	obj.ccsprite = sprite;
	obj.body = [sprite body];
	obj.position = [sprite position];
	obj.offset = [sprite position];
	obj.initialPosition = [sprite position];
	[sprites addObject:obj];
	[sprite setSpriteIsInParallax:self];
	
	int scrRight = (int)(obj.initialPosition.x/winSize.width);
	
	if(screenNumberOnTheRight <= scrRight)
		screenNumberOnTheRight = scrRight+1;
		
	int scrLeft = (int)(obj.initialPosition.x/winSize.width);
    
	if(screenNumberOnTheLeft >= scrLeft)
		screenNumberOnTheLeft = scrLeft-1;

    
	int scrTop = (int)(obj.initialPosition.y/winSize.height);
	
	if(screenNumberOnTheTop <= scrTop)
		screenNumberOnTheTop = scrTop + 1;
	
	int scrBottom = (int)(obj.initialPosition.y/winSize.height);

	if(screenNumberOnTheBottom >= scrBottom)
		screenNumberOnTheBottom = scrBottom-1;
}
////////////////////////////////////////////////////////////////////////////////
-(void) removeChild:(LHSprite*)sprite{
    
    if(nil == sprite) 
        return;
        
    for(int i = 0; i < (int)[sprites count]; ++i)
    {        
        LHParallaxPointObject* pt = [sprites objectAtIndex:i];
	
        if(pt.ccsprite == sprite)
        {
			[sprites removeObjectAtIndex:i];
            break;
        }
	}
    
    if([sprites count] == 0){
        if(lhLoader)[lhLoader removeParallaxNode:self];                     
    }
}
////////////////////////////////////////////////////////////////////////////////
-(void) registerSpriteHasMovedToEndListener:(id)object selector:(SEL)method{
    movedEndListenerObj = object;
    movedEndListenerSEL = method;
}
////////////////////////////////////////////////////////////////////////////////
-(NSString*)uniqueName{
    return uniqueName;
}
////////////////////////////////////////////////////////////////////////////////
-(void) followSprite:(LHSprite*)sprite 
   changePositionOnX:(bool)xChange 
   changePositionOnY:(bool)yChange{
    
    if(NULL == sprite)
    {
        if(NULL != followedSprite)
            [followedSprite setParallaxFollowingThisSprite:NULL];
    }
    
    followedSprite = sprite;
    
    followChangeX = xChange;
    followChangeY = yChange;
    
    if(NULL != sprite)
    {
        lastFollowedSpritePosition = [sprite position];
        [sprite setParallaxFollowingThisSprite:self];
    }
}
////////////////////////////////////////////////////////////////////////////////
-(NSArray*)spritesInNode{
	
	NSMutableArray* sprs = [[[NSMutableArray alloc] init] autorelease];
	for(LHParallaxPointObject* pt in sprites)
	{
		if(pt.ccsprite != nil)
			[sprs addObject:pt.ccsprite];
	}
	
	return sprs;
}
////////////////////////////////////////////////////////////////////////////////
-(NSArray*)bodiesInNode
{
	NSMutableArray* sprs = [[[NSMutableArray alloc] init] autorelease];
	for(LHParallaxPointObject* pt in sprites)
	{
		if(0 != pt.body)
			[sprs addObject:[NSValue valueWithPointer:pt.body]];
	}	
			 
	return sprs;
}
////////////////////////////////////////////////////////////////////////////////
-(void) setPosition:(CGPoint)pos 
            onPoint:(LHParallaxPointObject*)point 
             offset:(CGPoint)offset
{
    if(!isContinuous)
    {
        if(point.ccsprite != nil){
            point.ccsprite.position = pos;
        
            if(point.body != NULL){
            
                float angle = [point.ccsprite rotation];
                point.body->SetAwake(TRUE);
                
                point.body->SetTransform(b2Vec2(pos.x/[[LHSettings sharedInstance] lhPtmRatio], 
                                                pos.y/[[LHSettings sharedInstance] lhPtmRatio]), 
                                         CC_DEGREES_TO_RADIANS(-angle));
            }
        }
    }
    else
    {

        if(point.ccsprite != nil){
            
            CGPoint newPos = CGPointMake(point.ccsprite.position.x - offset.x,
                                         point.ccsprite.position.y - offset.y);
            [point.ccsprite setPosition:newPos];
            
            if(point.body != NULL){
            
            float angle = [point.ccsprite rotation];
            point.body->SetTransform(b2Vec2(newPos.x/[[LHSettings sharedInstance] lhPtmRatio], 
                                            newPos.y/[[LHSettings sharedInstance] lhPtmRatio]), 
                                     CC_DEGREES_TO_RADIANS(-angle));
            }
            
        }
    }
}
////////////////////////////////////////////////////////////////////////////////
-(CGSize) getBounds:(float)rw height:(float)rh angle:(float)radians
{
    float x1 = -rw/2;
    float x2 = rw/2;
    float x3 = rw/2;
    float x4 = -rw/2;
    float y1 = rh/2;
    float y2 = rh/2;
    float y3 = -rh/2;
    float y4 = -rh/2;
    
    float x11 = x1 * cos(radians) + y1 * sin(radians);
    float y11 = -x1 * sin(radians) + y1 * cos(radians);
    float x21 = x2 * cos(radians) + y2 * sin(radians);
    float y21 = -x2 * sin(radians) + y2 * cos(radians);
    float x31 = x3 * cos(radians) + y3 * sin(radians);
    float y31 = -x3 * sin(radians) + y3 * cos(radians);
    float x41 = x4 * cos(radians) + y4 * sin(radians);
    float y41 = -x4 * sin(radians) + y4 * cos(radians);

    float x_min = MIN(MIN(x11,x21),MIN(x31,x41));
    float x_max = MAX(MAX(x11,x21),MAX(x31,x41));
    
    float y_min = MIN(MIN(y11,y21),MIN(y31,y41));
    float y_max = MAX(MAX(y11,y21),MAX(y31,y41));
 
    return CGSizeMake(x_max-x_min, y_max-y_min);
}
////////////////////////////////////////////////////////////////////////////////
-(void)repositionPoint:(LHParallaxPointObject*)point
{
	CGSize spriteContentSize = [point.ccsprite contentSize];
    
    float angle = [point.ccsprite rotation];
    float rotation = CC_DEGREES_TO_RADIANS(angle);
	float scaleX = [point.ccsprite scaleX];
	float scaleY = [point.ccsprite scaleY];
    
    CGSize contentSize = [self getBounds:spriteContentSize.width 
                                  height:spriteContentSize.height 
                                   angle:rotation];
        
	switch (direction) {
		case 1: //right to left
		{
			if(point.ccsprite.position.x + contentSize.width/2.0f*scaleX <= 0)
			{
				float difX = point.ccsprite.position.x + contentSize.width/2.0f*scaleX;
		
				[point setOffset:ccp(winSize.width*screenNumberOnTheRight - point.ratio.x*speed -  contentSize.width/2.0f*scaleX + difX, point.offset.y)];
	
                if(nil != point.ccsprite){
                    CGPoint newPos = CGPointMake(point.offset.x, point.ccsprite.position.y);
                    [point.ccsprite setPosition:newPos];
                
                    if(point.body != NULL){
                    
                        float angle = [point.ccsprite rotation];
                        point.body->SetTransform(b2Vec2(newPos.x/[[LHSettings sharedInstance] lhPtmRatio], 
                                                        newPos.y/[[LHSettings sharedInstance] lhPtmRatio]), 
                                                 CC_DEGREES_TO_RADIANS(-angle));
                    }
                }
                    
                
                if(nil != movedEndListenerObj && nil != movedEndListenerSEL){
                    [movedEndListenerObj performSelector:movedEndListenerSEL withObject:point.ccsprite];
                }
			}
		}	
			break;
			
		case 0://left to right
		{
			if(point.ccsprite.position.x - contentSize.width/2.0f*scaleX >= winSize.width)
			{
				float difX = point.ccsprite.position.x - contentSize.width/2.0f*scaleX - winSize.width;
				
				[point setOffset:ccp(winSize.width*screenNumberOnTheLeft + point.ratio.x*speed +  contentSize.width/2.0f*scaleX + difX, point.offset.y)];
                
                
                
                if(nil != point.ccsprite){
                    CGPoint newPos = CGPointMake(point.offset.x, point.ccsprite.position.y);
                    [point.ccsprite setPosition:newPos];
                    
                    if(point.body != NULL){
                        
                        float angle = [point.ccsprite rotation];
                        point.body->SetTransform(b2Vec2(newPos.x/[[LHSettings sharedInstance] lhPtmRatio], 
                                                        newPos.y/[[LHSettings sharedInstance] lhPtmRatio]), 
                                                 CC_DEGREES_TO_RADIANS(-angle));
                    }
                }

                
                if(nil != movedEndListenerObj && nil != movedEndListenerSEL){
                    [movedEndListenerObj performSelector:movedEndListenerSEL withObject:point.ccsprite];
                }
			}
		}
			break;
			
		case 2://up to bottom
		{
			if(point.ccsprite.position.y + contentSize.height/2.0f*scaleY <= 0)
			{
				float difY = point.ccsprite.position.y + contentSize.height/2.0f*scaleY;
				
				[point setOffset:ccp(point.offset.x, winSize.height*screenNumberOnTheTop - point.ratio.y*speed - contentSize.height/2.0f*scaleY + difY)];
                
                
                if(nil != point.ccsprite){
                    CGPoint newPos = CGPointMake(point.ccsprite.position.x, point.offset.y);
                    [point.ccsprite setPosition:newPos];
                    
                    if(point.body != NULL){
                        
                        float angle = [point.ccsprite rotation];
                        point.body->SetTransform(b2Vec2(newPos.x/[[LHSettings sharedInstance] lhPtmRatio], 
                                                        newPos.y/[[LHSettings sharedInstance] lhPtmRatio]), 
                                                 CC_DEGREES_TO_RADIANS(-angle));
                    }
                }
                
                if(nil != movedEndListenerObj && nil != movedEndListenerSEL){
                    [movedEndListenerObj performSelector:movedEndListenerSEL withObject:point.ccsprite];
                }
			}
		}
			break;
			
		case 3://bottom to top
		{
			if(point.ccsprite.position.y - contentSize.height/2.0f*scaleY >= winSize.height)
			{
				float difY = point.ccsprite.position.y - contentSize.height/2.0f*scaleY - winSize.height;
				
				[point setOffset:ccp(point.offset.x, winSize.height*screenNumberOnTheBottom + point.ratio.y*speed + contentSize.height/2.0f*scaleY + difY)];
                
                if(nil != point.ccsprite){
                    CGPoint newPos = CGPointMake(point.ccsprite.position.x, point.offset.y);
                    [point.ccsprite setPosition:newPos];
                    
                    if(point.body != NULL){
                        
                        float angle = [point.ccsprite rotation];
                        point.body->SetTransform(b2Vec2(newPos.x/[[LHSettings sharedInstance] lhPtmRatio], 
                                                        newPos.y/[[LHSettings sharedInstance] lhPtmRatio]), 
                                                 CC_DEGREES_TO_RADIANS(-angle));
                    }
                }
                
                if(nil != movedEndListenerObj && nil != movedEndListenerSEL){
                    [movedEndListenerObj performSelector:movedEndListenerSEL withObject:point.ccsprite];
                }
			}
		}
			break;
		default:
			break;
	}
}
////////////////////////////////////////////////////////////////////////////////
-(void)visit
{
    if([[LHSettings sharedInstance] levelPaused]) //level is paused
        return;
    
    if(paused) //this parallax is paused
        return;
    
    if(NULL != followedSprite)
    {
        CGPoint spritePos = [followedSprite position];
        float deltaFX = lastFollowedSpritePosition.x - spritePos.x;
        float deltaFY = lastFollowedSpritePosition.y - spritePos.y;
        lastFollowedSpritePosition = spritePos;
        
        CGPoint lastNodePosition = [self position];        
        if(followChangeX && !followChangeY){
            [super setPosition:ccp(lastNodePosition.x + deltaFX, 
                                   lastNodePosition.y)];
        }
        else if(!followChangeX && followChangeY){
            [super setPosition:ccp(lastNodePosition.x, 
                                   lastNodePosition.y + deltaFY)];
        }
        else if(followChangeX && followChangeY){
            [super setPosition:ccp(lastNodePosition.x + deltaFX, 
                                   lastNodePosition.y + deltaFY)];
        }
    }
    
	CGPoint pos = [self position];
	if( ! CGPointEqualToPoint(pos, lastPosition) || isContinuous) 
	{
		for(LHParallaxPointObject *point in sprites)
		{
						
			float x = pos.x * point.ratio.x + point.offset.x;
			float y = pos.y * point.ratio.y + point.offset.y;	

            int i = -1; //direction left to right //bottom to up
            if(direction == 1 || direction == 2) //right to left //up to bottom
                i = 1;

			[self setPosition:CGPointMake(x, y) onPoint:point offset:CGPointMake(i*point.ratio.x*speed, i*point.ratio.y*speed)];	
			
			if(isContinuous)
			{
				[self repositionPoint:point];
			
				[point setOffset:ccp(point.offset.x + i*point.ratio.x*speed, 
									 point.offset.y + i*point.ratio.y*speed)];

			}
		}
		lastPosition = pos;
	}
	[super visit];
}
				   
@end