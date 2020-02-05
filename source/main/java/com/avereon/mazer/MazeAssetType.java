package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.xenon.Program;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class MazeAssetType extends AssetType {

	private static final String MEDIA_TYPE = "application/vnd.avereon.mazer.maze";

	public MazeAssetType( Product product ) {
		super( product, "mazer" );
		setDefaultCodec( new MazeCodec( product ) );
	}

	@Override
	public String getKey() {
		return MEDIA_TYPE;
	}

	@Override
	public boolean assetInit( Program program, Asset asset ) throws AssetException {
		asset.setModel( new Maze() );
		return true;
	}

	@Override
	public boolean assetUser( Program program, Asset asset ) throws AssetException {
		return super.assetUser( program, asset );
	}

}
