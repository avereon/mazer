package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.LogUtil;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class StackSolver extends MazeSolver {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private Deque<State> stack = new LinkedBlockingDeque<>();

	public StackSolver( Program program, Product product, MazeTool tool, Maze model ) {
		super( program, product, tool, model );
	}

	@Override
	public String toString() {
		return product.rb().text( BundleKey.LABEL, "solver.stack" );
	}

	@Override
	protected void execute() {
		while( execute && !model.isGridClear() ) {
			determineDirection( true );
			move();
			ThreadUtil.pause( 50 );
		}
	}

	private void turnLeft() {
		stack.push( new State( model ) );
		model.turnLeft();
	}

	private void turnRight() {
		stack.push( new State( model ) );
		model.turnRight();
	}

	private void move() {
		if( !model.isFrontClear() ) return;
		stack.push( new State( model ) );
		try {
			model.move();
		} catch( MoveException exception ) {
			program.getNoticeManager().error( exception );
		}
	}

	private void backup() {
		log.info( "Backup..." );
		State state = stack.poll();
		if( state == null ) return;
		model.setCookie( state.getX(), state.getY() );
		model.setDirection( state.getDirection() );
		model.incrementStepCount();
		determineDirection( false );
	}

	private void determineDirection( boolean forward ) {
		Direction direction = model.getDirection();
		int left = direction.getLeftValue( model );
		int right = direction.getRightValue( model );
		int front = direction.getFrontValue( model );

		boolean canLeft = left >= 0;
		boolean canRight = right >= 0;
		boolean canForward = front >= 0;

		if( canForward && (!canLeft || front <= left) && (!canRight || front <= right) ) {
			return;
		} else {
			if( canRight && (!canLeft || right <= left) ) {
				turnRight();
				return;
			} else if( canLeft ) {
				turnLeft();
				return;
			}
		}

		backup();
	}

	private static final class State {

		private int x;

		private int y;

		private Direction direction;

		public State( Maze model ) {
			this.x = model.getX();
			this.y = model.getY();
			this.direction = model.getDirection();
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public Direction getDirection() {
			return direction;
		}

	}

}
