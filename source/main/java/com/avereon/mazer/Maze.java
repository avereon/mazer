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

	private static final int UNVISITED = 0;

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	private static final String COOKIE_X = "cookie-x";

	private static final String COOKIE_Y = "cookie-y";

	private static final String DIRECTION = "direction";

	private static final String COLOR_SCALE = "color-scale";

	private static final int MIN_WIDTH = 1;

	private static final int MIN_HEIGHT = 1;

	private static final int DEFAULT_WIDTH = 10;

	private static final int DEFAULT_HEIGHT = 10;

	private int steps;

	public Maze() {
		setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		setDirection( Direction.EAST );
		setCookie( 0, 0 );
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
					setCellConfig( x, y, MazeConfig.STEP );
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

	public boolean isCookie( int x, int y ) {
		return getX() == x && getY() == y;
	}

	public void setCookie( int x, int y ) {
		log.warn( "Setting cookie location: " + x + "," + y );
		try {
			set( x, y, get( x, y ) + 1 );
			Txn.create();
			putResource( COOKIE_X, x );
			putResource( COOKIE_Y, y );
			Txn.commit();
		} catch( TxnException exception ) {
			log.error( "Error setting cookie location", exception );
		}
	}

	public int getCellConfig( int x, int y ) {
		return getValue( "cell-" + x + "-" + y, MazeConfig.STEP );
	}

	void setCellConfig( int x, int y, int state ) {
		setValue( "cell-" + x + "-" + y, state );
		if( state == MazeConfig.COOKIE ) setCookie( x, y );
		if( state == MazeConfig.HOLE ) set( x, y, MazeConfig.HOLE );
	}

	public Direction getDirection() {
		return getResource( DIRECTION );
	}

	public void setDirection( Direction direction ) {
		putResource( DIRECTION, direction );
	}

	public double getColorScale() {
		return getResource( COLOR_SCALE );
	}

	public void setColorScale( double scale ) {
		putResource( COLOR_SCALE, scale );
	}

	public int getStepCount() {
		return steps;
	}

	public void incrementStepCount() {
		steps++;
	}

	public int get( int x, int y ) {
		return getResource( "work-" + x + "-" + y, UNVISITED );
	}

	public void set( int x, int y, int value ) {
		putResource( "work-" + x + "-" + y, value );
	}

	public void resetVisits() {
		int width = getWidth();
		int height = getHeight();
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				set( x, y, UNVISITED );
			}
		}
	}

	public boolean isGridClear() {
		int width = getWidth();
		int height = getHeight();
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				if( get( x, y ) == UNVISITED ) return false;
			}
		}
		return true;
	}

	public boolean isLeftClear() {
		return getDirection().isLeftClear( this );
	}

	public boolean isFrontClear() {
		return getDirection().isFrontClear( this );
	}

	public boolean isRightClear() {
		return getDirection().isRightClear( this );
	}

	public void turnLeft() {
		log.info( "Turn left..." );
		getDirection().turnLeft( this );
	}

	public void turnRight() {
		log.info( "Turn right..." );
		getDirection().turnRight( this );
	}

	public void move() throws MoveException {
		log.info( "Move forward..." );
		move( 1 );
	}

	public void move( int steps ) throws MoveException {
		getDirection().move( this, steps );
	}

	@Override
	public void dispatch( TxnEvent event ) {
		super.dispatch( event );
		//log.warn( "Maze " + event.getEventType() + ": " + event );
	}

}
