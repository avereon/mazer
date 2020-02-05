package com.avereon.mazer;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MazeAssetTypeTest extends BaseMazerTest {

	@Test
	void testConstructor() {
		MazeAssetType type = new MazeAssetType( mazer );
		assertThat( type.getKey(), is( "application/vnd.avereon.mazer.maze" ) );
		assertThat( type.getName(), is( "Mazer Maze" ));
	}

}
