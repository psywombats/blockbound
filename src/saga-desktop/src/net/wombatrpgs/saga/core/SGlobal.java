/**
 *  SGlobal.java
 *  Created on Apr 4, 2014 6:35:33 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.core;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.core.interfaces.Queueable;
import net.wombatrpgs.saga.SagaSettings;
import net.wombatrpgs.saga.graphics.SagaGraphics;
import net.wombatrpgs.saga.rpg.chara.HeroParty;

/**
 * The counterpart to MGlobal. Holds Saga-related global information.
 */
public class SGlobal {
	
	/** RPG information */
	public static HeroParty heroes;
	
	/** Settings and default keys */
	public static SagaSettings settings;
	
	/** Saga-specific graphics */
	public static SagaGraphics graphics;
	
	/**
	 * Sets up all the global variables. Called once when game is created.
	 */
	public static void globalInit() {
		
		List<Queueable> toLoad;
		
		// settings first
		settings = new SagaSettings();
		toLoad = new ArrayList<Queueable>();
		
		// then everything else
		graphics = new SagaGraphics();
		heroes = new HeroParty();
		toLoad.add(heroes);
		MGlobal.assets.loadAssets(toLoad, "SGlobal");
		
		// debug save-y stuff
		if (MGlobal.args.length >= 1) {
			MGlobal.memory.load(MGlobal.args[0]);
		}
	}

}
