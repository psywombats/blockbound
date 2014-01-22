/**
 *  EasyCommandMap.java
 *  Created on Jan 22, 2014 1:36:15 AM for project saga
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.io.command;

import java.util.HashMap;
import java.util.Map;

import net.wombatrpgs.saga.io.CommandMap;
import net.wombatrpgs.saga.io.InputEvent;
import net.wombatrpgs.saga.io.InputEvent.EventType;
import net.wombatrpgs.sagaschema.io.data.InputButton;
import net.wombatrpgs.sagaschema.io.data.InputCommand;

/**
 * A command map with some lists and things filled out.
 */
public abstract class EasyCommandMap extends CommandMap {
	
	protected Map<InputEvent, InputCommand> bindings;
	
	/**
	 * Initializes a new easy map. Tells the children to fill in the bindings.
	 * @param	includeGlobals	True to include game-wide UI commands in the
	 * 							bindings, things like "fullscreen"
	 */
	public EasyCommandMap(boolean includeGlobals) {
		bindings = new HashMap<InputEvent, InputCommand>();
		initBindings();
		if (includeGlobals) {
			initGlobals();
		}
	}

	/**
	 * @see net.wombatrpgs.saga.io.CommandMap#parse
	 * (net.wombatrpgs.saga.io.InputEvent)
	 */
	@Override
	public InputCommand parse(InputEvent event) {
		return bindings.get(event);
	}

	/**
	 * The subclasses should add things to the bindings list here. Called from
	 * the constructor automatically.
	 */
	protected abstract void initBindings();
	
	/**
	 * The same thing, but this is for things that are shared across screens and
	 * contexts, such as fiddling with the window.
	 */
	protected final void initGlobals() {
		bindings.put(new InputEvent(InputButton.FULLSCREEN, EventType.PRESS),
				InputCommand.GLOBAL_FULLSCREEN);
	}
}
