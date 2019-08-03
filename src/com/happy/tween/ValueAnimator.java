package com.happy.tween;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.Tweenable;
import aurelienribon.tweenengine.equations.Linear;

/**
 * 动画
 * 
 * @author zhangliangming
 * 
 */
public class ValueAnimator {
	/**
	 * 动画时间
	 */
	private int duration = 250;
	private TweenManager manager;
	private Tween tween;
	private boolean isFinish = false;
	private AnimatorUpdateListener animatorUpdateListener;
	private AnimationListener animationListener;
	private Particule particule;

	public ValueAnimator() {
		manager = new TweenManager();
		Tween.setPoolEnabled(true);
	}

	/**
	 * 
	 * @param start
	 * @param end
	 */
	public synchronized void ofFloat(float start, float end) {
		if (particule == null)
			particule = new Particule();
		particule.setX(0);
		particule.setY(start);

		Tweenable tweenParticle = new TweenableParticule(particule);

		// target(x,y);
		tween = Tween.to(tweenParticle, TweenableParticule.XY, duration,
				Linear.INOUT).target(start, end);

		tween.addCompleteCallback(new TweenCallback() {

			public void tweenEventOccured(Types arg0, Tween arg1) {
				isFinish = true;
				if (animationListener != null) {
					animationListener.onAnimationEnd(particule.getY());
				}
			}
		});

		tween.addStartCallback(new TweenCallback() {

			public void tweenEventOccured(Types arg0, Tween arg1) {
				isFinish = false;
				if (animationListener != null) {
					animationListener.onAnimationStart(particule.getY());
				}
				new Thread() {

					public void run() {

						while (true && !isFinish) {
							try {
								Thread.sleep(50);
								if (animatorUpdateListener != null) {
									animatorUpdateListener
											.onAnimationUpdate(particule.getY());
								}
								if (manager != null
										&& manager.getTweenCount() > 0)
									manager.update();

							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					};

				}.start();
			}
		});

	}

	/**
	 * 开始
	 */
	public void start() {
		if (manager != null && tween != null)
			manager.add(tween.start());
	}

	public void cancel() {
		isFinish = true;
		if (manager != null)
			manager.clear();
		if (animationListener != null) {
			animationListener.onAnimationEnd(particule.getY());
		}
	}

	public boolean isRunning() {
		return !isFinish;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setAnimatorUpdateListener(
			AnimatorUpdateListener animatorUpdateListener) {
		this.animatorUpdateListener = animatorUpdateListener;
	}

	/**
	 * 更新进度
	 * 
	 * @author zhangliangming
	 * 
	 */
	public interface AnimatorUpdateListener {
		public void onAnimationUpdate(float curValue);
	}

	public void setAnimationListener(AnimationListener animationListener) {
		this.animationListener = animationListener;
	}

	/**
	 * 动画监听
	 * 
	 * @author zhangliangming
	 * 
	 */
	public interface AnimationListener {
		public void onAnimationStart(float curValue);

		public void onAnimationEnd(float curValue);
	}

}
