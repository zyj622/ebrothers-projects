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
#include "LHPathNode.h"
#include "LevelHelperLoader.h"
#include "LHSettings.h"
#include "LHSprite.h"
#include "platform.h"
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
/*
@interface NSMutableArray (LHMutableArrayExt)

- (void)moveObjectFromIndex:(NSUInteger)from toIndex:(NSUInteger)to;
- (NSArray *)reversedArray;
- (void)reverse;
@end
////////////////////////////////////////////////////////////////////////////////
@implementation NSMutableArray (LHMutableArrayExt)
////////////////////////////////////////////////////////////////////////////////
- (void)moveObjectFromIndex:(NSUInteger)from toIndex:(NSUInteger)to
{
    if (to != from) {
        id obj = [self objectAtIndex:from];
        [obj retain];
        [self removeObjectAtIndex:from];
        if (to >= [self count]) {
            [self addObject:obj];
        } else {
            [self insertObject:obj atIndex:to];
        }
        [obj release];
    }
}
////////////////////////////////////////////////////////////////////////////////
- (NSArray *)reversedArray {
    NSMutableArray *array = [NSMutableArray arrayWithCapacity:[self count]];
    NSEnumerator *enumerator = [self reverseObjectEnumerator];
    for (id element in enumerator) {
        [array addObject:element];
    }
    return array;
}
////////////////////////////////////////////////////////////////////////////////
- (void)reverse {
    
    if([self count] == 0)
        return;
    
    NSUInteger i = 0;
    NSUInteger j = [self count] - 1;
    while (i < j) {
        [self exchangeObjectAtIndex:i
                  withObjectAtIndex:j];
        
        ++i;
        --j;
    }
}
@end
 */
////////////////////////////////////////////////////////////////////////////////

int LHPathNode::numberOfPathNodes = 0;
LHPathNode::LHPathNode(void){
    
    numberOfPathNodes++;
    speed = 0.2f;
    interval = 0.01f;
    paused = false;
    startAtEndPoint = false;
    isCyclic = false;
    restartOtherEnd = false;
    axisOrientation = 0;
    
    flipX = false;
    flipY = false;
    ccsprite = NULL;
    body = NULL;
    uniqueName = "";

    currentPoint = 0;
    isLine = true;
    
    pathNotifierId = NULL;
    pathNotifierSel= NULL;

    struct timeval start;    
    gettimeofday(&start, NULL);
    double convertedTime = (double)start.tv_sec + ((double)(start.tv_usec))/1000000.0;
    m_time = convertedTime;
    elapsed = 0.0;
}
LHPathNode::~LHPathNode(void){
    
    CCLog("PATH NODE DEALLOC %d %s", --numberOfPathNodes, uniqueName.c_str());    
}

bool LHPathNode::initPathWithPoints(std::vector<CCPoint> points){
        
    pathPoints = points;
    return true;
}
LHPathNode* LHPathNode::nodePathWithPoints(std::vector<CCPoint> points){
    
    LHPathNode *pobNode = new LHPathNode();
    if (pobNode && pobNode->initPathWithPoints(points))
    {
        pobNode->autorelease();
        return pobNode;
    }
    CC_SAFE_DELETE(pobNode);
	return NULL;
}


void LHPathNode::setSprite(LHSprite* sprite){
    
    CCAssert( sprite != NULL, "Sprite must not be nil");
    
    ccsprite = sprite;
	initialAngle = ccsprite->getRotation();
    
    ccsprite->setPathNode(this);
    
    if((int)pathPoints.size() > 0)
        prevPathPosition = pathPoints[0];
    
}

void LHPathNode::setSpeed(float value){

    speed = value;
    
    interval = speed/(pathPoints.size()-1);
}

void LHPathNode::setStartAtEndPoint(bool val){
    
    startAtEndPoint = val;
    
    if(startAtEndPoint)
    {
        std::reverse(pathPoints.begin(),pathPoints.end());
    }
}
////////////////////////////////////////////////////////////////////////////////
float LHPathNode::rotationDegreeFromPoint(CCPoint endPoint, CCPoint startPoint)
{
	float rotateDegree = atan2(fabs(endPoint.x-startPoint.x),
							   fabs(endPoint.y-startPoint.y)) * 180.0f / M_PI;
	if (endPoint.y>=startPoint.y)
	{
		if (endPoint.x>=startPoint.x){
			rotateDegree = 180.0f + rotateDegree;
		}
		else{
			rotateDegree = 180.0f - rotateDegree;
		}
	}
	else{
		if (endPoint.x<=startPoint.x){
		}
		else{
			rotateDegree = 360.0 - rotateDegree;
		}
	}
	return rotateDegree;
}

void LHPathNode::visit(void)
{
    if(LHSettings::sharedInstance()->levelPaused() || paused) //level is paused
    {        
        struct timeval now;    
        gettimeofday(&now, NULL);
        double convertedTime = (double)now.tv_sec + ((double)(now.tv_usec))/1000000.0;
        elapsed += convertedTime - m_time;
        m_time = convertedTime;

        return;
    }
    
	if(0 == ccsprite)
		return;
		
	if(0 == (int)pathPoints.size())
		return;
        
	CCPoint startPosition = pathPoints[currentPoint];
            
	int previousPoint = currentPoint -1;
	if(previousPoint < 0){
		previousPoint = 0;
	}
	
	CCPoint prevPosition = pathPoints[previousPoint];
	CCPoint endPosition = startPosition;
	
	float startAngle = LHPathNode::rotationDegreeFromPoint(startPosition, prevPosition);
	if(currentPoint == 0)
		startAngle = initialAngle+270;
	
	float endAngle = startAngle;
	
	if((currentPoint + 1) < (int)pathPoints.size())
	{
		endPosition = pathPoints[currentPoint+1];
		endAngle = LHPathNode::rotationDegreeFromPoint(endPosition, startPosition);
	}
	else {
		if(isCyclic)
		{
			if(!restartOtherEnd)
                std::reverse(pathPoints.begin(),pathPoints.end());
            
            if(flipX){
                ccsprite->setFlipX(!ccsprite->isFlipX());
            }
            
            if(flipY){
                ccsprite->setFlipY(!ccsprite->isFlipY());
            }
			
			currentPoint = -1;
		}
        
        if(NULL != pathNotifierId)
        {
            (pathNotifierId->*pathNotifierSel)(ccsprite);
            			
			if(!isCyclic)
                paused = true;
        }
	}
	
	if(axisOrientation == 1)
		startAngle += 90.0f;
	if(axisOrientation == 1)
		endAngle += 90.0f;
	
	if(startAngle > 360)
		startAngle -=360;
	if(endAngle > 360)
		endAngle-=360;
	
	
	float t = MIN(1, elapsed/interval);
    
	CCPoint deltaP = ccpSub( endPosition, startPosition );

	CCPoint newPos = ccp((startPosition.x + deltaP.x * t), 
						 (startPosition.y + deltaP.y * t));
            
	
	if(startAngle > 270 && startAngle < 360 &&
	   endAngle > 0 && endAngle < 90){
		startAngle -= 360;
	}
	
	if(startAngle > 0 && startAngle < 90 &&
	   endAngle < 360 && endAngle > 270){
		startAngle += 360;
	}
	
	float deltaA = endAngle - startAngle;
	float newAngle = startAngle + deltaA*t;

	if(newAngle > 360)
		newAngle -= 360;
	
	if(NULL != ccsprite)
    {
        CCPoint sprPos = ccsprite->getPosition();
        
        CCPoint sprDelta = CCPointMake(newPos.x - prevPathPosition.x, newPos.y - prevPathPosition.y);
        ccsprite->setPosition(ccp((sprPos.x + sprDelta.x), 
                                  (sprPos.y + sprDelta.y)));
        
        prevPathPosition = newPos;        
    }

	if(axisOrientation != 0){
		ccsprite->setRotation(newAngle);
    }
	if(isLine){
        if(axisOrientation != 0){    
            ccsprite->setRotation(endAngle);
        }
    }
	
	
	float dist = ccpDistance(prevPathPosition, endPosition);
	
	if(0.001 > dist)
	{
		if(currentPoint + 1 < (int)pathPoints.size())
		{
			elapsed = 0.0;
			currentPoint += 1;     
		}
	}

	//updating all the shapes if any
	if(NULL != body)
	{
		if(b2_dynamicBody != body->GetType()) //we dont update dynamic bodies
		{
			if(NULL != ccsprite)
			{
				float angle = ccsprite->getRotation();
				CCPoint pos = ccsprite->getPosition();
                body->SetTransform(b2Vec2(pos.x/LHSettings::sharedInstance()->lhPtmRatio(), 
                                          pos.y/LHSettings::sharedInstance()->lhPtmRatio()),
                                   CC_DEGREES_TO_RADIANS(-angle));
			}
		}
	}
    
	/////////////////////////////////////////
	
    CCNode::visit();
   
    struct timeval now;    
    gettimeofday(&now, NULL);
    double convertedTime = (double)now.tv_sec + ((double)(now.tv_usec))/1000000.0;
    elapsed += convertedTime - m_time;
    m_time = convertedTime;

}
////////////////////////////////////////////////////////////////////////////////
