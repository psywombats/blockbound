/**
 *  MonsterMDO.java
 *  Created on Aug 7, 2012 2:21:51 AM for project RainfallSchema
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfallschema;

import net.wombatrpgs.mgns.core.Schema;
import net.wombatrpgs.mgns.core.Annotations.*;

/**
 * Another test schema...
 */
@Path("junk/")
public class MonsterMDO extends Schema {
	
	@Desc("Visible name of the monster")
	public String monsterName;
	
	@Desc("Text displayed in bestiary, or empty if no entry")
	@EmptyAllowed
	public String bestiaryEntry;
	
	@Desc("Maximum health of monster")
	public Integer mhp;
	
	@Desc("Gold dropped when killed")
	public Integer gold;
	
	@Desc("Monster that this monster can summon via special attack, or none if no special")
	@Nullable
	public MonsterMDO summonedMonster;
	

}
