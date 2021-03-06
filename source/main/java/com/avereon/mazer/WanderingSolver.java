package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.product.Rb;
import com.avereon.util.Log;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;

import java.lang.System.Logger;
import java.util.Random;

public class WanderingSolver extends MazeSolver {

	private static final Logger log = Log.get();

	private Random random = new Random();

	public WanderingSolver( Program program, Product product, MazeTool editor ) {
		super( program, product, editor );
	}

	@Override
	public String toString() {
		return Rb.text( BundleKey.LABEL, "solver.random" );
	}

	@Override
	protected void execute() {
		getMaze().setColorScale( 2 );
		while( execute && !getMaze().isGridClear() ) {
			switch( random.nextInt( 4 ) ) {
				case 0: {
					getMaze().turnLeft();
					break;
				}
				case 1: {
					getMaze().turnRight();
					break;
				}
				case 2: {
					break;
				}
				case 3: {
					getMaze().turnRight();
					getMaze().turnRight();
					break;
				}
			}
			if( getMaze().isFrontClear() ) {
				try {
					getMaze().move();
				} catch( MoveException exception ) {
					log.log( Log.ERROR, exception );
					return;
				}
			}
			ThreadUtil.pause( 1000 / getSpeed() );
		}

	}

}
