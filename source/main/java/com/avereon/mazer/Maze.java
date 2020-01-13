package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.node.Node;
import com.avereon.xenon.transaction.Txn;
import com.avereon.xenon.transaction.TxnEvent;
import com.avereon.xenon.transaction.TxnException;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Maze extends Node {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	public static final int DEFAULT = 0;

	public static final int MONSTER = -1;

	public static final int COOKIE = -2;

	public static final int HOLE = Integer.MIN_VALUE;

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	private static final String COOKIE_X = "cookie-x";

	private static final String COOKIE_Y = "cookie-y";

	private static final int MIN_WIDTH = 1;

	private static final int MIN_HEIGHT = 1;

	private static final int DEFAULT_WIDTH = 10;

	private static final int DEFAULT_HEIGHT = 10;

	private Direction direction;

	private int steps;

	public Maze() {
		setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		setDirection( Direction.EAST );
		setCookie( 1, 1 );
	}

	public int getWidth() {
		return getValue( WIDTH );
	}

	public int getHeight() {
		return getValue( HEIGHT );
	}

	public void setSize( int width, int height ) {
		if( width < MIN_WIDTH ) width = MIN_WIDTH;
		if( height < MIN_HEIGHT ) height = MIN_HEIGHT;
		try {
			Txn.create();
			clear();
			setValue( WIDTH, width );
			setValue( HEIGHT, height );
			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					setCellState( x, y, DEFAULT );
				}
			}
			Txn.commit();
		} catch( Exception exception ) {
			log.warn( "Error changing maze size", exception );
		}
	}

	public int getX() {
		return getResource( COOKIE_X );
	}

	public int getY() {
		return getResource( COOKIE_Y );
	}

	public void setCookie( int x, int y ) {
		try {
			Txn.create();
			setCellState( x, y, Maze.DEFAULT );
			putResource( COOKIE_X, x );
			putResource( COOKIE_Y, y );
			Txn.commit();
		} catch( TxnException exception ) {
			log.error( "Error setting cookie location", exception );
		}
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection( Direction direction ) {
		this.direction = direction;
	}

	public int getCellState( int x, int y ) {
		return getValue( "cell-" + x + "-" + y, Maze.DEFAULT );
	}

	void setCellState( int x, int y, int state ) {
		setValue( "cell-" + x + "-" + y, state );
	}

	public int getStepCount() {
		return steps;
	}

	public void incrementStepCount() {
		steps++;
	}

		@Override
		public void dispatch( TxnEvent event ) {
			super.dispatch( event );
			//log.warn( "Maze " + event.getEventType() + ": " + event );
		}

}
