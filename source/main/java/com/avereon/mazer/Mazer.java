package com.avereon.mazer;

import com.avereon.xenon.Mod;

public class Mazer extends Mod {

	@Override
	public void register() {
		super.register();
		getProgram().getIconLibrary().register( "mazer", MazerIcon.class );
	}

	@Override
	public void startup() {
		super.startup();
	}

	@Override
	public void shutdown() {
		super.shutdown();
	}

	@Override
	public void unregister() {
		super.unregister();
	}
}
