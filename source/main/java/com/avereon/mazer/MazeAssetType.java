package com.avereon.mazer;

import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class MazeAssetType extends AssetType {

	public MazeAssetType( ProgramProduct product ) {
		super( product, "mazer" );
		setDefaultCodec( new MazeCodec( product ) );
	}

	@Override
	public String getKey() {
		return getDefaultCodec().getKey();
	}

	@Override
	public boolean assetOpen( Program program, Asset asset ) throws AssetException {
		asset.setModel( new Maze() );
		return true;
	}

	@Override
	public boolean assetNew( Program program, Asset asset ) throws AssetException {
		return super.assetNew( program, asset );
	}

}
