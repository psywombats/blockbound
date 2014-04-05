/**
 *  FacesAnimation.java
 *  Created on Jan 24, 2013 7:45:30 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.graphics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.wombatrpgs.mgneschema.maps.data.OrthoDir;

/**
 * Any animation used to represent a moving entity on the map. Generally it's
 * a congolomeration of faces that switch out depending on how the entity's
 * moving.
 */
public abstract class FacesAnimation extends ScreenGraphic {
	
	protected static final float FLICKER_DURATION = .3f; // in s
	
	protected OrthoDir currentDir;
	protected AnimationStrip[] animations;
	protected int rangeX, rangeY;
	protected int facings;
	protected boolean flickering;
	protected float time;
	
	/**
	 * Sets up an animation from data for a specific map.
	 * @param	facings			How many facings make up this animation
	 */
	public FacesAnimation(int facings) {
		this.facings = facings;
		setFacing(OrthoDir.SOUTH);
		animations = new AnimationStrip[facings];
		flickering = false;
		time = 0;
		rangeX = 0;
		rangeY = 0;
	}
	
	/** Kryo constructor */
	protected FacesAnimation() { }
	
	/** @return The direction currently facing */
	public OrthoDir getFacing() { return currentDir; }
	
	/** @param dir The new current direction */
	public void setFacing(OrthoDir dir) { this.currentDir = dir; }
	
	/**
	 * The /real/ rendering method.
	 * @see net.wombatrpgs.mgne.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 * @param 	camera			The camera used to render
	 */
	public void coreRender(OrthographicCamera camera) {
		animations[currentDirOrdinal()].render(camera);
	}

	/** @see net.wombatrpgs.mgne.graphics.ScreenGraphic#getWidth() */
	@Override public int getWidth() { return animations[currentDirOrdinal()].getWidth(); }

	/** @see net.wombatrpgs.mgne.graphics.ScreenGraphic#getHeight() */
	@Override public int getHeight() { return animations[currentDirOrdinal()].getHeight(); }

	/**
	 * @see net.wombatrpgs.mgne.graphics.ScreenGraphic#setX(float)
	 */
	@Override
	public void setX(float x) {
		super.setX(x);
		for (int i = 0; i < facings; i++) {
			animations[i].setX(x);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.ScreenGraphic#setY(float)
	 */
	@Override
	public void setY(float y) {
		super.setY(y);
		for (int i = 0; i < facings; i++) {
			animations[i].setY(y);
		}
	}

	/**
	 * Gets the duration to play the whole animation.
	 * @return					The time it takes to play this, in s
	 */
	public float getDuration() {
		return animations[currentDirOrdinal()].getMaxTime();
	}
	
	/** @return The currently playing animation strip */
	public AnimationStrip getStrip() { return animations[currentDirOrdinal()]; }
	
	/** @return True if the anim is playing, false otherwise */
	public boolean isMoving() { return animations[currentDirOrdinal()].isMoving(); }

	/**
	 * @see net.wombatrpgs.mgne.graphics.Renderable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (int i = 0; i < facings; i++) {
			animations[i].queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.Renderable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager , int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (int i = 0; i < facings; i++) {
			animations[i].postProcessing(manager, pass);
			animations[i].update(0);
		}
	}
	
	/**
	 * There's a coreRender method to override instead
	 * @see net.wombatrpgs.mgne.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public final void render(OrthographicCamera camera) {
		if (shouldAppear()) {
			coreRender(camera);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.core.interfaces.Updateable#update(float)
	 */
	@Override
	public void update(float elapsed) {
		time += elapsed;
		for (int i = 0; i < facings; i++) {
			// we could just update the only one being displayed...
			animations[i].update(elapsed);
		}
	}
	
	/**
	 * Fetches a specific frame of the animation. Wonky because it takes an
	 * ordinal, not a direction.
	 * @param	dirOrdinal		The ordinal of the direction to fetch
	 * @param	frame			The number of the frame to fetch
	 * @return					The texture region for that frame
	 */
	public TextureRegion getFrame(int dirOrdinal, int frame) {
		return animations[currentDirOrdinal()].getFrame(frame);
	}
	
	/**
	 * Just starts moving.
	 */
	public void startMoving() {
		for (int i = 0; i < facings; i++) {
			animations[i].startMoving();
		}
	}

	/**
	 * Halts all animation movement.
	 */
	public void stopMoving() {
		for (int i = 0; i < facings; i++) {
			animations[i].stopMoving();
		}
	}
	
	/**
	 * Resets all the timings on this animation.
	 */
	public void reset() {
		for (int i = 0; i < facings; i++) {
			animations[i].reset();
		}
		time = 0;
	}
	
	/**
	 * Turns on/off this animation's flickering.
	 * @param 	flicker			True if we should flicker, false otherwise
	 */
	public void setFlicker(boolean flicker) {
		this.flickering = flicker;
	}
	
	/**
	 * Flashes a certain color for a certain time.
	 * @param	c				The color to flash
	 * @param	duration		How long the flash should take in total
	 */
	public void flash(Color c, float duration) {
		for (AnimationStrip strip : animations) {
			strip.flash(c, duration);
		}
	}
	
	/**
	 * This is used to index into the animations array. Returns the index.
	 * @return					The index of the current anim in the anim array
	 */
	protected abstract int currentDirOrdinal();
	
	/**
	 * Populates the array of animations.
	 */
	protected abstract void sliceAnimations();
	
	/**
	 * Determines if this animation is visible at the moment. This isn't in the
	 * culling sense but more of the "did I drink an invisibility potion"
	 * sense.
	 * @return					True if should be rendered, false otherwise
	 */
	protected boolean shouldAppear() {
		if (flickering) {
			int ms = (int) (time * 1000);
			int d = (int) (FLICKER_DURATION * 1000);
			if (ms % d < d/2) {
				return false;
			}
		}
		return true;
	}

}
