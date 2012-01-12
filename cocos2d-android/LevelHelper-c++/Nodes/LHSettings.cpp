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

#include "LHSettings.h"
#include <iostream>
#include <fstream>

LHSettings *LHSettings::m_sharedInstance = 0;

////////////////////////////////////////////////////////////////////////////////
LHSettings* LHSettings::sharedInstance(){
	if (0 == m_sharedInstance){
		m_sharedInstance = new LHSettings();
	}
    return m_sharedInstance;
}
////////////////////////////////////////////////////////////////////////////////
LHSettings::~LHSettings()
{
    
}
////////////////////////////////////////////////////////////////////////////////
LHSettings::LHSettings()
{
    m_useRetinaOnIpad = true;
    m_convertLevel = true;
    m_lhPtmRatio = 32.0f;
    m_customAlpha = 1.0f;
    m_convertRatio = CCPointMake(1, 1);
    m_realConvertRatio = CCPointMake(1, 1);
    m_newBodyId = 0;
    m_stretchArt = true;
    m_possitionOffset = CCPointMake(0.0f, 0.0f);
    m_levelPaused = false;
    m_imagesFolder = std::string("");
    m_isCoronaUser = false;
    m_preloadBatchNodes = false;
}
////////////////////////////////////////////////////////////////////////////////
int LHSettings::newBodyId(void)
{
	return m_newBodyId++;
}

void LHSettings::setImageFolder(const char* img){

    if(0 != img)
        m_imagesFolder = std::string(img);

}
const std::string& LHSettings::imageFolder(void){
    return m_imagesFolder;
}

const std::string LHSettings::imagePath(const std::string& image){
    
    //CCLog("Implement getImagePath()");

    if(isIpad())
    {   
        std::string file(image);
        
        size_t found;
        found=file.find_last_of(".");
        
        file.insert(found,"-hd");   
        
        const char* path = CCFileUtils::fullPathFromRelativePath(file.c_str());
        
        
        std::ifstream infile;
        infile.open (path);
        
        if(true == infile.is_open()) //IF THIS FAILS IT MEANS WE HAVE NO -hd file
        {
            infile.close();
            return std::string(path);
        }
        
        return image;
    }
    
    return image;
}
bool LHSettings::shouldScaleImageOnRetina(const std::string& image)
{
    //if it contains -hd return true - else return false
    if(std::string::npos != image.find("-hd")){
        return true; 
    }
        
    return false;
}

bool LHSettings::isIpad(void){
        
    CCSize wSize = CCDirector::sharedDirector()->getWinSizeInPixels();
    
    if((wSize.width == 1024 || wSize.width == 768) && 
       (wSize.height == 1024 || wSize.height == 768))
    {
        return true;
    }
    
    return false;
}


void LHSettings::setStretchArt(const bool& value){
    m_stretchArt = value;
    m_possitionOffset.x =0.0f;
    m_possitionOffset.y =0.0f;   
}

CCPoint LHSettings::possitionOffset(void){
    return m_possitionOffset;
}
void LHSettings::setConvertRatio(CCPoint val){
    
    m_convertRatio = val;
    m_realConvertRatio = val;
    if(!m_stretchArt)
    {
        if(isIpad())
        {
            if(m_convertRatio.x > 1.0 || m_convertRatio.y > 1.0f)
            {
                m_convertRatio.x = 2.0f;
                m_convertRatio.y = 2.0f;
                                
                if(CCDirector::sharedDirector()->getWinSize().width == 1024.0f)
                {
                    m_possitionOffset.x = 32.0f;
                    m_possitionOffset.y = 64.0f;   
                }
                else {
                    m_possitionOffset.x = 64.0f;
                    m_possitionOffset.y = 32.0f;
                }
            }
        }
    }

}
CCPoint LHSettings::convertRatio(void){
    if(!m_convertLevel)
		return CCPointMake(1, 1);
	
	return m_convertRatio;
}
CCPoint LHSettings::realConvertRatio(void){
    if(!m_convertLevel)
		return CCPointMake(1, 1);
	
	return m_realConvertRatio;
}