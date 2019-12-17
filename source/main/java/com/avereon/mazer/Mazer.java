package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.Mod;
import com.avereon.xenon.tool.ToolMetadata;
import org.slf4j.Logger;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;

public class Mazer extends Mod {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private MazeAssetType mazeAssetType;

	@Override
	public void register() {
		getProgram().getIconLibrary().register( "mazer", MazerIcon.class );
		getProgram().getAssetManager().addAssetType( mazeAssetType = new MazeAssetType( this ) );
		getProgram().getToolManager().registerTool( mazeAssetType, new ToolMetadata( this, MazeTool.class ) );

		//rb().text( "asset", "mazer-name" );

		String resource = "/com/avereon/mazer/bundles/asset.properties";
		log.warn( "Looking for resource: " + resource );
		InputStream input = getClass().getResourceAsStream( resource );
		if( input != null ) log.error( "FOUND THE RESOURCE by class(absolute)" );

		resource = "bundles/asset.properties";
		log.warn( "Looking for resource: " + resource );
		input = getClass().getResourceAsStream( resource );
		if( input != null ) log.error( "FOUND THE RESOURCE by class(relative)" );

		resource = "com/avereon/mazer/asset.properties";
		input = getClassLoader().getResourceAsStream( resource );
		log.warn( "Looking for resource: " + resource );
		if( input != null ) log.error( "FOUND THE RESOURCE by loader" );

		resource = "com/avereon/mazer/asset.properties";
		input = getClass().getClassLoader().getResourceAsStream( resource );
		log.warn( "Looking for resource: " + resource );
		if( input != null ) log.error( "FOUND THE RESOURCE by class.loader" );
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
