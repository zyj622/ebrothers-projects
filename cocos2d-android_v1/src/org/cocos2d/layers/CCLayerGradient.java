package org.cocos2d.layers;

import org.cocos2d.types.CGPoint;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.util.FloatMath;

public class CCLayerGradient extends CCColorLayer {

	protected ccColor3B endColor_;
	protected int startOpacity_;
	protected int endOpacity_;
	protected CGPoint vector_;
	protected boolean compressedInterpolation_;

	protected CCLayerGradient(ccColor4B color) {
		super(color);

	}

	public static CCLayerGradient node(ccColor4B color) {
		return new CCLayerGradient(color);
	}

	public ccColor3B startColor() {
		return color_;
	}

	public void setStartColor(ccColor3B colors) {
		setColor(colors);
	}

	public void setEndColor(ccColor3B colors) {
		endColor_ = colors;

	}

	public void setStartOpacity(int o) {
		startOpacity_ = o;

	}

	public void setEndOpacity(int o) {
		endOpacity_ = o;

	}

	public void setVector(CGPoint point) {
		vector_ = point;

	}

	public boolean compressedInterpolation() {
		return compressedInterpolation_;
	}

	public void setCompressedInterpolation(boolean compress) {
		compressedInterpolation_ = compress;
	}

	@Override
	protected void init(ccColor4B color, float w, float h) {
		endColor_ = ccColor3B.ccc3(color.a, color.g, color.b);
		endOpacity_ = color.a;
		startOpacity_ = color.a;
		vector_ = CGPoint.ccp(0, -1);
		super.init(color, w, h);
	}

	@Override
	protected void updateColor() {
		super.updateColor();

		float h = CGPoint.ccpLength(vector_);
		if (h == 0) {
			return;
		}

		float c = FloatMath.sqrt(2);
		CGPoint u = CGPoint.ccp(vector_.x / h, vector_.y / h);
		if (compressedInterpolation_) {
			float h2 = 1 / (Math.abs(u.x) + Math.abs(u.y));
			u = CGPoint.ccpMult(u, h2 * c);
		}

		float opacityf = (float) opacity_ / 255.0f;

		ccColor4B S = new ccColor4B(color_.r, color_.g, color_.b,
				(int) (startOpacity_ * opacityf));
		ccColor4B E = new ccColor4B(endColor_.r, endColor_.g, endColor_.b,
				(int) (endOpacity_ * opacityf));

		for (int i = 0; i < squareColors_.limit(); i++) {
			switch (i % 4) {
			case 0:

				squareColors_.put(i, (float) (E.r + (S.r - E.r)
						* ((c - u.x + u.y) / (2.0f * c))));
				break;
			case 1:
				squareColors_.put(i, (float) (E.g + (S.g - E.g)
						* ((c - u.x + u.y) / (2.0f * c))));
				break;
			case 2:
				squareColors_.put(i, (float) (E.b + (S.b - E.b)
						* ((c - u.x + u.y) / (2.0f * c))));
				break;
			default:
				squareColors_.put(i, (float) (E.a + (S.a - E.a)
						* ((c - u.x - u.y) / (2.0f * c))));
			}
			squareColors_.position(0);
		}

	}

}
