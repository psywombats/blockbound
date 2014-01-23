/**
 *  CommandGoToMenu.java
 *  Created on Mar 28, 2013 11:55:16 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.scenes.commands;

import com.badlogic.gdx.Gdx;

import net.wombatrpgs.saga.scenes.SceneCommand;
import net.wombatrpgs.saga.scenes.SceneParser;

/**
 * Go to title screen thing?
 */
public class CommandGoToTitle extends SceneCommand {

	/**
	 * Inherited constructor.
	 * @param 	parent			The parent parser to run for
	 * @param 	line			The line of code generated from
	 */
	public CommandGoToTitle(SceneParser parent, String line) {
		super(parent, line);
	}

	/**
	 * @see net.wombatrpgs.saga.scenes.SceneCommand#run()
	 */
	@Override
	public boolean run() {
		// TODO: scenes: go to title screen, not quit
		Gdx.app.exit();
		// unreachable code
		return false;
	}

}
