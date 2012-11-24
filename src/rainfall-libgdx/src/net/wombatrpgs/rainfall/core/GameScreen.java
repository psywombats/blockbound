/**
 *  Context.java
 *  Created on Nov 23, 2012 4:33:37 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.wombatrpgs.mgne.global.Global;
import net.wombatrpgs.rainfall.graphics.Renderable;
import net.wombatrpgs.rainfall.io.CommandListener;
import net.wombatrpgs.rainfall.io.CommandMap;

/**
 * A screen is the environment in which the game is now running. It's
 * essentially part of a massive state machine that defines the game, and saves
 * some notes about its parameters, such as the screen to display and how to
 * handle keys. Screens can be layered one on top of the other. In this case,
 * the screens with lower z values are rendered first, and the screens behind
 * are only rendered if the screen on top of them is counted as transparent.
 * More info in the ScreenStack class.
 */
public abstract class GameScreen implements CommandListener, 
											Comparable<GameScreen>,
											Renderable {
	
	/** Command map to use while this screen is active */
	protected CommandMap commandContext;
	/** The thing to draw if this canvas is visible */
	protected Renderable canvas;
	/** Depth, lower values are rendered last */
	protected float z = 0;
	/** If true, layers with higher z won't be rendered */
	protected boolean transparent;
	
	/**
	 * Creates a new game screen. Remember to call intialize when done setting
	 * things up yourself.
	 */
	public GameScreen() {
		
	}
	
	/**
	 * Called whenever this screen stops being the top screen on the stack. The
	 * screen will stop receiving player commands.
	 */
	public abstract void onFocusLost();
	
	/**
	 * Called whenever this screen starts being the top screen on the stack. The
	 * screen will start receiving player commands.
	 */
	public abstract void onFocusGained();
	
	/**
	 * Sets the z value (depth) of the screen. Higher z values are rendered
	 * later.
	 * @param 	z			The new z-value
	 */
	public void setZ(float z) {
		this.z = z;
	}
	
	/**
	 * Returns the z value (depth) of the screen. Higher z values are rendered
	 * later.
	 * @return				The current z-value
	 */
	public float getZ() {
		return z;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(GameScreen other) {
		if (z < other.z) {
			return (int) Math.floor(z - other.z) - 1;
		} else if (z > other.z) {
			return 0;
		} else {
			return (int) Math.floor(z - other.z) + 1;
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.graphics.Renderable#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		canvas.render(camera);
	}

	/**
	 * @see net.wombatrpgs.rainfall.graphics.Renderable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		canvas.queueRequiredAssets(manager);
	}

	/**
	 * @see net.wombatrpgs.rainfall.graphics.Renderable#postProcessing()
	 */
	@Override
	public void postProcessing() {
		canvas.postProcessing();
	}

	/**
	 * Run some final safety checks and finish initialization. Call once during
	 * the constructor.
	 */
	protected final void init() {
		if (canvas == null) {
			Global.reporter.warn("No canvas for screen " + this);
		}
		if (commandContext == null) {
			Global.reporter.warn("No command context for screen " + this);
		}
	}

}