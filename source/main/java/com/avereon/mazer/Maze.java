package com.avereon.mazer;

import com.avereon.xenon.node.Node;

public class Maze extends Node {

	private static final String WIDTH = "width";

	private static final String HEIGHT = "height";

	private static final int MIN_WIDTH = 1;

	private static final int MIN_HEIGHT = 1;

	private static final int DEFAULT_WIDTH = 10;

	private static final int DEFAULT_HEIGHT = 10;

	public Maze() {
		setWidth( DEFAULT_WIDTH );
		setHeight( DEFAULT_HEIGHT );
	}

	public int getWidth() {
		return getValue( WIDTH );
	}

	public Maze setWidth( int width ) {
		if( width < MIN_WIDTH ) width = MIN_WIDTH;
		setValue( WIDTH, width );
		return this;
	}

	public int getHeight() {
		return getValue( HEIGHT );
	}

	public Maze setHeight( int height ) {
		if( height < MIN_HEIGHT ) height = MIN_HEIGHT;
		setValue( HEIGHT, height );
		return this;
	}

}
