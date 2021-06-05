package com.avereon.mazer;

import com.avereon.xenon.asset.Asset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.fail;

public class MazeCodecTest extends BaseMazerTest {

	private MazeAssetType assetType;

	private Maze maze;

	private Asset asset;

	@BeforeEach
	void setup() {
		Path path = Paths.get( "target", "maze.txt" );
		assetType = new MazeAssetType( mazer );
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
		assertThat( reader.readLine(), is( "S 3,3" ) );
		assertThat( reader.readLine(), is( "H 1,1" ) );
		assertThat( reader.readLine(), is( "M 2,1" ) );
		assertThat( reader.readLine(), is( "C 0,2" ) );
		assertThat( reader.readLine(), is( "D NORTH" ) );
		assertThat( reader.readLine(), is( nullValue() ) );
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
		assertThat( maze.getWidth(), is( 3 ) );
		assertThat( maze.getHeight(), is( 3 ) );
		assertThat( maze.getCellConfig( 0, 0 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 0, 1 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 0, 2 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 1, 0 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 1, 1 ), is( MazeConfig.HOLE ) );
		assertThat( maze.getCellConfig( 1, 2 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 2, 0 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 2, 1 ), is( MazeConfig.STEP ) );
		assertThat( maze.getCellConfig( 2, 2 ), is( MazeConfig.COOKIE ) );

		// Check the initial runtime information
		// There should be one visit where the cookie is
		assertThat( maze.getX(), is( 2 ) );
		assertThat( maze.getY(), is( 2 ) );
		assertThat( maze.get( 2, 2 ), is( 1 ) );
		// There should be no visits where there is a hole
		assertThat( maze.get( 1, 1 ), is( -1 ) );
		assertThat( maze.getDirection(), is( Direction.SOUTH ) );
	}

	@Test
	void testLoadWithError() {
		asset.setModel( maze );

		StringBuilder builder = new StringBuilder( "S 3 3\n" );
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
			assertThat( cause.getClass(), is( NumberFormatException.class ) );
		}
	}

}
