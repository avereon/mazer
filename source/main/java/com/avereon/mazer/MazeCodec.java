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
			Maze maze = new Maze();

			// First line is the maze size
			String[] size = reader.readLine().split( " " );
			int width = Integer.parseInt( size[ 0 ] );
			int height = Integer.parseInt( size[ 1 ] );
			maze.setSize( width, height );

			// Load the maze state
			int cellCount = width * height;
			for( int index = 0; index < cellCount; index++ ) {
				String[] cellData = reader.readLine().split( " " );
				int x = Integer.parseInt( cellData[0]);
				int y = Integer.parseInt( cellData[1]);
				int s = Integer.parseInt( cellData[2]);
				maze.setCellState( x,y,s );
			}

			asset.setModel( maze );
		} catch( Exception exception ) {
			throw new IOException( exception );
		}
	}

	@Override
	public void save( Asset asset, OutputStream output ) throws IOException {
		log.warn( "Saving asset: " + asset  );
		try {
			Maze maze = asset.getModel();
			int width = maze.getWidth();
			int height = maze.getHeight();

			PrintStream printer = new PrintStream( output, true, StandardCharsets.UTF_8 );
			printer.println( width + " " + height );
			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					printer.println( x + " " + y + " " + maze.getCellState( x, y ) );
				}
			}

			printer.close();
		} catch( Exception exception ) {
			throw new IOException( exception );
		}
	}

}
