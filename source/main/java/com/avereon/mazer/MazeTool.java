package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.*;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.task.Task;
import com.avereon.xenon.tool.ProgramTool;
import com.avereon.xenon.workpane.ToolException;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class MazeTool extends ProgramTool {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private static final int DEFAULT_ZOOM = 20;

	private static Map<Integer, Background> backgrounds;

	private MazePropertiesAction mazePropertiesAction;

	private ResetAction resetAction;

	private RunToggleAction runAction;

	private ComboBox<String> chooser;

	private Label steps;

	private GridPane grid;

	private Cell[][] cells;

	private int zoom = DEFAULT_ZOOM;

	private MazeSolver solver;

	static {
		backgrounds = new HashMap<>();
		backgrounds.put( MazeConfig.STEP, createBackground( "#80808080" ) );
		backgrounds.put( MazeConfig.HOLE, createBackground( "#00000000" ) );
		backgrounds.put( MazeConfig.COOKIE, createBackground( "#806000C0" ) );
		backgrounds.put( MazeConfig.MONSTER, createBackground( "#00800080" ) );
	}

	public MazeTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		setGraphic( product.getProgram().getIconLibrary().getIcon( "mazer" ) );
		mazePropertiesAction = new MazePropertiesAction( product.getProgram() );
		resetAction = new ResetAction( product.getProgram() );
		runAction = new RunToggleAction( product.getProgram() );

		grid = new GridPane();
		grid.setAlignment( Pos.CENTER );

		chooser = new ComboBox<>();
		chooser.getItems().add( getProduct().rb().text( BundleKey.LABEL, "solver.stack" ) );
		chooser.getItems().add( getProduct().rb().text( BundleKey.LABEL, "solver.sandbar" ) );
		chooser.getItems().add( getProduct().rb().text( BundleKey.LABEL, "solver.random" ) );
		chooser.getSelectionModel().select( 0 );

		steps = new Label( getProduct().rb().text( BundleKey.PROMPT, "steps" ) + 0 );

		BorderPane pane = new BorderPane( grid, chooser, null, steps, null );
		pane.setBorder( new Border( new BorderStroke( Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths( UiFactory.PAD ) ) ) );

		getChildren().addAll( pane );
	}

	@Override
	protected void assetReady( OpenAssetRequest request ) throws ToolException {
		assetRefreshed();
	}

	@Override
	protected void assetRefreshed() throws ToolException {
		Maze maze = getMaze();
		int width = maze.getWidth();
		int height = maze.getHeight();

		if( cells == null || cells.length != width || cells[ 0 ].length != height ) rebuildGrid();

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				int config = maze.getCellConfig( x, y );
				Cell cell = cells[ x ][ y ];
				cell.setSize( zoom );
				cell.setConfig( config == MazeConfig.COOKIE ? MazeConfig.STEP : config );
				cell.setVisits( maze.get( x, y ) );
			}
		}

		cells[ maze.getX() ][ maze.getY() ].setConfig( MazeConfig.COOKIE );

		steps.setText( getProduct().rb().text( BundleKey.PROMPT, "steps" ) + maze.getStepCount() );
	}

	private void rebuildGrid() {
		grid.getChildren().clear();

		Maze maze = getMaze();
		int width = maze.getWidth();
		int height = maze.getHeight();
		cells = new Cell[ width ][ height ];
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				grid.add( cells[ x ][ y ] = new Cell( maze, x, y ), x, y );
			}
		}
	}

	@Override
	protected void activate() throws ToolException {
		pushAction( "properties", mazePropertiesAction );
		//pushAction( "undo", resetAction );
		//pushAction( "redo", runAction );
		pushAction( "reset", resetAction );
		pushAction( "runpause", runAction );

		getProgram().getWorkspaceManager().getActiveWorkspace().pushToolbarActions( "reset", "runpause" );
	}

	@Override
	protected void conceal() throws ToolException {
		getProgram().getWorkspaceManager().getActiveWorkspace().pullToolbarActions();

		pullAction( "properties", mazePropertiesAction );
		//pullAction( "undo", resetAction );
		//pullAction( "redo", runAction );
		pullAction( "reset", resetAction );
		pullAction( "runpause", runAction );
	}

	private Maze getMaze() {
		return (Maze)getAsset().getModel();
	}

	public MazeSolver getSolver() {
		return solver;
	}

	public void setSolver( MazeSolver solver ) {
		this.solver = solver;
	}

	// TODO Since this is more of an asset action and not a tool action it may
	// be more appropriate to move this to the asset manager.
	private static class MazePropertiesAction extends Action {

		private MazePropertiesAction( Program program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			// TODO Open the properties tool with this tool's asset's properties.
			getProgram().getNoticeManager().addNotice( new Notice( "Maze Tool", "Opening the asset properties..." ) );
		}

	}

	private class ResetAction extends Action {

		protected ResetAction( Program program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent actionEvent ) {
			MazeSolver solver = getSolver();
			if( solver != null ) solver.stop();
			getMaze().reset();
		}

	}

	private class RunToggleAction extends Action {

		protected RunToggleAction( Program program ) {
			super( program );
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public void handle( ActionEvent event ) {
			MazeSolver solver = getSolver();

			if( solver != null && solver.isRunning() ) {
				solver.stop();
			} else {
				switch( chooser.getSelectionModel().getSelectedIndex() ) {
					case 1: {
						solver = new SandbarSolver( getProgram(), getProduct(), MazeTool.this );
						break;
					}
					case 2: {
						solver = new WanderingSolver( getProgram(), getProduct(), MazeTool.this );
						break;
					}
					default: {
						solver = new StackSolver( getProgram(), getProduct(), MazeTool.this );
						break;
					}
				}
				setSolver( solver.setMaze( getMaze() ) );

				getMaze().reset();
				Task<?> task = Task.of( String.valueOf( solver ), solver );
				task.setPriority( Task.Priority.LOW );
				getProgram().getTaskManager().submit( task );
			}
		}

	}

	private static Background createBackground( String color ) {
		return new Background( new BackgroundFill( Color.web( color ), CornerRadii.EMPTY, Insets.EMPTY ) );
	}

	private static class Cell extends Region {

		private Maze maze;

		private int x;

		private int y;

		private int size;

		private int visits;

		private int config;

		private Background visited = createBackground( "#80000040" );

		public Cell( Maze maze, int x, int y ) {
			this.maze = maze;
			this.x = x;
			this.y = y;
			setBackground( backgrounds.get( MazeConfig.STEP ) );

			setOnMousePressed( e -> {
				if( e.isPrimaryButtonDown() ) {
					int newState;
					if( e.isShiftDown() ) {
						newState = getVisits() == MazeConfig.MONSTER ? MazeConfig.STEP : MazeConfig.MONSTER;
					} else if( e.isControlDown() ) {
						newState = MazeConfig.STEP;
					} else {
						newState = MazeConfig.HOLE;
					}
					maze.setCellConfig( x, y, newState );
				}
				if( e.isSecondaryButtonDown() ) {
					maze.setCookieStart( x, y );
					maze.reset();
				}

			} );
		}

		public int getSize() {
			return size;
		}

		public Cell setSize( int size ) {
			setPrefSize( size, size );
			this.size = size;
			return this;
		}

		public void setConfig( int config ) {
			this.config = config;
			setBackground( backgrounds.get( config ) );

			if( config == MazeConfig.STEP || config == MazeConfig.MONSTER ) {
				if( visits > 0 ) setBackground( visited );
			}
		}

		public int getVisits() {
			return this.visits;
		}

		public Cell setVisits( int visits ) {
			this.visits = visits;
			return this;
		}

	}

}
