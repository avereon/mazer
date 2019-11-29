module com.avereon.mazer {
	requires com.avereon.xenon;
	requires com.avereon.zevra;

	exports com.avereon.mazer to com.avereon.xenon;

	provides com.avereon.xenon.Mod with com.avereon.mazer.Mazer;
}
