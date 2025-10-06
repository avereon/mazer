package com.avereon.mazer;

import com.avereon.xenon.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class MazeCodecTest extends BaseMazerTest {

	private MazeResourceType assetType;

	private Maze maze;

	private Asset asset;

	@BeforeEach
	void setup() {
		Path path = Paths.get( "target", "maze.txt" );
		assetType = new MazeResourceType( mazer );
		asset = new Asset( assetType, path.toUri() );
		maze = new Maze();
	}

	@Test
	void testSave() throws Exception {
		maze.setSize( 3, 3 );
		maze.setCellConfig( 1, 1, MazeConfig.HOLE );
		maze.setCellConfig( 2, 1, MazeConfig.MONSTER );
		maze.setCookieStart( 0, 2 );
		maze.setStartDirection( Direction.NORTH );
		asset.setModel( maze );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assetType.getDefaultCodec().save( asset, output );

		String content = new String( output.toByteArray(), StandardCharsets.UTF_8 );

		BufferedReader reader = new BufferedReader( new StringReader( content ) );
		assertThat( reader.readLine() ).isEqualTo( "S 3,3" );
		assertThat( reader.readLine() ).isEqualTo( "H 1,1" );
		assertThat( reader.readLine() ).isEqualTo( "M 2,1" );
		assertThat( reader.readLine() ).isEqualTo( "C 0,2" );
		assertThat( reader.readLine() ).isEqualTo( "D NORTH" );
		assertThat( reader.readLine() ).isNull();
	}

	@Test
	void testLoad() throws Exception {
		asset.setModel( maze );

		StringBuilder builder = new StringBuilder( "S 3,3\n" );
		builder.append( "H 1,1\n" );
		builder.append( "C 2,2\n" );
		builder.append( "D SOUTH\n" );

		String content = builder.toString();

		InputStream input = new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
		assetType.getDefaultCodec().load( asset, input );
		maze = asset.getModel();

		// Check the maze configuration
		assertThat( maze.getWidth() ).isEqualTo( 3 );
		assertThat( maze.getHeight() ).isEqualTo( 3 );
		assertThat( maze.getCellConfig( 0, 0 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 0, 1 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 0, 2 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 1, 0 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 1, 1 ) ).isEqualTo( MazeConfig.HOLE );
		assertThat( maze.getCellConfig( 1, 2 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 2, 0 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 2, 1 ) ).isEqualTo( MazeConfig.STEP );
		assertThat( maze.getCellConfig( 2, 2 ) ).isEqualTo( MazeConfig.COOKIE );

		// Check the initial runtime information
		// There should be one visit where the cookie is
		assertThat( maze.getX() ).isEqualTo( 2 );
		assertThat( maze.getY() ).isEqualTo( 2 );
		assertThat( maze.get( 2, 2 ) ).isEqualTo( 1 );
		// There should be no visits where there is a hole
		assertThat( maze.get( 1, 1 ) ).isEqualTo( -1 );
		assertThat( maze.getDirection() ).isEqualTo( Direction.SOUTH );
	}

	@Test
	@SuppressWarnings( "ResultOfMethodCallIgnored" )
	void testLoadWithError() {
		asset.setModel( maze );

		@SuppressWarnings( "StringBufferReplaceableByString" ) StringBuilder builder = new StringBuilder( "S 3 3\n" );
		builder.append( "H 1 1\n" );
		builder.append( "Z\n" );

		String content = builder.toString();

		InputStream input = new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
		try {
			assetType.getDefaultCodec().load( asset, input );
			maze = asset.getModel();
			fail( "Load should throw IOException but did not" );
		} catch( IOException exception ) {
			Throwable cause = exception.getCause();
			assertThat( cause.getClass() ).isEqualTo( NumberFormatException.class );
		}
	}

}
