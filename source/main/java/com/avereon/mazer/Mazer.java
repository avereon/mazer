package com.avereon.mazer;

import com.avereon.xenon.Mod;

public class Mazer extends Mod {

	@Override
	public void register() {
		getProgram().getIconLibrary().register( "mazer", MazerIcon.class );
	}

	@Override
	public void startup() {}

	@Override
	public void shutdown() {}

	@Override
	public void unregister() {
		getProgram().getIconLibrary().unregister( "mazer", MazerIcon.class );
	}

}
