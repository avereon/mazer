package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.node.Node;
import com.avereon.xenon.transaction.Txn;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Maze extends Node {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	public static final int DEFAULT = 0;

	public static final int MONSTER = -1;

	public static final int HOLE = Integer.MIN_VALUE;

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	private static final int MIN_WIDTH = 1;

	private static final int MIN_HEIGHT = 1;

	private static final int DEFAULT_WIDTH = 10;

	private static final int DEFAULT_HEIGHT = 10;

	private int x;

	private int y;

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

	public Maze setWidth( int width ) {
		setSize( width, getHeight() );
		return this;
	}

	public int getHeight() {
		return getValue( HEIGHT );
	}

	public Maze setHeight( int height ) {
		setSize( getWidth(), height );
		return this;
	}

	public void setSize( int width, int height ) {
		if( width < MIN_WIDTH ) width = MIN_WIDTH;
		if( height < MIN_HEIGHT ) height = MIN_HEIGHT;
		try {
			Txn.create();
			clear();
			setValue( WIDTH, width );
			setValue( HEIGHT, height );
			Txn.commit();
		} catch( Exception exception ) {
			log.warn( "Error changing maze size", exception );
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setCookie( int x, int y ) {
		this.x = x;
		this.y = y;
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

	public void incrementSteps() {
		steps++;
	}

	//	@Override
	//	public void dispatchEvent( NodeEvent event ) {
	//		super.dispatchEvent( event );
	//		log.warn( "Maze " + event.getType() + ": " + event.getNode() );
	//	}

}
