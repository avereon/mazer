package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.LogUtil;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Random;

public class RandomSolver extends MazeSolver {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private Random random = new Random();

	public RandomSolver( Program program, Product product, MazeTool editor, Maze model ) {
		super( program, product, editor, model );
	}

	@Override
	public String toString() {
		return product.rb().text( BundleKey.LABEL, "solver.random" );
	}

	@Override
	protected void execute() {
		model.setColorScale( 2 );
		while( execute && !model.isGridClear() ) {
			//log.info( "Random solver step..." );
			switch( random.nextInt( 4 ) ) {
				case 0: {
					model.turnLeft();
					break;
				}
				case 1: {
					model.turnRight();
					break;
				}
				case 2: {
					break;
				}
				case 3: {
					model.turnRight();
					model.turnRight();
					break;
				}
			}
			if( model.isFrontClear() ) {
				try {
					model.move();
				} catch( MoveException exception ) {
					program.getNoticeManager().error( exception );
					return;
				}
			}
			ThreadUtil.pause( 50 );
		}

	}

}
