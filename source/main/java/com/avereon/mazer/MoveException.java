package com.avereon.mazer;

public class MoveException extends Exception {

	public MoveException() {
		super();
	}

	public MoveException( String message ) {
		super( message );
	}

	public MoveException( Throwable cause ) {
		super( cause );
	}

	public MoveException( String message, Throwable cause ) {
		super( message, cause );
	}

}
