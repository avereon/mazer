package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DefaultMazeCodec extends Codec {

	private Product product;

	public DefaultMazeCodec( Product product ) {
		this.product = product;
	}

	@Override
	public String getKey() {
		return "com.avereon.mazer.codec.default";
	}

	@Override
	public String getName() {
		return product.rb().text( "mazer", "codec.default.name" );
	}

	@Override
	public boolean canLoad() {
		return true;
	}

	@Override
	public boolean canSave() {
		return true;
	}

	@Override
	public void load( Asset asset, InputStream input ) throws IOException {
		// TODO Convert the input stream to the asset model
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		// TODO Convert the asset model to the output stream
	}

}
