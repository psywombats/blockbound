/**
 *  GibParticleSet.java
 *  Created on Jan 31, 2013 12:57:44 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.saga.graphics.particles;

import net.wombatrpgs.saga.core.Constants;
import net.wombatrpgs.saga.core.SGlobal;
import net.wombatrpgs.sagaschema.graphics.GibsetMDO;

/**
 * Generates particles from gibs.
 */
public class GibParticleSet extends ParticleSet {
	
	protected GibsetMDO mdo;
	
	/**
	 * Creates a gibset from data. Remember to tell this thing to queue!
	 * @param 	mdo				The MDO to get data from
	 */
	public GibParticleSet(GibsetMDO mdo) {
		super(Constants.GIBS_DIR+mdo.file, mdo.count, mdo.frameWidth, mdo.frameHeight);
		this.mdo = mdo;
	}

	/**
	 * @see net.wombatrpgs.saga.graphics.particles.ParticleSet#generateParticle
	 * (net.wombatrpgs.saga.graphics.particles.Emitter)
	 */
	@Override
	public Particle generateParticle(Emitter source) {
		Particle part = new Particle(source.mdo, source,
				particleSources[SGlobal.rand.nextInt(mdo.count)]);
		return part;
	}

}
