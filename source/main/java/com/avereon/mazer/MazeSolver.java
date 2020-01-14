package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.xenon.Program;

public abstract class MazeSolver implements Runnable {

	protected Program program;

	protected Product product;

	protected MazeTool editor;

	private Maze maze;

	protected boolean execute;

	private boolean running;

	private final Object stoplock = new Object();

	public MazeSolver( Program program, Product product, MazeTool editor ) {
		this.program = program;
		this.product = product;
		this.editor = editor;
	}

	public final Maze getMaze() {
		return maze;
	}

	public final MazeSolver setMaze( Maze maze ) {
		this.maze = maze;
		return this;
	}

	@Override
	public void run() {
		running = true;
		execute = true;
		try {
			//editor.updateActionState();
			execute();
		} finally {
			running = false;
			//editor.updateActionState();
			synchronized( stoplock ) {
				stoplock.notifyAll();
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void stop() {
		this.execute = false;
	}

	public void stopAndWait() {
		if( !isRunning() ) return;
		synchronized( stoplock ) {
			stop();
			try {
				stoplock.wait();
			} catch( InterruptedException exception ) {
				// Intentionally ignore exception.
			}
		}
	}

	protected abstract void execute();

}
