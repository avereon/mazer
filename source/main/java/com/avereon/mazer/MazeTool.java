package com.avereon.mazer;

import com.avereon.util.LogUtil;
import com.avereon.xenon.Action;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.OpenAssetRequest;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.tool.ProgramTool;
import com.avereon.xenon.workpane.ToolException;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

	private GridPane grid;

	private Cell[][] cells;

	private int zoom = DEFAULT_ZOOM;

	static {
		backgrounds = new HashMap<>();
		backgrounds.put( Maze.COOKIE, createBackground( "#80600080" ) );
		backgrounds.put( Maze.DEFAULT, createBackground( "#80808080" ) );
		backgrounds.put( Maze.MONSTER, createBackground( "#800000C0" ) );
		backgrounds.put( Maze.HOLE, createBackground( "#00000000" ) );
	}

	public MazeTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		setGraphic( product.getProgram().getIconLibrary().getIcon( "mazer" ) );
		mazePropertiesAction = new MazePropertiesAction( product.getProgram() );

		grid = new GridPane();
		grid.setAlignment( Pos.CENTER );
		getChildren().addAll( grid );
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
				cells[ x ][ y ].setState( maze.getCellState( x, y ) ).setSize( zoom );
			}
		}

		cells[ maze.getX() ][ maze.getY() ].setState( Maze.COOKIE );
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
	}

	@Override
	protected void conceal() throws ToolException {
		pullAction( "properties", mazePropertiesAction );
	}

	private Maze getMaze() {
		return (Maze)getAsset().getModel();
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

	private static Background createBackground( String color ) {
		return new Background( new BackgroundFill( Color.web( color ), CornerRadii.EMPTY, Insets.EMPTY ) );
	}

	private static class Cell extends Region {

		private Maze maze;

		private int x;

		private int y;

		private int size;

		private int state;

		public Cell( Maze maze, int x, int y ) {
			this.maze = maze;
			this.x = x;
			this.y = y;
			setBackground( backgrounds.get( Maze.DEFAULT ) );

			setOnMousePressed( e -> {

				if( e.isPrimaryButtonDown() ) {
					int newState;
					if( e.isShiftDown() ) {
						newState = getState() == Maze.MONSTER ? Maze.DEFAULT : Maze.MONSTER;
					} else if( e.isControlDown() ) {
						newState = Maze.DEFAULT;
					} else {
						newState = Maze.HOLE;
					}
					maze.setCellState( x, y, newState );
				}
				if( e.isSecondaryButtonDown() ) {
					maze.setCookie( x, y );
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

		public int getState() {
			return this.state;
		}

		public Cell setState( int state ) {
			this.state = state;
			if( state > Maze.DEFAULT ) state = Maze.DEFAULT;
			setBackground( backgrounds.get( state ) );
			return this;
		}

	}

}
