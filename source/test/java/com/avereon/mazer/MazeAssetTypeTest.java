package com.avereon.mazer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MazeAssetTypeTest extends BaseMazerTest {

	@Test
	void testConstructor() {
		MazeAssetType type = new MazeAssetType( mazer );
		assertThat( type.getKey() ).isEqualTo( "application/vnd.avereon.mazer.maze" );
		assertThat( type.getName() ).isEqualTo( "Mazer Maze" );
	}

}
