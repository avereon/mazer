package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;

import java.io.*;
import java.lang.System.Logger;
import java.nio.charset.StandardCharsets;

public class MazeCodec extends Codec {

	private static final Logger log = Log.log();

	private Product product;

	public MazeCodec( Product product ) {
		this.product = product;
		setDefaultExtension( "maze" );
	}

	@Override
	public String getKey() {
		return "com.avereon.mazer.codec.maze";
	}

	@Override
	public String getName() {
		return product.rb().text( "asset", "codec-maze-name" );
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
		log.log( Log.DEBUG, "Loading maze: " + asset );

		BufferedReader reader = new BufferedReader( new InputStreamReader( input, StandardCharsets.UTF_8 ) );

		try {
			Maze maze = asset.getModel();

			String line;
			while( (line = reader.readLine()) != null ) {
				char type = line.charAt( 0 );
				String[] data = line.substring( 2 ).split( "," );
				for( int index = 0; index < data.length; index++ ) {
					data[ index ] = data[ index ].trim();
				}
				switch( type ) {
					case 'S': {
						maze.setSize( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ) );
						break;
					}
					case 'H': {
						maze.setCellConfig( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ), MazeConfig.HOLE );
						break;
					}
					case 'C': {
						maze.setCellConfig( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ), MazeConfig.COOKIE );
						break;
					}
					case 'M': {
						maze.setCellConfig( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ), MazeConfig.MONSTER );
						break;
					}
					case 'D': {
						maze.setStartDirection( Direction.valueOf( data[ 0 ] ) );
						break;
					}
				}
			}

			maze.reset();
			asset.setModel( maze );
		} catch( Exception exception ) {
			throw new IOException( exception );
		}
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		log.log( Log.DEBUG, "Saving maze: " + asset );
		try {
			Maze maze = asset.getModel();
			int width = maze.getWidth();
			int height = maze.getHeight();

			PrintStream printer = new PrintStream( output, true, StandardCharsets.UTF_8 );

			printer.println( "S" + " " + width + "," + height );
			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					int state = maze.getCellConfig( x, y );
					if( state != MazeConfig.STEP ) {
						switch( state ) {
							case MazeConfig.HOLE: {
								printer.println( "H " + x + "," + y );
								break;
							}
							case MazeConfig.MONSTER: {
								printer.println( "M " + x + "," + y );
								break;
							}
						}
					}
				}
			}
			printer.println( "C" + " " + maze.getCookieStartX() + "," + maze.getCookieStartY() );
			printer.println( "D" + " " + maze.getStartDirection().name() );

			printer.close();
		} catch( Exception exception ) {
			throw new IOException( exception );
		}
	}

}
