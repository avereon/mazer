package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.xenon.Program;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;
import com.avereon.xenon.node.NodeEvent;

public class MazeAssetType extends AssetType {

	public MazeAssetType( Product product ) {
		super( product, "mazer" );
		setDefaultCodec( new DefaultMazeCodec( product ) );
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) throws AssetException {
		Maze maze = new Maze();
		maze.addNodeListener( e -> {
			if( e.getType() == NodeEvent.Type.VALUE_CHANGED ) asset.refresh();
		} );
		asset.setModel( maze );
		return true;
	}

	@Override
	public boolean assetUser( Program program, Asset asset ) throws AssetException {
		return super.assetUser( program, asset );
	}

}
