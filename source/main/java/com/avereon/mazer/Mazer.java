package com.avereon.mazer;

import com.avereon.rossa.icon.PauseIcon;
import com.avereon.rossa.icon.PlayIcon;
import com.avereon.util.LogUtil;
import com.avereon.xenon.Mod;
import com.avereon.xenon.tool.ToolRegistration;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Mazer extends Mod {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private MazeAssetType mazeAssetType;

	@Override
	public void register() {
		registerIcon( "mazer", MazerIcon.class );
		registerIcon( "play", PlayIcon.class );
		registerIcon( "pause", PauseIcon.class );

		registerAssetType( mazeAssetType = new MazeAssetType( this ) );
		registerTool( mazeAssetType, new ToolRegistration( this, MazeTool.class ) );

		registerAction( this.rb(), "reset" );
		registerAction( this.rb(), "runpause" );
	}

	@Override
	public void startup() {}

	@Override
	public void shutdown() {}

	@Override
	public void unregister() {
		unregisterAction( "runpause" );
		unregisterAction( "reset" );

		unregisterTool( mazeAssetType, MazeTool.class );
		unregisterAssetType( mazeAssetType );

		unregisterIcon( "pause", PauseIcon.class );
		unregisterIcon( "play", PlayIcon.class );
		unregisterIcon( "mazer", MazerIcon.class );
	}

}
