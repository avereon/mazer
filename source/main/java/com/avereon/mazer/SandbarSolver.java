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

	private byte[][] map;

	private int width;

	private int height;

	private Random random = new Random();

	public SandbarSolver( Program program, Product product, MazeTool tool, Maze model ) {
		super( program, product, tool, model );
	}

	@Override
	public String toString() {
		return product.rb().text( BundleKey.LABEL, "solver.sandbar" );
	}

	@Override
	public void execute() {
		width = model.getWidth();
		height = model.getHeight();
		map = new byte[width][height];
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				int value = model.getCellConfig( x, y );
				map[x][y] = value >= MazeConfig.HOLE ? Byte.MAX_VALUE : 0;
			}
		}
		map[model.getX()][model.getY()]++;
		model.setColorScale( 16 );

		while( execute && !model.isGridClear() ) {
			// Determine which way to go.
			model.setDirection( determineDirection( model.getDirection() ) );

			if( model.isFrontClear() ) {
				// Move.
				try {
					model.move();
				} catch( MoveException exception ) {
					program.getNoticeManager().error( exception );
					return;
				}

				// Update the map.
				map[model.getX()][model.getY()]++;
			}
			ThreadUtil.pause( 50 );
		}
	}

	private Direction determineDirection( Direction current ) {
		// Collect the surrounding values.
		int x = model.getX();
		int y = model.getY();
		byte n = getValue( x, y - 1 );
		byte e = getValue( x + 1, y );
		byte s = getValue( x, y + 1 );
		byte w = getValue( x - 1, y );

		// Find lowest value.
		int v = Byte.MAX_VALUE;
		if( n < v ) v = n;
		if( e < v ) v = e;
		if( s < v ) v = s;
		if( w < v ) v = w;

		List<Direction> options = new ArrayList<Direction>( 4 );
		if( n == v ) options.add( Direction.NORTH );
		if( e == v ) options.add( Direction.EAST );
		if( s == v ) options.add( Direction.SOUTH );
		if( w == v ) options.add( Direction.WEST );

		// Prefer the current direction.
		if( options.contains( current ) ) return current;

		return options.get( random.nextInt( options.size() ) );
	}

	private byte getValue( int x, int y ) {
		if( x < 0 || x >= width ) return Byte.MAX_VALUE;
		if( y < 0 || y >= height ) return Byte.MAX_VALUE;
		return map[x][y];
	}
}
