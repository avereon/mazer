package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.product.Rb;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.RbKey;
import com.avereon.xenon.Program;
import lombok.CustomLog;

import java.util.Random;

@CustomLog
public class WanderingSolver extends MazeSolver {

	private final Random random = new Random();

	public WanderingSolver( Program program, Product product, MazeTool editor ) {
		super( program, product, editor );
	}

	@Override
	public String toString() {
		return Rb.text( RbKey.LABEL, "solver.random" );
	}

	@Override
	protected void execute() {
		getMaze().setColorScale( 2 );
		while( execute && !getMaze().isGridClear() ) {
			switch( random.nextInt( 4 ) ) {
				case 0 -> getMaze().turnLeft();
				case 1 -> getMaze().turnRight();
				case 2 -> {}
				case 3 -> {
					getMaze().turnRight();
					getMaze().turnRight();
				}
			}
			if( getMaze().isFrontClear() ) {
				try {
					getMaze().move();
				} catch( MoveException exception ) {
					log.atError( exception ).log();
					return;
				}
			}
			ThreadUtil.pause( 1000 / getSpeed() );
		}

	}

}
