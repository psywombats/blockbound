/**
 *  Ability.java
 *  Created on Oct 18, 2013 4:16:28 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mrogue.characters.abilities;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.mrogue.characters.Action;
import net.wombatrpgs.mrogue.characters.CharacterEvent;
import net.wombatrpgs.mrogue.characters.GameUnit;
import net.wombatrpgs.mrogue.characters.travel.Step;
import net.wombatrpgs.mrogue.core.MGlobal;
import net.wombatrpgs.mrogueschema.characters.AbilityMDO;

/**
 * An ability is a special sort of action. It can be used by a character or a
 * hero, and it's not necessarily part of an AI routine. Actually it's kind of
 * a typical thing then... it's just constructed from a special ability MDO.
 */
public class Ability extends Action {
	
	protected AbilityMDO mdo;
	protected Effect effect;
	
	/**
	 * Creates a new ability for a particular actor from data.
	 * @param actor
	 */
	public Ability(CharacterEvent actor, AbilityMDO mdo) {
		super(actor);
		this.mdo = mdo;
		this.effect = EffectFactory.createEffect(mdo.effect, this);
	}
	
	/** @return The MP cost of this ability */
	public int getMP() { return mdo.mpCost; }
	
	/** @return The in-game name of this ability */
	public String getName() { return mdo.name; }
	
	/** @return The step animation of this ability */
	public Step getStep() { return effect.getStep(); }

	/**
	 * @see net.wombatrpgs.mrogue.characters.Action#act()
	 */
	@Override
	final public void act() {
		actor.getUnit().onAbilityUsed(this);
		effect.act(acquireTargets());
	}
	
	/**
	 * Switches on the ability targeting type and gets all victims affected by
	 * this attack.
	 * @return					The characters affected by this ability
	 */
	protected List<GameUnit> acquireTargets() {
		List<GameUnit> targets = new ArrayList<GameUnit>();
		switch (mdo.target) {
		case USER:
			targets.add(actor.getUnit());
			break;
		default:
			MGlobal.reporter.warn("Unknown ability target type " + mdo.target +
					" for ability + " + mdo);
		}
		return targets;
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.characters.Action#baseCost()
	 */
	@Override
	protected int baseCost() {
		return mdo.energyCost;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + "(" + mdo.key + ")";
	}

}
