package com.avereon.mazer;

import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.exception.ResourceException;
import com.avereon.xenon.asset.AssetType;

public class MazeAssetType extends AssetType {

	public MazeAssetType( XenonProgramProduct product ) {
		super( product, "mazer" );
		setDefaultCodec( new MazeCodec( product ) );
	}

	@Override
	public String getKey() {
		return getDefaultCodec().getKey();
	}

	@Override
	public boolean assetNew( Xenon program, Asset asset ) throws ResourceException {
		return super.assetNew( program, asset );
	}

	@Override
	public boolean assetOpen( Xenon program, Asset asset ) throws ResourceException {
		asset.setModel( new Maze() );
		return true;
	}

}
