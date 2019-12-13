package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.xenon.Program;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetException;
import com.avereon.xenon.asset.AssetType;

public class MazeAssetType extends AssetType {

	public MazeAssetType( Product product ) {
		super( product, "mazer" );
		setDefaultCodec( new DefaultMazeCodec( product ) );
	}

	@Override
	public boolean assetDefault( Program program, Asset asset ) throws AssetException {
		return super.assetDefault( program, asset );
	}

	@Override
	public boolean assetDialog( Program program, Asset asset ) throws AssetException {
		return super.assetDialog( program, asset );
	}

}
