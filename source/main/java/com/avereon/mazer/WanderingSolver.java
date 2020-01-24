package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.LogUtil;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Random;

public class WanderingSolver extends MazeSolver {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private Random random = new Random();

	public WanderingSolver( Program program, Product product, MazeTool editor ) {
		super( program, product, editor );
	}

	@Override
	public String toString() {
		return product.rb().text( BundleKey.LABEL, "solver.random" );
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
					getProgram().getNoticeManager().error( exception );
					return;
				}
			}
			ThreadUtil.pause( 1000 / getSpeed() );
		}

	}

}
