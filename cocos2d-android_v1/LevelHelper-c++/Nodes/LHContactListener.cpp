
#include "LHContactListener.h"


LHContactListener::LHContactListener(){
}

LHContactListener::~LHContactListener() {
}

void LHContactListener::BeginContact(b2Contact* contact) {
    (*preSolveSelector)( nodeObject, contact, NULL);
}

void LHContactListener::EndContact(b2Contact* contact) {
    (*postSolveSelector)(nodeObject, contact, NULL);
}

void LHContactListener::PreSolve(b2Contact* contact, 
								 const b2Manifold* oldManifold) {
    (*preSolveSelector)( nodeObject, contact, oldManifold);
}

void LHContactListener::PostSolve(b2Contact* contact, 
								  const b2ContactImpulse* impulse) {
    (*postSolveSelector)(nodeObject, contact, impulse);
}