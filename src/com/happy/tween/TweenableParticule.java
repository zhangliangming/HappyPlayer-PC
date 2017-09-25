package com.happy.tween;

import aurelienribon.tweenengine.Tweenable;

/**
 * 实现Tweenable接口，用于表明运动的返回值和变化值
 * 
 * @author zhangliangming
 * 
 */
public class TweenableParticule implements Tweenable {
	// The following lines define the different possible tween types.
	// It's up to you to define what you need :-)

	public static final int X = 1;
	public static final int Y = 2;
	public static final int XY = 3;

	// Composition pattern

	private Particule target;

	// Constructor

	public TweenableParticule(Particule particule) {
		this.target = particule;
	}

	// Tweenable implementation

	public int getTweenValues(int tweenType, float[] returnValues) {
		switch (tweenType) {
		case X:
			returnValues[0] = target.getX();
			return 1;
		case Y:
			returnValues[0] = target.getY();
			return 1;
		case XY:
			returnValues[0] = target.getX();
			returnValues[1] = target.getY();
			return 2;
		default:
			assert false;
			return 0;
		}
	}

	public void onTweenUpdated(int tweenType, float[] newValues) {
		switch (tweenType) {
		case X:
			target.setX(newValues[0]);
			break;
		case Y:
			target.setY(newValues[1]);
			break;
		case XY:
			target.setX(newValues[0]);
			target.setY(newValues[1]);
			break;
		default:
			assert false;
			break;
		}
	}
}
