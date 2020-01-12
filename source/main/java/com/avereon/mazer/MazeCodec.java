package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.LogUtil;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.Codec;
import org.slf4j.Logger;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;

public class MazeCodec extends Codec {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

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
		log.warn( "Loading maze: " + asset );

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
						maze.setCellState( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ), Maze.HOLE );
						break;
					}
					case 'M': {
						maze.setCellState( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ), Maze.MONSTER );
						break;
					}
					case 'C': {
						maze.setCookie( Integer.parseInt( data[ 0 ] ), Integer.parseInt( data[ 1 ] ) );
						break;
					}
					case 'D': {
						maze.setDirection( Direction.valueOf( data[ 0 ] ) );
						break;
					}
				}
			}

			asset.setModel( maze );
		} catch( Exception exception ) {
			throw new IOException( exception );
		}
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		log.warn( "Saving asset: " + asset );
		try {
			Maze maze = asset.getModel();
			int width = maze.getWidth();
			int height = maze.getHeight();

			PrintStream printer = new PrintStream( output, true, StandardCharsets.UTF_8 );

			printer.println( "S" + " " + width + "," + height );
			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					int state = maze.getCellState( x, y );
					if( state < Maze.DEFAULT ) printer.println( (state == Maze.MONSTER ? "M" : "H") + " " + x + "," + y );
				}
			}
			printer.println( "C" + " " + maze.getX() + "," + maze.getY() );
			printer.println( "D" + " " + maze.getDirection().name() );

			printer.close();
		} catch( Exception exception ) {
			throw new IOException( exception );
		}
	}

}
