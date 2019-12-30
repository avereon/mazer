package com.avereon.mazer;

import com.avereon.xenon.Action;
import com.avereon.xenon.OpenToolRequestParameters;
import com.avereon.xenon.Program;
import com.avereon.xenon.ProgramProduct;
import com.avereon.xenon.asset.Asset;
import com.avereon.xenon.asset.AssetEvent;
import com.avereon.xenon.notice.Notice;
import com.avereon.xenon.tool.ProgramTool;
import com.avereon.xenon.workpane.ToolException;
import javafx.event.ActionEvent;

public class MazeTool extends ProgramTool {

	private MazePropertiesAction mazePropertiesAction;

	public MazeTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		setGraphic( product.getProgram().getIconLibrary().getIcon( "mazer" ) );
		mazePropertiesAction = new MazePropertiesAction( product.getProgram() );

		asset.getEventBus().register( AssetEvent.ACTIVATED, e -> {
			getProgram().getActionLibrary().getAction( "properties" ).pushAction( mazePropertiesAction );
		} );
		asset.getEventBus().register( AssetEvent.DEACTIVATED, e -> {
			getProgram().getActionLibrary().getAction( "properties" ).pullAction( mazePropertiesAction );
		} );
	}

	@Override
	protected void assetReady( OpenToolRequestParameters parameters ) throws ToolException {
		super.assetReady( parameters );
		setTitle( getAsset().getName() );

		// TODO If there are no existing properties, open the properties tool
	}

	@Override
	protected void activate() {
		getProgram().getActionLibrary().getAction( "properties" ).pushAction( mazePropertiesAction );
	}

	@Override
	protected void conceal() {
		getProgram().getActionLibrary().getAction( "properties" ).pullAction( mazePropertiesAction );
	}

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

}
