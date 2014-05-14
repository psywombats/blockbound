/**
 *  Party.java
 *  Created on Apr 4, 2014 6:39:19 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.rpg.chara;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.mgne.core.AssetQueuer;
import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.graphics.interfaces.Disposable;
import net.wombatrpgs.sagaschema.rpg.chara.PartyMDO;
import net.wombatrpgs.sagaschema.rpg.chara.data.PartyEntryMDO;

/**
 * Just a collection of characters. The player party is in a different subclass.
 */
public class Party extends AssetQueuer implements Disposable {
	
	protected List<List<Chara>> groups;
	protected List<Chara> members;
	
	/**
	 * Creates a party from data.
	 * @param	mdo				The data to create party from
	 */
	public Party(PartyMDO mdo) {
		groups = new ArrayList<List<Chara>>();
		for (PartyEntryMDO entryMDO : mdo.members) {
			List<Chara> group = new ArrayList<Chara>();
			for (int i = 0; i < entryMDO.count; i += 1) {
				Chara chara = instantiateChara(entryMDO.monster);
				group.add(chara);
				assets.add(chara);
			}
			groups.add(group);
		}
		rebuildMembers();
	}
	
	/** @return The number of groups in this party */
	public int groupCount() { return groups.size(); }
	
	/** @return The total number of characters in the party */
	public int size() { return members.size(); }
	
	/** @return The nth monster group */
	public List<Chara> getGroup(int n) { return groups.get(n); }
	
	/** @return All members of this party */
	public List<Chara> getAll() { return members; }
	
	/** @return A representative of the nth group */
	public Chara getFront(int n) { return getGroup(n).get(0); }

	/**
	 * @see net.wombatrpgs.mgne.graphics.interfaces.Disposable#dispose()
	 */
	@Override
	public void dispose() {
		for (Chara chara : getAll()) {
			chara.dispose();
		}
	}
	
	/**
	 * Checks if a character is in this party.
	 * @param	chara			The character to check
	 * @return					True if that character is in this party
	 */
	public boolean contains(Chara chara) {
		return members.contains(chara);
	}
	
	/**
	 * Gets the index of the group the chara is in. Returns -1 if no group.
	 * @param	chara			The chara to to check
	 * @return					The index of that enemy's group
	 */
	public int index(Chara chara) {
		for (int i = 0; i < groupCount(); i += 1) {
			if (getGroup(i).contains(chara)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Swaps the order of the groups at the given indices.
	 * @param	index1			One of the indexes to swap
	 * @param	index2			The other index to swap
	 */
	public void swap(int index1, int index2) {
		int firstIndex, secondIndex;
		if (index1 < index2) {
			firstIndex = index1;
			secondIndex = index2;
		} else {
			firstIndex = index2;
			secondIndex = index1;
		}
		List<Chara> group1 = groups.get(firstIndex);
		List<Chara> group2 = groups.get(secondIndex);
		groups.remove(secondIndex);
		groups.remove(firstIndex);
		groups.add(firstIndex, group2);
		groups.add(secondIndex, group1);
		rebuildMembers();
	}
	
	/**
	 * Swaps the order of the groups containing the given characters.
	 * @param	chara1			One of the characters to group swap
	 * @param	chara2			The other character to group swap
	 */
	public void swap(Chara chara1, Chara chara2) {
		swap(charaToIndex(chara1), charaToIndex(chara2));
	}
	
	/**
	 * Create the appropriate subclass of chara for this party. Override if the
	 * party is made of players or enemies or something that requires subclass.
	 * @param	mdoKey			The key of the mdo being instantiated
	 * @return					The instantiated chara
	 */
	protected Chara instantiateChara(String mdoKey) {
		return new Chara(mdoKey);
	}
	
	/**
	 * Looks up the index of the group containing the selected character.
	 * @param	chara			The character to look up the index of
	 * @return					The index of the group of that character
	 */
	protected int charaToIndex(Chara chara) {
		for (int i = 0; i < groups.size(); i += 1) {
			if (groups.get(i).contains(chara)) {
				return i;
			}
		}
		MGlobal.reporter.err("No group contains: " + chara);
		return -1;
	}
	
	/**
	 * Rebuilds the all member list, preserving ordering.
	 */
	protected void rebuildMembers() {
		members = new ArrayList<Chara>();
		for (List<Chara> group : groups) {
			members.addAll(group);
		}
	}

}
