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

public class MazeTool extends ProgramTool {

	private static final Logger log = LogUtil.get( MethodHandles.lookup().lookupClass() );

	private static final int DEFAULT_ZOOM = 20;

	private MazePropertiesAction mazePropertiesAction;

	private GridPane grid;

	private Space[][] state;

	private int zoom = DEFAULT_ZOOM;

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

		if( state == null || state.length != width || state[ 0 ].length != height ) rebuildGrid();

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				state[ x ][ y ].setState( maze.getCellState( x, y ) ).setSize( zoom );
			}
		}
	}

	private void rebuildGrid() {
		grid.getChildren().clear();

		Maze maze = getMaze();
		int width = maze.getWidth();
		int height = maze.getHeight();
		state = new Space[ width ][ height ];
		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				grid.add( state[ x ][ y ] = new Space( maze, x, y ), y, x );
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

	private static class Space extends Region {

		private static final Background DEFAULT = new Background( new BackgroundFill( Color.web( "#80808060" ), CornerRadii.EMPTY, Insets.EMPTY ) );

		private static final Background HOLE = new Background( new BackgroundFill( Color.web( "#00000000" ), CornerRadii.EMPTY, Insets.EMPTY ) );

		private Maze maze;

		private int x;

		private int y;

		private int size;

		private int state;

		public Space( Maze maze, int x, int y ) {
			this.maze = maze;
			this.x = x;
			this.y = y;
			setBackground( HOLE );

			setOnMousePressed( e -> {
				int newState = state;
				switch( state ) {
					case -1: {
						newState = 0;
						break;
					}
					case 0: {
						newState = -1;
						break;
					}
				}
				maze.setCellState( x, y, newState );
			} );
		}

		public int getSize() {
			return size;
		}

		public Space setSize( int size ) {
			setPrefSize( size, size );
			this.size = size;
			return this;
		}

		public int getState() {
			return this.state;
		}

		public Space setState( int state ) {
			this.state = state;

			switch( state ) {
				case 0: {
					setBackground( DEFAULT );
					break;
				}
				case -1: {
					setBackground( HOLE );
					break;
				}
			}
			return this;
		}

	}

}
