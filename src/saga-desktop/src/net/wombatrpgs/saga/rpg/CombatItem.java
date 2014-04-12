/**
 *  CombatItem.java
 *  Created on Apr 12, 2014 2:49:18 AM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.rpg;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.sagaschema.rpg.abil.CombatItemMDO;

/**
 * Represents the combat item MDO. This could be an item or ability, but either
 * way, it can be equipped to a character and potentially have a use in combat.
 */
// TODO: combat: add the combat aspects of this class
public class CombatItem {
	
	protected CombatItemMDO mdo;
	
	protected String name;
	protected int uses;
	
	/**
	 * Creates a new combat item from data.
	 * @param	mdo				The data to create from
	 */
	public CombatItem(CombatItemMDO mdo) {
		this.mdo = mdo;
		uses = mdo.uses;
		name = MGlobal.charConverter.convert(mdo.abilityName);
	}
	
	/**
	 * Creates a new combat item from a key to data.
	 * @param	key				The key to the data to create from
	 */
	public CombatItem(String key) {
		this(MGlobal.data.getEntryFor(key, CombatItemMDO.class));
	}
	
	/** @return The ability name of this item */
	public String getName() { return name; }
	
	/** @return True if this item has unlimited uses */
	public boolean isUnlimited() { return mdo.uses == 0; }
	
	/** @return The number of uses remaining on this item */
	public int getUses() { return uses; }

}
