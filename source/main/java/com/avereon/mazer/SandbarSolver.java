package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.LogUtil;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SandbarSolver extends MazeSolver {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private int[][] map;

	private int width;

	private int height;

	private Random random = new Random();

	public SandbarSolver( Program program, Product product, MazeTool tool ) {
		super( program, product, tool );
	}

	@Override
	public String toString() {
		return product.rb().text( BundleKey.LABEL, "solver.sandbar" );
	}

	@Override
	public void execute() {
		width = getMaze().getWidth();
		height = getMaze().getHeight();
		map = new int[ width ][ height ];
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				int value = getMaze().getCellConfig( x, y );
				map[ x ][ y ] = value == MazeConfig.HOLE ? -1 : 0;
			}
		}
		map[ getMaze().getX() ][ getMaze().getY() ]++;
		getMaze().setColorScale( 16 );

		while( execute && !getMaze().isGridClear() ) {
			// Determine which way to go
			getMaze().setDirection( determineDirection( getMaze().getDirection() ) );

			if( getMaze().isFrontClear() ) {
				// Move
				try {
					getMaze().move();
				} catch( MoveException exception ) {
					getProgram().getNoticeManager().error( exception );
					return;
				}

				// Update the map
				map[ getMaze().getX() ][ getMaze().getY() ]++;
			}
			ThreadUtil.pause( 1000 / getSpeed() );
		}
	}

	private Direction determineDirection( Direction current ) {
		// Collect the surrounding values
		int x = getMaze().getX();
		int y = getMaze().getY();
		int n = getValue( x, y - 1 );
		int e = getValue( x + 1, y );
		int s = getValue( x, y + 1 );
		int w = getValue( x - 1, y );

		// Find lowest value that is not -1
		int v = Byte.MAX_VALUE;
		if( n >= 0 && n < v ) v = n;
		if( e >= 0 && e < v ) v = e;
		if( s >= 0 && s < v ) v = s;
		if( w >= 0 && w < v ) v = w;

		List<Direction> options = new ArrayList<>( 4 );
		if( n >= 0 && n == v ) options.add( Direction.NORTH );
		if( e >= 0 && e == v ) options.add( Direction.EAST );
		if( s >= 0 && s == v ) options.add( Direction.SOUTH );
		if( w >= 0 && w == v ) options.add( Direction.WEST );

		// Prefer the current direction
		if( options.contains( current ) ) return current;

		return options.get( random.nextInt( options.size() ) );
	}

	private int getValue( int x, int y ) {
		if( x < 0 || x >= width ) return -1;
		if( y < 0 || y >= height ) return -1;
		return map[ x ][ y ];
	}

}
