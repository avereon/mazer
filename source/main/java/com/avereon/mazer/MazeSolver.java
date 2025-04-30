package com.avereon.mazer;

import com.avereon.product.Product;
import com.avereon.xenon.Xenon;
import com.avereon.xenon.ProgramTask;
import com.avereon.zerra.javafx.Fx;

public abstract class MazeSolver extends ProgramTask<Void> {

	protected Product product;

	protected MazeTool tool;

	private Maze maze;

	protected boolean execute;

	private boolean running;

	private int speed;

	private final Object stoplock = new Object();

	public MazeSolver( Xenon program, Product product, MazeTool tool ) {
		super(program);
		this.product = product;
		this.tool = tool;
		setPriority( Priority.LOW );
	}

	public Product getProduct() {
		return product;
	}

	public MazeTool getTool() {
		return tool;
	}

	public final Maze getMaze() {
		return maze;
	}

	public final MazeSolver setMaze( Maze maze ) {
		this.maze = maze;
		return this;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed( int speed ) {
		this.speed = speed;
	}

	@Override
	public Void call() {
		running = true;
		execute = true;
		try {
			execute();
		} finally {
			running = false;
			Fx.run( () -> getProgram().getActionLibrary().getAction( "runpause" ).setState( "run" ) );
			synchronized( stoplock ) {
				stoplock.notifyAll();
			}
		}
		return null;
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
