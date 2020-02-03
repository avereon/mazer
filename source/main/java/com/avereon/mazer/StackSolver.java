package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.util.Log;
import com.avereon.util.ThreadUtil;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.Program;

import java.lang.System.Logger;
import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

public class StackSolver extends MazeSolver {

	private static final Logger log = Log.log();

	private Deque<State> stack = new LinkedBlockingDeque<>();

	public StackSolver( Program program, Product product, MazeTool tool ) {
		super( program, product, tool );
	}

	@Override
	public String toString() {
		return product.rb().text( BundleKey.LABEL, "solver.stack" );
	}

	@Override
	protected void execute() {
		while( execute && !getMaze().isGridClear() ) {
			determineMove( true );
			move();
		}
	}

	private void turnLeft() {
		stack.push( new State( getMaze() ) );
		getMaze().turnLeft();
	}

	private void turnRight() {
		stack.push( new State( getMaze() ) );
		getMaze().turnRight();
	}

	private void move() {
		if( !getMaze().isFrontClear() ) return;
		try {
			stack.push( new State( getMaze() ) );
			getMaze().move();
		} catch( MoveException exception ) {
			getProgram().getNoticeManager().error( exception );
		}
	}

	private void backup() {
		State state = stack.poll();
		if( state == null ) return;
		getMaze().setCookie( state.getX(), state.getY() );
		getMaze().setDirection( state.getDirection() );
		getMaze().incrementStepCount();
		determineMove( false );
	}

	private void determineMove( boolean forward ) {
		ThreadUtil.pause( 1000 / getSpeed() );
		Direction direction = getMaze().getDirection();
		int left = direction.getLeftValue( getMaze() );
		int front = direction.getFrontValue( getMaze() );
		int right = direction.getRightValue( getMaze() );

		boolean canLeft = left >= 0;
		boolean canRight = right >= 0;
		boolean canForward = front >= 0;

		if( forward && canForward && (!canLeft || front <= left) && (!canRight || front <= right) ) {
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
