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
		asset = new Asset( path.toUri(), assetType );
		maze = new Maze();
	}

	@Test
	void testSave() throws Exception {
		maze.setSize( 3, 3 );
		maze.setCellState( 1, 1, Maze.HOLE );
		asset.setModel( maze );
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assetType.getDefaultCodec().save( asset, output );

		String content = new String( output.toByteArray(), StandardCharsets.UTF_8 );

		BufferedReader reader = new BufferedReader( new StringReader( content ) );
		assertThat( reader.readLine(), is( "S 3,3" ) );
		assertThat( reader.readLine(), is( "H 1,1" ) );
		assertThat( reader.readLine(), is( "C 1,1" ) );
		assertThat( reader.readLine(), is( "D EAST" ) );
		assertThat( reader.readLine(), is( nullValue() ) );

		assertThat( output.toByteArray().length, is( 25 ) );
	}

	@Test
	void testLoad() throws Exception {
		asset.setModel( maze );

		StringBuilder builder = new StringBuilder( "S 3,3\n" );
		builder.append( "H 2,2\n" );
		builder.append( "C 0,0\n" );
		builder.append( "D SOUTH\n" );

		String content = builder.toString();

		InputStream input = new ByteArrayInputStream( content.getBytes( StandardCharsets.UTF_8 ) );
		assetType.getDefaultCodec().load( asset, input );
		maze = asset.getModel();

		assertThat( maze.getWidth(), is( 3 ) );
		assertThat( maze.getHeight(), is( 3 ) );
		assertThat( maze.getCellState( 0, 0 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 0, 1 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 0, 2 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 1, 0 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 1, 1 ), is( Maze.HOLE ) );
		assertThat( maze.getCellState( 1, 2 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 2, 0 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 2, 1 ), is( Maze.DEFAULT ) );
		assertThat( maze.getCellState( 2, 2 ), is( Maze.DEFAULT ) );
		assertThat( maze.getX(), is( 2 ) );
		assertThat( maze.getY(), is( 2 ) );
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
