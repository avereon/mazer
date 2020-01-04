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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class MazeCodecTest extends BaseMazerTest {

	private MazeAssetType assetType;

	private Maze maze;

	private Asset asset;

	@BeforeEach
	void setup() {
		Path path = Paths.get( "target", "maze.txt" );
		assetType = new MazeAssetType( mazer );
		asset = new Asset( path.toUri(), assetType );
		maze = new Maze();
	}

	@Test
	void testSave() throws Exception {
		maze.setSize( 3, 2 );
		maze.setCellState( 1, 1, -1 );
		asset.setModel( maze );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assetType.getDefaultCodec().save( asset, output );

		String content = new String( output.toByteArray(), StandardCharsets.UTF_8 );

		BufferedReader reader = new BufferedReader( new StringReader( content ) );
		assertThat( reader.readLine(), is( "3 2" ) );
		assertThat( reader.readLine(), is( "0 0 0" ) );
		assertThat( reader.readLine(), is( "0 1 0" ) );
		assertThat( reader.readLine(), is( "1 0 0" ) );
		assertThat( reader.readLine(), is( "1 1 -1" ) );
		assertThat( reader.readLine(), is( "2 0 0" ) );
		assertThat( reader.readLine(), is( "2 1 0" ) );

		assertThat( output.toByteArray().length, is( 41 ) );
	}

	@Test
	void testLoad() throws Exception {
		asset.setModel( maze );

		StringBuilder builder = new StringBuilder( "3 2\n" );
		builder.append( "0 0 0\n" );
		builder.append( "0 1 0\n" );
		builder.append( "1 0 0\n" );
		builder.append( "1 1 -1\n" );
		builder.append( "2 0 0\n" );
		builder.append( "2 1 0\n" );

		String content = builder.toString();

		InputStream input = new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
		assetType.getDefaultCodec().load( asset, input );
		maze = asset.getModel();

		assertThat( maze.getWidth(), is( 3 ) );
		assertThat( maze.getHeight(), is( 2 ) );
		assertThat( maze.getCellState( 0, 0 ), is( 0 ) );
		assertThat( maze.getCellState( 0, 1 ), is( 0 ) );
		assertThat( maze.getCellState( 1, 0 ), is( 0 ) );
		assertThat( maze.getCellState( 1, 1 ), is( -1 ) );
		assertThat( maze.getCellState( 2, 0 ), is( 0 ) );
		assertThat( maze.getCellState( 2, 1 ), is( 0 ) );
	}

	@Test
	void testLoadWithError() {
		asset.setModel( maze );

		StringBuilder builder = new StringBuilder( "3 2\n" );
		builder.append( "0 0 0\n" );
		builder.append( "0 1 0\n" );
		builder.append( "1 0 0\n" );

		String content = builder.toString();

		InputStream input = new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
		try {
			assetType.getDefaultCodec().load( asset, input );
			maze = asset.getModel();
			fail( "Load should throw IOException but did not" );
		} catch( IOException exception ) {
			Throwable cause = exception.getCause();
			assertTrue( cause instanceof NullPointerException );
		}
	}

}
