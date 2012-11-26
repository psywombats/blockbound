/**
 *  TestScreen.java
 *  Created on Nov 24, 2012 4:14:43 AM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.rainfall.test;

import net.wombatrpgs.mgne.global.Global;
import net.wombatrpgs.rainfall.core.GameScreen;
import net.wombatrpgs.rainfall.core.RGlobal;
import net.wombatrpgs.rainfall.io.InputCommand;
import net.wombatrpgs.rainfall.io.TestCommandMap;
import net.wombatrpgs.rainfall.maps.Direction;
import net.wombatrpgs.rainfall.maps.Level;
import net.wombatrpgs.rainfallschema.maps.MapMDO;
import net.wombatrpgs.rainfallschema.test.MapLoadTestMDO;

/**
 * TESTING 1 2 3 TESTING DO YOU HEAR ME TESTINGGGGGGGG
 */
public class TestScreen extends GameScreen {
	
	private Level map;
	
	public TestScreen() {
		MapLoadTestMDO mapTestMDO = (MapLoadTestMDO) RGlobal.data.getEntryByKey("map_test");
		MapMDO mapMDO = (MapMDO) RGlobal.data.getEntryByKey(mapTestMDO.map);
		map = new Level(mapMDO);
		map.queueRequiredAssets(RGlobal.assetManager);
		while (!RGlobal.assetManager.update());
		map.postProcessing(RGlobal.assetManager);
		map.queueMapObjectAssets(RGlobal.assetManager);
		while (!RGlobal.assetManager.update());
		map.postProcessingMapObjects(RGlobal.assetManager);
		
		commandContext = new TestCommandMap();
		z = 0;
		canvas = map;
		
		init();
	}

	/**
	 * @see net.wombatrpgs.rainfall.io.CommandListener#onCommand
	 * (net.wombatrpgs.rainfall.io.InputCommand)
	 */
	@Override
	public void onCommand(InputCommand command) {
		Global.reporter.inform("Command received: " + command);
		
		// start move
		if (command.equals(InputCommand.MOVE_START_DOWN)) {
			RGlobal.hero.startMove(Direction.DOWN);
		} else if (command.equals(InputCommand.MOVE_START_LEFT)) {
			RGlobal.hero.startMove(Direction.LEFT);
		} else if (command.equals(InputCommand.MOVE_START_RIGHT)) {
			RGlobal.hero.startMove(Direction.RIGHT);
		} else if (command.equals(InputCommand.MOVE_START_UP)) {
			RGlobal.hero.startMove(Direction.UP);
		}
		
		// end move
		if (command.equals(InputCommand.MOVE_STOP_DOWN)) {
			RGlobal.hero.stopMove(Direction.DOWN);
		} else if (command.equals(InputCommand.MOVE_STOP_LEFT)) {
			RGlobal.hero.stopMove(Direction.LEFT);
		} else if (command.equals(InputCommand.MOVE_STOP_RIGHT)) {
			RGlobal.hero.stopMove(Direction.RIGHT);
		} else if (command.equals(InputCommand.MOVE_STOP_UP)) {
			RGlobal.hero.stopMove(Direction.UP);
		}
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.GameScreen#onFocusLost()
	 */
	@Override
	public void onFocusLost() {
		// tsilb
	}

	/**
	 * @see net.wombatrpgs.rainfall.core.GameScreen#onFocusGained()
	 */
	@Override
	public void onFocusGained() {
		// tslib
	}

}
