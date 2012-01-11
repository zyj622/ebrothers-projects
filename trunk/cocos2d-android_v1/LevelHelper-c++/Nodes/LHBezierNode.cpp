//  This file was generated by LevelHelper
//  http://www.levelhelper.org
//
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
#include "LHBezierNode.h"
#include "LevelHelperLoader.h"
#include "LHPathNode.h"
#include "LHSettings.h"
#include "LHSprite.h"

int LHBezierNode::numberOfBezierNodes = 0;
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
void LHBezierNode::init(void){
    
}
////////////////////////////////////////////////////////////////////////////////
LHBezierNode::~LHBezierNode(void){
    
    CCLog("LHBezierNode destructor %d", --numberOfBezierNodes);
    
//    for(int i = 0; i < pathNodes.count(); ++i)
//    {
//        LHPathNode* pNode = pathNodes.getObjectAtIndex(i);
//        pNode->removeFromParentAndCleanup(true);
//	}
//	pathNodes.removeAllObjects();
	
	if(0 != body)
	{
		b2World* _world = body->GetWorld();
		if(0 != _world)
		{
			_world->DestroyBody(body);
			body = NULL;
		}
	}
    
    pathPoints.clear();
	linesHolder.clear();
    trianglesHolder.clear();
}
////////////////////////////////////////////////////////////////////////////////
LHBezierNode::LHBezierNode(void){
 
    texture = NULL;
    body = NULL;
    numberOfBezierNodes++;
}
////////////////////////////////////////////////////////////////////////////////
bool LHBezierNode::initWithDictionary(LHDictionary* bezierDict,
                                      CCLayer* ccLayer, 
                                      b2World* world){
    
    isClosed	= bezierDict->objectForKey("IsClosed")->boolValue();
    isTile		= bezierDict->objectForKey("IsTile")->boolValue();
    isVisible	= bezierDict->objectForKey("IsDrawable")->boolValue();
    isLine		= bezierDict->objectForKey("IsSimpleLine")->boolValue();
    isPath		= bezierDict->objectForKey("IsPath")->boolValue();
    
    uniqueName  = bezierDict->objectForKey("UniqueName")->stringValue();
    
    setTag(bezierDict->objectForKey("Tag")->intValue());
    setVertexZ(bezierDict->objectForKey("ZOrder")->intValue());
    
    std::string img = bezierDict->objectForKey("Image")->stringValue();
    imageSize = CCSizeZero;
    if(img != "")
    {
        std::string path = LHSettings::sharedInstance()->imagePath(img.c_str());
        texture = CCTextureCache::sharedTextureCache()->addImage(path.c_str());
        if( texture ) {
            imageSize = texture->getContentSize();
        }
    }
    
    winSize = CCDirector::sharedDirector()->getWinSize();		
    
    
    color = CCRectFromString(bezierDict->objectForKey("Color")->stringValue().c_str());
    lineColor = CCRectFromString(bezierDict->objectForKey("LineColor")->stringValue().c_str());
    lineWidth = bezierDict->objectForKey("LineWidth")->floatValue();
    
    initTileVerticesFromDictionary(bezierDict);
    initPathPointsFromDictionary(bezierDict);	
    createBodyFromDictionary(bezierDict,world);
    
    return true;
}
////////////////////////////////////////////////////////////////////////////////
LHBezierNode* LHBezierNode::nodeWithDictionary(LHDictionary* properties,
                                               CCLayer* ccLayer, 
                                               b2World* world){
    
    LHBezierNode *pobBNode = new LHBezierNode();
	if (pobBNode && pobBNode->initWithDictionary(properties, ccLayer, world))
    {
	    pobBNode->autorelease();
        return pobBNode;
    }
    CC_SAFE_DELETE(pobBNode);
	return NULL;
}
////////////////////////////////////////////////////////////////////////////////
LHPathNode* LHBezierNode::addSpriteOnPath(LHSprite* spr, 
                                          float   pathSpeed, 
                                          bool    startAtEndPoint,
                                          bool    isCyclic,
                                          bool    restartOtherEnd,
                                          int     axis,
                                          bool    flipx,
                                          bool    flipy,
                                          bool    deltaMove){
    
    
    
	LHPathNode* node = LHPathNode::nodePathWithPoints(pathPoints);	
    node->setStartAtEndPoint(startAtEndPoint);
	node->setSprite(spr);
	node->setBody(spr->getBody());
    
    if(!deltaMove){
        if((int)pathPoints.size() > 0)
        {
            CCPoint pathPos = pathPoints[0];
            spr->transformPosition(pathPos);
        }
    }
    
	node->setSpeed(pathSpeed);
    node->setRestartOtherEnd(restartOtherEnd);
	node->setIsCyclic(isCyclic);
	node->setAxisOrientation(axis);
	node->setIsLine(isLine);
    node->setFlipX(flipx);
    node->setFlipY(flipy);
    node->setUniqueName(uniqueName.c_str());
    //pathNodes.addObject(node);
	    
    this->getParent()->addChild(node);
    
    return  node;

}
////////////////////////////////////////////////////////////////////////////////
CCPoint LHBezierNode::pointOnCurve(CCPoint p1, CCPoint p2, CCPoint p3, CCPoint p4, float t){    
	float var1, var2, var3;
    CCPoint vPoint(0.0f, 0.0f);
    
    var1 = 1 - t;
    var2 = var1 * var1 * var1;
    var3 = t * t * t;
    vPoint.x = var2*p1.x + 3*t*var1*var1*p2.x + 3*t*t*var1*p3.x + var3*p4.x;
    vPoint.y = var2*p1.y + 3*t*var1*var1*p2.y + 3*t*t*var1*p3.y + var3*p4.y;
    return(vPoint);				
}
////////////////////////////////////////////////////////////////////////////////
void LHBezierNode::initTileVerticesFromDictionary(LHDictionary* bezierDict)
{
	//trianglesHolder = [[NSMutableArray alloc] init];
	
	CCPoint convert = LHSettings::sharedInstance()->convertRatio();
	LHArray* fixtures = bezierDict->arrayForKey("TileVertices");
    
    if(NULL != fixtures)
    {
    for(int i = 0; i < fixtures->count(); ++i)
    {
        LHArray* fix = fixtures->objectAtIndex(i)->arrayValue();
        
        std::vector<CCPoint> triagle;
        
        for(int j = 0; j < fix->count(); ++j)
		{
			CCPoint point = LHPointFromString(fix->objectAtIndex(j)->stringValue());
			
            CCPoint pos_offset = LHSettings::sharedInstance()->possitionOffset();
            point.x += pos_offset.x;
            point.y += pos_offset.y;
            
			point.x = point.x* convert.x;
			point.y = winSize.height - point.y*convert.y;
			
            CCLog("Push point %f %f", point.x, point.y);
            triagle.push_back(point);
		}
		
        CCLog("Push triangle ");
        trianglesHolder.push_back(triagle);
	}	
	}
	
	//linesHolder = [[NSMutableArray alloc] init];
	if(isVisible)
	{
		LHArray* curvesInShape = bezierDict->objectForKey("Curves")->arrayValue();
		
		int MAX_STEPS = 25;
		
        for(int i = 0; i < curvesInShape->count(); ++i)
		{
            LHDictionary* curvDict = curvesInShape->objectAtIndex(i)->dictValue();
            
			CCPoint endCtrlPt   = LHPointFromString(curvDict->objectForKey("EndControlPoint")->stringValue());
			CCPoint startCtrlPt = LHPointFromString(curvDict->objectForKey("StartControlPoint")->stringValue());
			CCPoint endPt       = LHPointFromString(curvDict->objectForKey("EndPoint")->stringValue());
			CCPoint startPt     = LHPointFromString(curvDict->objectForKey("StartPoint")->stringValue());
			
            CCPoint pos_offset = LHSettings::sharedInstance()->possitionOffset();
            
            endCtrlPt.x += pos_offset.x;
            endCtrlPt.y += pos_offset.y;
            
            startCtrlPt.x += pos_offset.x;
            startCtrlPt.y += pos_offset.y;
            
            endPt.x += pos_offset.x;
            endPt.y += pos_offset.y;
            
            startPt.x += pos_offset.x;
            startPt.y += pos_offset.y;
            
			if(!isLine)
			{
				CCPoint prevPoint;
				bool firstPt = true;
				
				for(float t = 0; t <= (1 + (1.0f / MAX_STEPS)); t += 1.0f / MAX_STEPS)
				{
					CCPoint vPoint = LHBezierNode::pointOnCurve(startPt,
                                                                startCtrlPt,
                                                                endCtrlPt,
                                                                endPt,
                                                                t);
					
					if(!firstPt)
					{
						CCPoint pt1 = CCPointMake(prevPoint.x*convert.x, 
												  winSize.height - prevPoint.y*convert.y);
						CCPoint pt2 = CCPointMake(vPoint.x*convert.x, 
												  winSize.height - vPoint.y*convert.y);
						
                        linesHolder.push_back(pt1);
                        linesHolder.push_back(pt2);
					}
					prevPoint = vPoint;
					firstPt = false;					
				}
			}
			else
			{
				
				CCPoint pos1 = CCPointMake(startPt.x*convert.x, 
										   winSize.height - startPt.y*convert.y);
				CCPoint pos2 = CCPointMake(endPt.x*convert.x, 
										   winSize.height - endPt.y*convert.y);
				
                linesHolder.push_back(pos1);
                linesHolder.push_back(pos2);				
			}
		}
	}
}
////////////////////////////////////////////////////////////////////////////////
void LHBezierNode::initPathPointsFromDictionary(LHDictionary* bezierDict)
{
	//pathPoints = [[NSMutableArray alloc] init];
	
    LHArray* curvesInShape = bezierDict->objectForKey("Curves")->arrayValue();    
    int MAX_STEPS = 25;    
	CCPoint conv = LHSettings::sharedInstance()->convertRatio();
	
	int i = 0;
    for(int j = 0; j < curvesInShape->count(); ++j)
    {
        LHDictionary* curvDict = curvesInShape->objectAtIndex(j)->dictValue();
        
        CCPoint endCtrlPt   = LHPointFromString(curvDict->objectForKey("EndControlPoint")->stringValue());
        CCPoint startCtrlPt = LHPointFromString(curvDict->objectForKey("StartControlPoint")->stringValue());
        CCPoint endPt       = LHPointFromString(curvDict->objectForKey("EndPoint")->stringValue());
        CCPoint startPt     = LHPointFromString(curvDict->objectForKey("StartPoint")->stringValue());
		
		CCPoint pos_offset = LHSettings::sharedInstance()->possitionOffset();
        endCtrlPt.x += pos_offset.x;
        endCtrlPt.y += pos_offset.y;
        
        startCtrlPt.x += pos_offset.x;
        startCtrlPt.y += pos_offset.y;
        
        endPt.x += pos_offset.x;
        endPt.y += pos_offset.y;
        
        startPt.x += pos_offset.x;
        startPt.y += pos_offset.y;
        
		if(!isLine)
        {
            for(float t = 0; t <= (1 + (1.0f / MAX_STEPS)); t += 1.0f / MAX_STEPS)
            {
                CCPoint vPoint = LHBezierNode::pointOnCurve(startPt,
                                                            startCtrlPt,
                                                            endCtrlPt,
                                                            endPt,
                                                            t);
				
                pathPoints.push_back(ccp(vPoint.x*conv.x, winSize.height - vPoint.y*conv.y));
            }
			
            pathPoints.pop_back();
        }
        else
        {
            pathPoints.push_back(ccp(startPt.x*conv.x, 
                                     winSize.height - startPt.y*conv.y));

            
            if(i == curvesInShape->count()-1)
            {
                pathPoints.push_back(ccp(endPt.x*conv.x,
                                         winSize.height - endPt.y*conv.y));
            }
            ++i;            
        }
	}		
}
////////////////////////////////////////////////////////////////////////////////
void LHBezierNode::createBodyFromDictionary(LHDictionary* bezierDict, b2World* world)
{
	if(isPath)
		return;
	
	if((int)pathPoints.size() < 2)
		return;
	
	b2BodyDef bodyDef;	
	
	int bodyType = bezierDict->objectForKey("PhysicType")->intValue();
	if(bodyType > 2)
        return;
        
	bodyDef.type = (b2BodyType)bodyType;
    
	bodyDef.position.Set(0.0f, 0.0f);
	bodyDef.angle = 0.0f;
	
	bodyDef.userData = this;
	
	body = world->CreateBody(&bodyDef);
	
    
  
	float ptm = LHSettings::sharedInstance()->lhPtmRatio();
    
    if(b2_version.major <= 2)
        if(b2_version.minor <=2)
            if(b2_version.revision <2)
                CCLog("Please update to Box2d 2.2.2 or above or else you may experience asserts");
    
    for(int k =0; k< (int)trianglesHolder.size();++k)
    {
        std::vector<CCPoint> fix = trianglesHolder[k];
        
        int size = fix.size();
        b2Vec2 *verts = new b2Vec2[size];
        int i = 0;
        for(int j = 0; j < size; ++j)
        {
            CCPoint pt = fix[j];
            
            verts[i].x =pt.x/ptm;
            verts[i].y =pt.y/ptm;
            ++i;
        }

        b2PolygonShape shape;
        shape.Set(verts, size);		
        
        b2FixtureDef fixture;
        
        fixture.density = bezierDict->objectForKey("Density")->floatValue();
		fixture.friction = bezierDict->objectForKey("Friction")->floatValue();
		fixture.restitution = bezierDict->objectForKey("Restitution")->floatValue();
		
		fixture.filter.categoryBits = bezierDict->objectForKey("Category")->intValue();
		fixture.filter.maskBits = bezierDict->objectForKey("Mask")->intValue();
		fixture.filter.groupIndex = bezierDict->objectForKey("Group")->intValue();
		
		fixture.isSensor = bezierDict->objectForKey("IsSenzor")->boolValue();
        
        fixture.shape = &shape;
        body->CreateFixture(&fixture);
        delete[] verts;
    }		
    
    bool firstPoint = true;
    CCPoint prevPoint = ccp(0,0);
    for(int i = 0; i < (int)pathPoints.size();++i)
    {
        CCPoint pt = pathPoints[i];
                
        if(!firstPoint)
        {
            int size = 2;
            b2Vec2 *verts = new b2Vec2[size];
            
            verts[0].x =prevPoint.x/ptm;
            verts[0].y =prevPoint.y/ptm;
            verts[1].x =pt.x/ptm;
            verts[1].y =pt.y/ptm;
            
            b2EdgeShape shape;
            shape.Set(verts[0], verts[1]);
            
            b2FixtureDef fixture;
            
            fixture.density = bezierDict->objectForKey("Density")->floatValue();
            fixture.friction = bezierDict->objectForKey("Friction")->floatValue();
            fixture.restitution = bezierDict->objectForKey("Restitution")->floatValue();
            
            fixture.filter.categoryBits = bezierDict->objectForKey("Category")->intValue();
            fixture.filter.maskBits = bezierDict->objectForKey("Mask")->intValue();
            fixture.filter.groupIndex = bezierDict->objectForKey("Group")->intValue();
            
            fixture.isSensor = bezierDict->objectForKey("IsSenzor")->boolValue();
            
            fixture.shape = &shape;
            body->CreateFixture(&fixture);
            delete[] verts;
        }
        
        firstPoint = false;
        prevPoint = pt;
    }        
}
////////////////////////////////////////////////////////////////////////////////
void LHBezierNode::draw(void)
{
	if(0.0f != LHSettings::sharedInstance()->customAlpha())
	{
		glColor4f(color.origin.x, 
				  color.origin.y, 
				  color.size.width, 
				  color.size.height*LHSettings::sharedInstance()->customAlpha());
		glPushMatrix();
		
        glDisableClientState(GL_COLOR_ARRAY);
        
        if(NULL != texture)
        {
		glEnable(GL_TEXTURE_2D);		
		glBindTexture(GL_TEXTURE_2D, texture->getName());
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
        for(int k = 0; k < (int)trianglesHolder.size();++k)
		{
            std::vector<CCPoint> fix = trianglesHolder[k];
            
			int size = (int)fix.size();
			CCPoint* glVertices = new CCPoint[size];
			CCPoint* glUV = new CCPoint[size];
			int i = 0;
            for(int j = 0; j < size; ++j)
			{
                CCPoint pt = fix[j];

				glVertices[i].x =pt.x;
				glVertices[i].y =pt.y;
				
				glUV[i].x = pt.x/imageSize.width;
				glUV[i].y = (winSize.height - pt.y)/imageSize.height;
				++i;
			}
			glTexCoordPointer(2, GL_FLOAT, 0, glUV);
			glVertexPointer(2, GL_FLOAT, 0, glVertices);
			glDrawArrays(GL_TRIANGLE_FAN, 0, size);
			delete[] glVertices;
			delete[] glUV;
		}		
        
		}
        
		float oldLineWidth = 1.0f;
		glGetFloatv(GL_LINE_WIDTH, &oldLineWidth); 
		
		glLineWidth(lineWidth);
		
		glDisable(GL_TEXTURE_2D);
		glColor4f(lineColor.origin.x, 
				  lineColor.origin.y, 
				  lineColor.size.width, 
				  lineColor.size.height*LHSettings::sharedInstance()->customAlpha());
		
        for(int i = 0; i < (int)linesHolder.size(); i+=2)
		{
			CCPoint pt1 = linesHolder[i];
			CCPoint pt2 = linesHolder[i+1];
            
			CCPoint* line = new CCPoint[2];
			line[0].x = pt1.x;
			line[0].y = pt1.y;
				
			line[1].x = pt2.x;
			line[1].y = pt2.y;
			
			glVertexPointer(2, GL_FLOAT, 0, line);
			glDrawArrays(GL_LINES, 0, 2);
				
			delete[] line;
		}
        
        glEnableClientState(GL_COLOR_ARRAY);
		glLineWidth(oldLineWidth);
		glEnable(GL_TEXTURE_2D);	
		glPopMatrix();
	}	
}
////////////////////////////////////////////////////////////////////////////////
