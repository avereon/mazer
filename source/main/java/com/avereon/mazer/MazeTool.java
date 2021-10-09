package com.avereon.mazer;

import com.avereon.data.NodeEvent;
import com.avereon.event.EventHandler;
import com.avereon.product.Rb;
import com.avereon.skill.RunPauseResettable;
import com.avereon.xenon.BundleKey;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.ProgramTool;
import com.avereon.xenon.UiFactory;
import com.avereon.xenon.action.common.ResetAction;
import com.avereon.xenon.action.common.RunPauseAction;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.zarra.javafx.Fx;
import com.avereon.zarra.javafx.FxUtil;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.CustomLog;

import java.util.HashMap;
import java.util.Map;

/**
 * The maze tool edit a maze and runs a maze solver for the maze.
 * <p>
 * This tool is part of an example mod for
 * <a href="https://www.avereon.com/product/xenon">Xenon</a>. The tool
 * demonstrates various capabilities and practices common to Xenon tools.
 * </p>
 */
@CustomLog
public class MazeTool extends ProgramTool implements RunPauseResettable {

	private static final int DEFAULT_ZOOM = 20;

	private static final Map<Integer, Background> backgrounds;

	private final ResetAction resetAction;

	private final RunPauseAction runAction;

	private final TextField mazeWidth;

	private final TextField mazeHeight;

	private final ComboBox<String> chooser;

	private final Spinner<Integer> solverSpeed;

	private final Label steps;

	private final GridPane grid;

	private final IntegerProperty zoom;

	private MazeSolver solver;

	private Cell[][] cells;

	private final EventHandler<NodeEvent> modelChangeHandler;

	static {
		backgrounds = new HashMap<>();
		backgrounds.put( MazeConfig.STEP, createBackground( "#80404020" ) );
		backgrounds.put( MazeConfig.HOLE, createBackground( "#808080C0" ) );
		backgrounds.put( MazeConfig.COOKIE, createBackground( "#806000C0" ) );
		backgrounds.put( MazeConfig.MONSTER, createBackground( "#00800080" ) );
	}

	/**
	 * Create a maze tool.
	 * <p>
	 * The constructor of any tool requires the product associated with this tool,
	 * the Mazer mod in this case, as well as the asset this tool will be working
	 * on. This tool should have been registered to only work on MazeAssetType
	 * assets so that the asset provided to the tool will be a maze asset.
	 * </p>
	 *
	 * @param product The product associated with the tool
	 * @param asset The asset the tool will work on
	 */
	public MazeTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		this.zoom = new SimpleIntegerProperty( DEFAULT_ZOOM );

		resetAction = new com.avereon.xenon.action.common.ResetAction( product.getProgram(), this );
		runAction = new com.avereon.xenon.action.common.RunPauseAction( product.getProgram(), this );

		mazeWidth = new TextField( String.valueOf( Maze.DEFAULT_WIDTH ) );
		mazeWidth.setOnAction( e -> getMaze().setSize( Integer.parseInt( mazeWidth.getText() ), getMaze().getHeight() ) );
		mazeHeight = new TextField( String.valueOf( Maze.DEFAULT_HEIGHT ) );
		mazeHeight.setOnAction( e -> getMaze().setSize( getMaze().getWidth(), Integer.parseInt( mazeHeight.getText() ) ) );

		chooser = new ComboBox<>();
		chooser.getItems().add( Rb.text( BundleKey.LABEL, "solver.stack" ) );
		chooser.getItems().add( Rb.text( BundleKey.LABEL, "solver.sandbar" ) );
		chooser.getItems().add( Rb.text( BundleKey.LABEL, "solver.random" ) );
		chooser.getSelectionModel().select( 0 );

		solverSpeed = new Spinner<>( 10, 100, 10 );
		solverSpeed.valueProperty().addListener( ( o, d, n ) -> getSolver().setSpeed( n ) );

		grid = new GridPane();
		grid.setAlignment( Pos.CENTER );

		steps = new Label( Rb.text( BundleKey.PROMPT, "steps" ) + 0 );

		HBox hbox = new HBox( new Label( "Width: " ), mazeWidth, new Label( "Height: " ), mazeHeight, new Label( "Speed:" ), solverSpeed, chooser );
		hbox.setAlignment( Pos.BASELINE_LEFT );
		hbox.setSpacing( UiFactory.PAD );

		BorderPane pane = new BorderPane( grid, hbox, null, steps, null );
		pane.setBorder( new Border( new BorderStroke( Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths( UiFactory.PAD ) ) ) );

		getChildren().addAll( pane );

		addEventFilter( ScrollEvent.SCROLL, e -> {
			if( e.getDeltaY() == 0 ) return;
			Fx.run( () -> {
				int increment = 0;
				if( e.getDeltaY() < 0 ) increment = -1;
				if( e.getDeltaY() > 0 ) increment = 1;
				setZoom( getZoom() + increment );
			} );
		} );

		modelChangeHandler = e -> refresh();
	}

	public int getZoom() {
		return zoom.get();
	}

	public void setZoom( int zoom ) {
		if( zoom < 1 ) zoom = 1;
		this.zoom.set( zoom );
		refresh();
	}

	public IntegerProperty zoomProperty() {
		return zoom;
	}

	public MazeSolver getSolver() {
		return solver;
	}

	public void setSolver( MazeSolver solver ) {
		this.solver = solver;
		solver.setSpeed( solverSpeed.getValue() );
	}

	/**
	 * Called from the {@link com.avereon.xenon.asset.AssetManager AssetManager}
	 * when the asset is ready for use by the tool. It should not be assumed the
	 * asset is ready when the tool constructor is called. Logic requiring the
	 * asset to be ready should be called after this method has been called.
	 *
	 * @param request The OpenAssetRequest that contains information about how the
	 * asset was requested to be opened.
	 */
	@Override
	protected void ready( OpenAssetRequest request ) {
		setTitle( getAsset().getName() );
		setGraphic( getProgram().getIconLibrary().getIcon( "mazer" ) );

		getMaze().register( NodeEvent.NODE_CHANGED, modelChangeHandler );
	}

	@Override
	protected void open( OpenAssetRequest request ) {
		refresh();
	}

	/**
	 * Called when the tool is activated. It is common for the tool to register
	 * actions, menu bar items and tool bar items in this method. Any action, menu
	 * bar item or tool bar item should be removed in the {@link #conceal} method.
	 */
	@Override
	protected void activate() {
		pushAction( "reset", resetAction );
		pushAction( "runpause", runAction );
		pushTools( "reset runpause" );
		if( getAsset().isLoaded() ) runAction.setState( getSolver().isRunning() ? "pause" : "run" );
	}

	/**
	 * Called when the tool is concealed. It is common for the tool to unregister
	 * actions and menu bar items and tool bar items that were registered in the
	 * {@link #activate} method in this method.
	 */
	@Override
	protected void conceal() {
		pullTools();
		pullAction( "reset", resetAction );
		pullAction( "runpause", runAction );
	}

	@Override
	protected void deallocate() {
		getMaze().unregister( NodeEvent.NODE_CHANGED, modelChangeHandler );

		MazeSolver solver = getSolver();
		if( solver != null ) solver.stop();
	}

	private Maze getMaze() {
		return (Maze)getAsset().getModel();
	}

	/**
	 * Called any time the asset is refreshed. This is an indicator that the asset
	 * has changed in some way that the tool needs to be aware of. Note that the
	 * method is not provided any further information about the reason for the
	 * refresh.
	 */
	private void refresh() {
		Maze maze = getMaze();
		int width = maze.getWidth();
		int height = maze.getHeight();

		Fx.run( () -> {
			this.mazeWidth.setText( String.valueOf( width ) );
			this.mazeHeight.setText( String.valueOf( height ) );

			if( cells == null || cells.length != width || cells[ 0 ].length != height ) rebuildGrid();

			double zoomX = getZoom() / getScene().getWindow().getOutputScaleX();
			double zoomY = getZoom() / getScene().getWindow().getOutputScaleY();

			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					int config = maze.getCellConfig( x, y );
					Cell cell = cells[ x ][ y ];
					cell.setPrefSize( zoomX, zoomY );
					cell.setConfig( config == MazeConfig.COOKIE ? MazeConfig.STEP : config );
					cell.setVisits( maze.get( x, y ) );
				}
			}

			cells[ maze.getX() ][ maze.getY() ].setConfig( MazeConfig.COOKIE );

			steps.setText( Rb.text( BundleKey.PROMPT, "steps" ) + maze.getStepCount() );
		} );
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
	public void pause() {
		MazeSolver solver = getSolver();
		if( solver != null ) solver.stop();
	}

	@Override
	public void reset() {
		pause();
		getMaze().reset();
	}

	@Override
	public void run() {
		switch( chooser.getSelectionModel().getSelectedIndex() ) {
			case 1 -> solver = new SandbarSolver( getProgram(), getProduct(), MazeTool.this );
			case 2 -> solver = new WanderingSolver( getProgram(), getProduct(), MazeTool.this );
			default -> solver = new StackSolver( getProgram(), getProduct(), MazeTool.this );
		}
		setSolver( solver.setMaze( getMaze() ) );

		getProgram().getTaskManager().submit( solver );
	}

	//	private class ResetAction extends Action {
	//
	//		protected ResetAction( Program program ) {
	//			super( program );
	//		}
	//
	//		@Override
	//		public boolean isEnabled() {
	//			return true;
	//		}
	//
	//		@Override
	//		public void handle( ActionEvent actionEvent ) {
	//			MazeSolver solver = getSolver();
	//			if( solver != null ) solver.stop();
	//			getMaze().reset();
	//		}
	//
	//	}
	//
	//	private class RunPauseAction extends Action {
	//
	//		protected RunPauseAction( Program program ) {
	//			super( program );
	//		}
	//
	//		@Override
	//		public boolean isEnabled() {
	//			return true;
	//		}
	//
	//		@Override
	//		public void handle( ActionEvent event ) {
	//			if( getMaze().isGridClear() ) getMaze().reset();
	//
	//			MazeSolver solver = getSolver();
	//			if( solver != null && solver.isRunning() ) {
	//				solver.stop();
	//			} else {
	//				switch( chooser.getSelectionModel().getSelectedIndex() ) {
	//					case 1: {
	//						solver = new SandbarSolver( getProgram(), getProduct(), MazeTool.this );
	//						break;
	//					}
	//					case 2: {
	//						solver = new WanderingSolver( getProgram(), getProduct(), MazeTool.this );
	//						break;
	//					}
	//					default: {
	//						solver = new StackSolver( getProgram(), getProduct(), MazeTool.this );
	//						break;
	//					}
	//				}
	//				setSolver( solver.setMaze( getMaze() ) );
	//
	//				getProgram().getTaskManager().submit( solver );
	//			}
	//		}
	//
	//	}

	private static Background createBackground( String color ) {
		return createBackground( Color.web( color ) );
	}

	private static Background createBackground( Color color ) {
		return new Background( new BackgroundFill( color, CornerRadii.EMPTY, Insets.EMPTY ) );
	}

	private static class Cell extends Region {

		private final int x;

		private final int y;

		private int visits;

		private final Background visited = createBackground( "#80000040" );

		public Cell( Maze maze, int x, int y ) {
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

			setOnMouseDragged( ( e ) -> {
				Cell cell = (Cell)FxUtil.pick( getParent(), e.getSceneX(), e.getSceneY() );
				if( cell != null && e.isPrimaryButtonDown() ) {
					int newState = MazeConfig.HOLE;
					if( e.isControlDown() ) newState = MazeConfig.STEP;
					maze.setCellConfig( cell.x, cell.y, newState );
				}
			} );
		}

		public void setConfig( int config ) {
			setBackground( backgrounds.get( config ) );
			if( visits > 0 && config == MazeConfig.STEP || config == MazeConfig.MONSTER ) {
				double alpha = Math.min( 1.0, visits * 0.1 );
				setBackground( createBackground( Color.color( 0, 0.5, 0.5, alpha ) ) );
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
