package com.avereon.mazer;

import com.avereon.product.ProductCard;
import com.avereon.util.DateUtil;
import com.avereon.util.Version;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ModTest extends BaseMazerTest {

	private static SimpleDateFormat timestampFormat = new SimpleDateFormat( DateUtil.DEFAULT_DATE_FORMAT );

	@Test
	void testProductCard() throws Exception {
		ProductCard card = mazer.getCard();
		assertThat( card.getGroup(), is( "com.avereon" ) );
		assertThat( card.getArtifact(), is( "mazer" ) );
		assertThat( card.getVersion(), is( new Version( card.getVersion() ).toString() ) );
		assertThat( card.getTimestamp(), is( timestampFormat.format( timestampFormat.parse( card.getTimestamp() ) ) ) );

		assertThat( card.getPackaging(), is( "mod" ) );
		assertThat( card.getIcons(), CoreMatchers.hasItems( "mazer", "https://avereon.com/download/latest/mazer/product/icon" ) );
		assertThat( card.getName(), is( "Mazer" ) );

		assertThat( card.getProvider(), is( "Avereon" ) );
		//assertThat( card.getProviderUrl(), is( "https://www.avereon.com"));
		assertThat( card.getInception(), is( 2019 ) );
	}

}
