package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.node.Node;
import com.avereon.xenon.transaction.Txn;
import com.avereon.xenon.transaction.TxnEvent;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;

public class Maze extends Node {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	private static final int MIN_WIDTH = 1;

	private static final int MIN_HEIGHT = 1;

	private static final int DEFAULT_WIDTH = 10;

	private static final int DEFAULT_HEIGHT = 10;

	public Maze() {
		setSize( DEFAULT_WIDTH, DEFAULT_HEIGHT );
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

	int getCellState( int x, int y ) {
		return getValue( "cell-" + x + "-" + y, 0 );
	}

	void setCellState( int x, int y, int state ) {
		setValue( "cell-" + x + "-" + y, state );
	}

	@Override
	public void handle( TxnEvent event ) {
		//log.warn( "Maze " + event );
		super.handle( event );
	}

	@Override
	public String toString() {
		return super.toString( "width", "height" );
	}

}
