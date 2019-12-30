package com.avereon.mazer;

import com.avereon.event.EventHandler;
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

	private EventHandler<AssetEvent> assetActivatedHandler;

	private EventHandler<AssetEvent> assetDeactivatedHandler;

	public MazeTool( ProgramProduct product, Asset asset ) {
		super( product, asset );
		setGraphic( product.getProgram().getIconLibrary().getIcon( "mazer" ) );
		mazePropertiesAction = new MazePropertiesAction( product.getProgram() );

		assetActivatedHandler = e -> getProgram().getActionLibrary().getAction( "properties" ).pushAction( mazePropertiesAction );
		assetDeactivatedHandler = e-> getProgram().getActionLibrary().getAction( "properties" ).pullAction( mazePropertiesAction );
	}

	@Override
	protected void assetReady( OpenToolRequestParameters parameters ) throws ToolException {
		super.assetReady( parameters );
		setTitle( getAsset().getName() );

		// TODO If there are no existing properties, open the properties tool
	}

	@Override
	protected void allocate() throws ToolException {
		getAsset().getEventBus().register( AssetEvent.ACTIVATED, assetActivatedHandler );
		getAsset().getEventBus().register( AssetEvent.DEACTIVATED, assetDeactivatedHandler );
	}

	@Override
	protected void deallocate() throws ToolException {
		getAsset().getEventBus().unregister( AssetEvent.ACTIVATED, assetActivatedHandler );
		getAsset().getEventBus().unregister( AssetEvent.DEACTIVATED, assetDeactivatedHandler );
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
