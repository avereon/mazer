package com.avereon.mazer;

import com.avereon.rossa.icon.PauseIcon;
import com.avereon.rossa.icon.PlayIcon;
import com.avereon.util.Log;
import com.avereon.xenon.Mod;
import com.avereon.xenon.tool.ToolRegistration;

import java.lang.System.Logger;

/**
 * The Mazer mod. This mod is part of an example mod for
 * <a href="https://www.avereon.com/product/xenon">Xenon</a>. The mod
 * demonstrates various capabilities and practices common to Xenon mods.
 */
public class Mazer extends Mod {

	private static final Logger log = Log.log();

	private MazeAssetType mazeAssetType;

	public Mazer() {
		mazeAssetType = new MazeAssetType( this );
	}

	/**
	 * Called when the mod is registered with the program. It is common to
	 * register custom icons, actions, asset types and tools in this method.
	 */
	@Override
	public void register() {
		registerIcon( "mazer", MazerIcon.class );
		registerIcon( "play", PlayIcon.class );
		registerIcon( "pause", PauseIcon.class );

		registerAction( this.rb(), "reset" );
		registerAction( this.rb(), "runpause" );

		registerAssetType( mazeAssetType );
		registerTool( mazeAssetType, new ToolRegistration( this, MazeTool.class ) );
	}

	/**
	 * Called when the mod is allowed to start long running items like timers,
	 * tasks and other threads. Since this mod does not have a need for any long
	 * running items, it is empty.
	 */
	@Override
	public void startup() {}

	/**
	 * Called when the mod should shut down any long running items like timers,
	 * tasks and other threads. Since this mod does not have any long running
	 * items, it is empty.
	 */
	@Override
	public void shutdown() {}

	/**
	 * Called when the mod is unregistered from the program. It is common to
	 * unregister custom icons, actions, asset types and tools in this method.
	 */
	@Override
	public void unregister() {
		unregisterTool( mazeAssetType, MazeTool.class );
		unregisterAssetType( mazeAssetType );

		unregisterAction( "runpause" );
		unregisterAction( "reset" );

		unregisterIcon( "pause", PauseIcon.class );
		unregisterIcon( "play", PlayIcon.class );
		unregisterIcon( "mazer", MazerIcon.class );
	}

}
