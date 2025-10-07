package com.avereon.mazer;

import com.avereon.xenon.Xenon;
import com.avereon.xenon.XenonProgramProduct;
import com.avereon.xenon.asset.Resource;
import com.avereon.xenon.asset.exception.ResourceException;
import com.avereon.xenon.asset.ResourceType;

public class MazeResourceType extends ResourceType {

	public MazeResourceType( XenonProgramProduct product ) {
		super( product, "mazer" );
		setDefaultCodec( new MazeCodec( product ) );
	}

	@Override
	public String getKey() {
		return getDefaultCodec().getKey();
	}

	@Override
	public boolean assetNew( Xenon program, Resource resource ) throws ResourceException {
		return super.assetNew( program, resource );
	}

	@Override
	public boolean assetOpen( Xenon program, Resource resource ) throws ResourceException {
		resource.setModel( new Maze() );
		return true;
	}

}
