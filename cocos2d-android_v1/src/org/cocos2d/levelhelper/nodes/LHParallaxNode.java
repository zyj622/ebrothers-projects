package org.cocos2d.levelhelper.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import org.cocos2d.levelhelper.LHObject;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class LHParallaxNode extends CCNode {
	boolean isContinuous;
	int direction;
	float speed;
	boolean paused;
	
	CGPoint lastPosition;
    String uniqueName;
	    
    Object movedEndListenerObj;
    String movedEndListenerSEL;
    
	CGSize winSize;
	
	int screenNumberOnTheRight;
	int screenNumberOnTheLeft;
	int screenNumberOnTheTop;
	int screenNumberOnTheBottom;
	
    ArrayList<LHParallaxPointObject> sprites;
    
    LHSprite followedSprite;
    CGPoint lastFollowedSpritePosition;
    boolean followChangeX;
    boolean followChangeY;
    
    public LHParallaxNode() {
    	sprites = new ArrayList<LHParallaxPointObject>();
    }
    
	boolean initWithDictionary(HashMap<String, LHObject> parallaxDict){

	    if(null == parallaxDict)
	        return false;
	    
	    followedSprite = null;
	    isContinuous = parallaxDict.get("ContinuousScrolling").boolValue();
	    direction = parallaxDict.get("Direction").intValue();
	    speed = parallaxDict.get("Speed").floatValue();
	    lastPosition = CGPoint.make(-100,-100);
	    paused = false;
	    winSize = CCDirector.sharedDirector().winSize();
	    screenNumberOnTheRight = 1;
	    screenNumberOnTheLeft = 0;
	    screenNumberOnTheTop = 0;
	    
	    movedEndListenerObj = null;
	    movedEndListenerSEL = null;
	    
	    uniqueName  = parallaxDict.get("UniqueName").stringValue();
	    if(!isContinuous)
	        speed = 1.0f;

	    return true;
	}
	////////////////////////////////////////////////////////////////////////////////
	LHParallaxNode nodeWithDictionary(HashMap<String, LHObject> properties){
	    LHParallaxNode pobNode = new LHParallaxNode();
		if (pobNode!=null && pobNode.initWithDictionary(properties))
	    {
	        return pobNode;
	    }
		return null;
	}
	////////////////////////////////////////////////////////////////////////////////
	void addChild(LHSprite sprite, CCPoint ratio)
	{
		assert( sprite != null):"Argument must be non-nil";
		
		LHParallaxPointObject obj = LHParallaxPointObject.pointWithCCPoint(ratio);
		obj.ccsprite = sprite;
	    sprite.setParallaxNode(this);
		obj.body = sprite.getBody();
		obj.position = sprite.getPosition();
		obj.offset = sprite.getPosition();
		obj.initialPosition = sprite.getPosition();
	    sprites.addObject(obj);
	    
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
	void removeChild(LHSprite sprite)
	{
	    if(null == sprite) 
	        return;
	    int size = sprites.size();
		for(int i = 0; i< size; ++i)
	    {        
	        LHParallaxPointObject pt = sprites.getObjectAtIndex(i);
	        if(pt.ccsprite.equals( sprite))
	        {
	            sprites.removeObject(pt);
	            return;
	        }
		}
	}
	////////////////////////////////////////////////////////////////////////////////
	void registerSpriteHasMovedToEndListener(Object object, String method)
	{
	    movedEndListenerObj = object; 
	    movedEndListenerSEL = method;
	}
	////////////////////////////////////////////////////////////////////////////////
	ArrayList<LHSprite> spritesInNode()
	{
		ArrayList<LHSprite> sprs =new ArrayList<LHSprite>();
		for (LHParallaxPointObject pt : sprites) {
			if(null != pt.ccsprite)
	            sprs.add((LHSprite)pt.ccsprite);
		}
	    return sprs;
	}
	////////////////////////////////////////////////////////////////////////////////
	ArrayList<Body> bodiesInNode(){
	    
	    ArrayList<Body> sprs;
	    
	    ArrayList<LHParallaxPointObject*>.iterator it;
	    
	    for(it = sprites.begin(); it < sprites.end(); ++it)
	    {        
	        LHParallaxPointObject* pt = *it;
	        
	        if(null != pt.body)
	            sprs.push_back(pt.body);
	    }
	    return sprs;
	}
	////////////////////////////////////////////////////////////////////////////////
	void setPositionOnPointWithOffset(const CCPoint& pos, 
	                                                  LHParallaxPointObject* point, 
	                                                  const CCPoint& offset)
	{
	    if(!isContinuous)
	    {
	        if(point.ccsprite != null){
	            point.ccsprite.setPosition(pos);
	        
	            if(point.body != null){
	            
	                float angle = point.ccsprite.getRotation();
	                point.body.SetAwake(true);
	                
	                point.body.SetTransform(b2Vec2(pos.x/LHSettings.sharedInstance().lhPtmRatio(), 
	                                                 pos.y/LHSettings.sharedInstance().lhPtmRatio()), 
	                                         CC_DEGREES_TO_RADIANS(-angle));
	            }
	        }
	    }
	    else
	    {

	        if(point.ccsprite != null){
	            
	            CCPoint newPos = CCPointMake(point.ccsprite.getPosition().x - offset.x,
	                                         point.ccsprite.getPosition().y - offset.y);
	            point.ccsprite.setPosition(newPos);
	            
	            if(point.body != null){
	            
	            float angle = point.ccsprite.getRotation();
	            point.body.SetTransform(b2Vec2(newPos.x/LHSettings.sharedInstance().lhPtmRatio(), 
	                                             newPos.y/LHSettings.sharedInstance().lhPtmRatio()), 
	                                     CC_DEGREES_TO_RADIANS(-angle));
	            }
	            
	        }
	    }
	}
	////////////////////////////////////////////////////////////////////////////////
	CCSize getBounds(float rw, float rh, float radians)
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
	 
	    return CCSizeMake(x_max-x_min, y_max-y_min);
	}
	////////////////////////////////////////////////////////////////////////////////
	void repositionPoint(LHParallaxPointObject* point)
	{
		CCSize spriteContentSize = point.ccsprite.getContentSize();
	    
	    float angle = point.ccsprite.getRotation();
	    float rotation = CC_DEGREES_TO_RADIANS(angle);
		float scaleX = point.ccsprite.getScaleX();
		float scaleY = point.ccsprite.getScaleY();
	    
	    CCSize contentSize = getBounds(spriteContentSize.width,
	                                   spriteContentSize.height,
	                                   rotation);
	        
		switch (direction) {
			case 1: //right to left
			{
				if(point.ccsprite.getPosition().x + contentSize.width/2.0f*scaleX <= 0)
				{
					float difX = point.ccsprite.getPosition().x + contentSize.width/2.0f*scaleX;
			
					point.setOffset(ccp(winSize.width*screenNumberOnTheRight - point.ratio.x*speed -  contentSize.width/2.0f*scaleX + difX, 
	                                     point.offset.y));
		
	                if(null != point.ccsprite){
	                    CCPoint newPos = CCPointMake(point.offset.x, point.ccsprite.getPosition().y);
	                    point.ccsprite.setPosition(newPos);
	                
	                    if(point.body != null){
	                    
	                        float angle = point.ccsprite.getRotation();
	                        point.body.SetTransform(b2Vec2(newPos.x/LHSettings.sharedInstance().lhPtmRatio(), 
	                                                         newPos.y/LHSettings.sharedInstance().lhPtmRatio()), 
	                                                 CC_DEGREES_TO_RADIANS(-angle));
	                    }
	                }
	                    
	                
	                if(null != movedEndListenerObj){
	                    (movedEndListenerObj.*movedEndListenerSEL)(point.ccsprite);
	                }
				}
			}	
				break;
				
			case 0://left to right
			{
				if(point.ccsprite.getPosition().x - contentSize.width/2.0f*scaleX >= winSize.width)
				{
					float difX = point.ccsprite.getPosition().x - contentSize.width/2.0f*scaleX - winSize.width;
					
					point.setOffset(ccp(winSize.width*screenNumberOnTheLeft + point.ratio.x*speed +  contentSize.width/2.0f*scaleX + difX, 
	                                     point.offset.y));
	                
	                
	                
	                if(null != point.ccsprite){
	                    CCPoint newPos = CCPointMake(point.offset.x, point.ccsprite.getPosition().y);
	                    point.ccsprite.setPosition(newPos);
	                    
	                    if(point.body != null){
	                        
	                        float angle = point.ccsprite.getRotation();
	                        point.body.SetTransform(b2Vec2(newPos.x/LHSettings.sharedInstance().lhPtmRatio(), 
	                                                         newPos.y/LHSettings.sharedInstance().lhPtmRatio()), 
	                                                 CC_DEGREES_TO_RADIANS(-angle));
	                    }
	                }

	                
	                if(null != movedEndListenerObj){
	                    (movedEndListenerObj.*movedEndListenerSEL)(point.ccsprite);
	                }
				}
			}
				break;
				
			case 2://up to bottom
			{
				if(point.ccsprite.getPosition().y + contentSize.height/2.0f*scaleY <= 0)
				{
					float difY = point.ccsprite.getPosition().y + contentSize.height/2.0f*scaleY;
					
					point.setOffset(ccp(point.offset.x, 
	                                     winSize.height*screenNumberOnTheTop - point.ratio.y*speed - contentSize.height/2.0f*scaleY + difY));
	                
	                
	                if(null != point.ccsprite){
	                    CCPoint newPos = CCPointMake(point.ccsprite.getPosition().x, point.offset.y);
	                    point.ccsprite.setPosition(newPos);
	                    
	                    if(point.body != null){
	                        
	                        float angle = point.ccsprite.getRotation();
	                        point.body.SetTransform(b2Vec2(newPos.x/LHSettings.sharedInstance().lhPtmRatio(), 
	                                                         newPos.y/LHSettings.sharedInstance().lhPtmRatio()), 
	                                                  CC_DEGREES_TO_RADIANS(-angle));
	                    }
	                }
	                
	                if(null != movedEndListenerObj){
	                    (movedEndListenerObj.*movedEndListenerSEL)(point.ccsprite);
	                }
				}
			}
				break;
				
			case 3://bottom to top
			{
				if(point.ccsprite.getPosition().y - contentSize.height/2.0f*scaleY >= winSize.height)
				{
					float difY = point.ccsprite.getPosition().y - contentSize.height/2.0f*scaleY - winSize.height;
					
					point.setOffset(ccp(point.offset.x, 
	                                     winSize.height*screenNumberOnTheBottom + point.ratio.y*speed + contentSize.height/2.0f*scaleY + difY));
	                
	                if(null != point.ccsprite){
	                    CCPoint newPos = CCPointMake(point.ccsprite.getPosition().x, point.offset.y);
	                    point.ccsprite.setPosition(newPos);
	                    
	                    if(point.body != null){
	                        
	                        float angle = point.ccsprite.getRotation();
	                        point.body.SetTransform(b2Vec2(newPos.x/LHSettings.sharedInstance().lhPtmRatio(), 
	                                                         newPos.y/LHSettings.sharedInstance().lhPtmRatio()), 
	                                                 CC_DEGREES_TO_RADIANS(-angle));
	                    }
	                }
	                
	                if(null != movedEndListenerObj){
	                    (movedEndListenerObj.*movedEndListenerSEL)(point.ccsprite);
	                }
				}
			}
				break;
			default:
				break;
		}
	}
	////////////////////////////////////////////////////////////////////////////////
	void setPosition(CCPoint newPosition)
	{
	    CCNode.setPosition(newPosition);
	    visit();
	}
	////////////////////////////////////////////////////////////////////////////////
	void setFollowSprite(LHSprite* sprite, 
	                                     boolean changeXPosition, 
	                                     boolean changeYPosition){
	    
	    if(null == sprite)
	    {
	        if(null != followedSprite)
	            followedSprite.parallaxFollowingThisSprite = null;
	    }
	    
	    followedSprite = sprite;
	    
	    followChangeX = changeXPosition;
	    followChangeY = changeYPosition;
	    
	    if(null != sprite)
	    {
	        lastFollowedSpritePosition = sprite.getPosition();
	        sprite.parallaxFollowingThisSprite = this;
	    }
	}
	////////////////////////////////////////////////////////////////////////////////
	void visit(void)
	{
	    if(LHSettings.sharedInstance().levelPaused()) //level is paused
	        return;
	    
	    if(paused) //this parallax is paused
	        return;
	    
	    
	    if(null != followedSprite)
	    {
	        float deltaFX = lastFollowedSpritePosition.x - followedSprite.getPosition().x;
	        float deltaFY = lastFollowedSpritePosition.y - followedSprite.getPosition().y;
	        lastFollowedSpritePosition = followedSprite.getPosition();
	 
	        CCPoint lastPosition = this.getPosition();        
	        if(followChangeX && !followChangeY)
	        {
	            CCNode.setPosition(ccp(lastPosition.x + deltaFX, lastPosition.y));
	        }
	        else if(!followChangeX && followChangeY)
	        {
	            CCNode.setPosition(ccp(lastPosition.x, lastPosition.y + deltaFY));
	        }
	        else if(followChangeX && followChangeY)
	        {
	            CCNode.setPosition(ccp(lastPosition.x + deltaFX, lastPosition.y + deltaFY));
	        }
	    }
	    
		CCPoint pos = getPosition();
		if( ! CCPoint.CCPointEqualToPoint(pos, lastPosition) || isContinuous) 
		{
	        for(int k = 0; k < sprites.count(); ++k)
	        {
	            LHParallaxPointObject* point = sprites.getObjectAtIndex(k);
							
				float x = pos.x * point.ratio.x + point.offset.x;
				float y = pos.y * point.ratio.y + point.offset.y;	

	            int i = -1; //direction left to right //bottom to up
	            if(direction == 1 || direction == 2) //right to left //up to bottom
	                i = 1;

	            setPositionOnPointWithOffset(CCPointMake(x, y), 
	                                         point, 
	                                         CCPointMake(i*point.ratio.x*speed, 
	                                                     i*point.ratio.y*speed));

				if(isContinuous)
				{
					repositionPoint(point);
				
					point.setOffset(ccp(point.offset.x + i*point.ratio.x*speed, 
										 point.offset.y + i*point.ratio.y*speed));

				}
			}
			lastPosition = pos;
		}
	
	class LHParallaxPointObject
	{
		public CCPoint position;
	public CGPoint	ratio;
	public CGPoint offset;
	public CGPoint initialPosition;
	public CGNode ccsprite;	//weak ref
	public Body body;		//weak ref
	    

	    bool initWithCCPoint(CCPoint point){
	        ratio = point;
	        return true;
	    }
	    
	    void setOffset(CGPoint pt){offset = pt;}
	    
	    static LHParallaxPointObject pointWithCCPoint(CCPoint point){

	        LHParallaxPointObject pobPoint = new LHParallaxPointObject();
	        if (pobPoint != null&& pobPoint.initWithCCPoint(point))
	        {
	            return pobPoint;
	        }
	        return null;
	    }
	}

}
