package com.avereon.mazer;

import com.avereon.product.ProductCard;
import com.avereon.product.Version;
import com.avereon.util.DateUtil;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;

class ModTest extends BaseMazerTest {

	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat( DateUtil.DEFAULT_DATE_FORMAT );

	@Test
	void testProductCard() throws Exception {
		ProductCard card = mazer.getCard();
		assertThat( card.getGroup() ).isEqualTo( "com.avereon" );
		assertThat( card.getArtifact() ).isEqualTo( "mazer" );
		assertThat( card.getVersion() ).isEqualTo( new Version( card.getVersion() ).toString() );
		assertThat( card.getTimestamp() ).isEqualTo( timestampFormat.format( timestampFormat.parse( card.getTimestamp() ) ) );

		assertThat( card.getPackaging() ).isEqualTo( "mod" );
		assertThat( card.getIcons() ).contains( "mazer", "https://avereon.com/download/latest/mazer/product/icon" );
		assertThat( card.getName() ).isEqualTo( "Mazer" );

		assertThat( card.getProvider() ).isEqualTo( "Avereon" );
		//assertThat( card.getProviderUrl()).isEqualTo( "https://www.avereon.com"));
		assertThat( card.getInception() ).isEqualTo( 2019 );
	}

}
