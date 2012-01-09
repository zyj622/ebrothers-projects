package org.cocos2d.layers;

import java.util.Iterator;

import javax.microedition.khronos.opengles.GL10;

import org.cocos2d.config.ccMacros;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGSize;
import org.cocos2d.utils.GLESDebugDraw;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class CCPhysicsLayer extends CCLayer {
	// Simulation space should be larger than window per Box2D
	// recommendation.
	static {
		System.loadLibrary("gdx");
	}
	
	protected static final float BUFFER = 1.0f;

	// FPS for the PhysicsWorld to sync to
	protected static final float FPS = (float) CCDirector.sharedDirector()
			.getAnimationInterval();
	private static float rdelta = 0;

	private float mRatio = 1f;
	protected final World world;
	private GLESDebugDraw debugDraw;
	protected final float scaledWidth;
	protected final float scaledHeight;

	public CCPhysicsLayer(float ratio) {
		super();
		mRatio = ratio;
		CGSize s = CCDirector.sharedDirector().winSize();
		scaledWidth = s.width / mRatio;
		scaledHeight = s.height / mRatio;

		world = new World(new Vector2(0, -9.8f), true);
		world.setContinuousPhysics(true);
	}

	public void useDebugDraw() {
		debugDraw = new GLESDebugDraw(world, mRatio);
		debugDraw.setFlag(GLESDebugDraw.DRAW_SHAPE | GLESDebugDraw.DRAW_JOINT);
	}

	@Override
	public void draw(GL10 gl) {
		if (debugDraw != null) {
			debugDraw.drawDebugData(gl);
		}
	}

	@Override
	public void onEnter() {
		super.onEnter();

		// start ticking (for physics simulation)
		schedule("tick");
	}

	@Override
	public void onExit() {
		super.onExit();

		// stop ticking (for physics simulation)
		unschedule("tick");
	}

	public synchronized void tick(float delta) {
		if ((rdelta += delta) < FPS)
			return;

		// It is recommended that a fixed time step is used with Box2D for
		// stability
		// of the simulation, however, we are using a variable time step
		// here.
		// You need to make an informed choice, the following URL is useful
		// http://gafferongames.com/game-physics/fix-your-timestep/

		// Instruct the world to perform a simulation step. It is
		// generally best to keep the time step and iterations fixed.
		synchronized (world) {
			world.step(FPS, 8, 1);
		}

		rdelta = 0;

		// Iterate over the bodies in the physics world
		Iterator<Body> it = world.getBodies();
		while (it.hasNext()) {
			Body b = it.next();
			Object userData = b.getUserData();

			if (userData != null && userData instanceof CCSprite) {
				// Synchronize the Sprites position and rotation with the
				// corresponding body
				final CCSprite sprite = (CCSprite) userData;
				final Vector2 pos = b.getPosition();
				sprite.setPosition(pos.x * mRatio, pos.y * mRatio);
				sprite.setRotation(-1.0f
						* ccMacros.CC_RADIANS_TO_DEGREES(b.getAngle()));
			}
		}
	}
}
