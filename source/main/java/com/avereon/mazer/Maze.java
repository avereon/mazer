package com.avereon.mazer;

import com.avereon.data.Node;
import com.avereon.transaction.Txn;
import com.avereon.transaction.TxnEvent;
import com.avereon.transaction.TxnException;
import lombok.CustomLog;

/**
 * The Maze class is the data model used for the Maze asset type, asset and
 * tool. It subclasses {@link Node} to utilize the
 * functionality provided in Node that simplifies the management of attributes,
 * data structure and the modified flag. Since all assets are also Nodes, if the
 * asset data model is also a Node then the modified flag is automatically
 * managed by the Node logic.
 */
@CustomLog
public class Maze extends Node {

	private static final int UNVISITED = 0;

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	private static final String COOKIE_X = "cookie-x";

	private static final String COOKIE_Y = "cookie-y";

	private static final String DIRECTION = "direction";

	private static final String COLOR_SCALE = "color-scale";

	private static final String START_X = "start-x";

	private static final String START_Y = "start-y";

	private static final String START_DIRECTION = "start-direction";

	private static final int MIN_WIDTH = 1;

	private static final int MIN_HEIGHT = 1;

	static final int DEFAULT_WIDTH = 10;

	static final int DEFAULT_HEIGHT = 10;

	private int steps;

	public Maze() {
		addModifyingKeys( WIDTH, HEIGHT, START_DIRECTION );
		setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
		setStartDirection( Direction.EAST );
		setCookieStart( 0, 0 );
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
					addModifyingKeys( "cell-" + x + "-" + y );
					setCellConfig( x, y, MazeConfig.STEP );
				}
			}
			Txn.commit();
		} catch( Exception exception ) {
			log.atWarn().withCause( exception ).log( "Error changing maze size" );
		}
	}

	public int getX() {
		return getValue( COOKIE_X );
	}

	public int getY() {
		return getValue( COOKIE_Y );
	}

	public boolean isCookie( int x, int y ) {
		return getX() == x && getY() == y;
	}

	public void setCookie( int x, int y ) {
		try {
			set( x, y, get( x, y ) + 1 );
			Txn.create();
			setValue( COOKIE_X, x );
			setValue( COOKIE_Y, y );
			Txn.commit();
		} catch( TxnException exception ) {
			log.atError().withCause( exception ).log( "Error setting cookie location" );
		}
	}

	public int getCellConfig( int x, int y ) {
		return getValue( "cell-" + x + "-" + y, MazeConfig.STEP );
	}

	void setCellConfig( int x, int y, int state ) {
		setValue( "cell-" + x + "-" + y, state );
		if( state == MazeConfig.COOKIE ) setCookieStart( x, y );
		if( state == MazeConfig.HOLE ) set( x, y, -1 );
	}

	public int getCookieStartX() {
		return getValue( START_X, 0 );
	}

	public int getCookieStartY() {
		return getValue( START_Y, 0 );
	}

	public void setCookieStart( int x, int y ) {
		try {
			Txn.create();
			setValue( START_X, x );
			setValue( START_Y, y );
			Txn.commit();
		} catch( TxnException exception ) {
			exception.printStackTrace();
		}
	}

	public Direction getStartDirection() {
		return getValue( START_DIRECTION, Direction.EAST );
	}

	public void setStartDirection( Direction direction ) {
		setValue( START_DIRECTION, direction );
	}

	public Direction getDirection() {
		return getValue( DIRECTION );
	}

	public void setDirection( Direction direction ) {
		setValue( DIRECTION, direction );
	}

	public double getColorScale() {
		return getValue( COLOR_SCALE );
	}

	public void setColorScale( double scale ) {
		setValue( COLOR_SCALE, scale );
	}

	public int getStepCount() {
		return steps;
	}

	public void incrementStepCount() {
		steps++;
	}

	public int get( int x, int y ) {
		return getValue( "work-" + x + "-" + y, UNVISITED );
	}

	public void set( int x, int y, int value ) {
		setValue( "work-" + x + "-" + y, value );
	}

	public void reset() {
		setCookie( getCookieStartX(), getCookieStartY() );
		setDirection( getStartDirection() );
		steps = 0;

		// Clear all visit counts
		int width = getWidth();
		int height = getHeight();
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				boolean isHole = getCellConfig( x, y ) == MazeConfig.HOLE;
				set( x, y, isHole ? -1 : UNVISITED );
			}
		}

		// Sets the visit count for the cookie
		set( getX(), getY(), 1 );
	}

	public boolean isGridClear() {
		int width = getWidth();
		int height = getHeight();
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				if( getCellConfig( x, y ) != MazeConfig.HOLE && get( x, y ) == UNVISITED ) return false;
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
		//log.log( Log.INFO,  "Turn left..." );
		getDirection().turnLeft( this );
	}

	public void turnRight() {
		//log.log( Log.INFO,  "Turn right..." );
		getDirection().turnRight( this );
	}

	public void move() throws MoveException {
		//log.log( Log.INFO,  "Move forward..." );
		move( 1 );
	}

	public void move( int steps ) throws MoveException {
		getDirection().move( this, steps );
	}

	@Override
	public void dispatch( TxnEvent event ) {
		super.dispatch( event );
		//log.log( Log.WARN,  "Maze " + event.getEventType() + ": " + event );
	}

}
