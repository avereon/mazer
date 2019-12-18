package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.Mod;
import com.avereon.xenon.tool.ToolMetadata;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Mazer extends Mod {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private MazeAssetType mazeAssetType;

	@Override
	public void register() {
		getProgram().getIconLibrary().register( "mazer", MazerIcon.class );
		getProgram().getAssetManager().addAssetType( mazeAssetType = new MazeAssetType( this ) );
		getProgram().getToolManager().registerTool( mazeAssetType, new ToolMetadata( this, MazeTool.class ) );
	}

	@Override
	public void startup() {}

	@Override
	public void shutdown() {}

	@Override
	public void unregister() {
		getProgram().getToolManager().unregisterTool( mazeAssetType, MazeTool.class );
		getProgram().getAssetManager().removeAssetType( mazeAssetType );
		getProgram().getIconLibrary().unregister( "mazer", MazerIcon.class );
	}

}
