package com.avereon.mazer;

import com.avereon.xenon.OpenToolRequestParameters;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.tool.ProgramTool;
import com.avereon.xenon.workpane.ToolException;

public class MazeTool extends ProgramTool {

	public MazeTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
	}

	@Override
	protected void assetReady( OpenToolRequestParameters parameters ) throws ToolException {
		super.assetReady( parameters );
		setTitle( getAsset().getName() );
	}

}
