package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.node.Node;
import com.avereon.xenon.transaction.Txn;
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
			// FIXME This causes an NPE when loading
			//clear();
			setValue( WIDTH, width );
			setValue( HEIGHT, height );
			Txn.commit();
		} catch( Exception exception ) {
			log.warn( "Error changing maze size", exception );
		}
	}

	public int getCellState( int x, int y ) {
		Column column = getValue( String.valueOf( x ) );
		return column == null ? 0 : column.getValue( y );
	}

	void setCellState( int x, int y, int state ) {
		Column column = getValue( String.valueOf( x ) );
		if( column == null ) setValue( String.valueOf( x ), column = new Column() );
		column.setValue( y, state );
	}

	private static class Column extends Node {

		public int getValue( int index ) {
			Integer value = getValue( String.valueOf( index ) );
			return value == null ? 0 : value;
		}

		public void setValue( int index, int state ) {
			setValue( String.valueOf( index ), state );
		}

	}

}
