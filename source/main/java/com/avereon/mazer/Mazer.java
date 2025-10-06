package com.avereon.mazer;

import com.avereon.product.Rb;
import com.avereon.xenon.Module;
import com.avereon.xenon.ToolRegistration;
import lombok.CustomLog;

/**
 * The Mazer mod. This mod is part of an example mod for
 * <a href="https://www.avereon.com/product/xenon">Xenon</a>. The mod
 * demonstrates various capabilities and practices common to Xenon mods.
 */
@CustomLog
public class Mazer extends Module {

	private final MazeResourceType mazeAssetType;

	public Mazer() {
		Rb.init( this );
		mazeAssetType = new MazeResourceType( this );
	}

	/**
	 * Called when the mod is registered with the program.
	 */
	@Override
	public void register() {}

	/**
	 * Called when the mod is started. It is common to register custom icons, actions, asset types, tools and long running items like timers, tasks and other threads in this method.
	 */
	@Override
	public void startup() {
		registerIcon( "mazer", new MazerIcon() );

		registerAction( this, "reset" );
		registerAction( this, "runpause" );

		registerAssetType( mazeAssetType );
		registerTool( mazeAssetType, new ToolRegistration( this, MazeTool.class ) );
	}

	/**
	 * Called when the mod should shut down. It is common to unregister custom icons, actions, asset types, tools and long running items like timers, tasks and other threads in this method.
	 */
	@Override
	public void shutdown() {
		unregisterTool( mazeAssetType, MazeTool.class );
		unregisterAssetType( mazeAssetType );

		unregisterAction( "runpause" );
		unregisterAction( "reset" );

		unregisterIcon( "mazer", new MazerIcon() );
	}

	/**
	 * Called when the mod is unregistered from the program.
	 */
	@Override
	public void unregister() {}

}
