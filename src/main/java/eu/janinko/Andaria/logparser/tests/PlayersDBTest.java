package eu.janinko.Andaria.logparser.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import eu.janinko.Andaria.logparser.model.Player;
import eu.janinko.Andaria.logparser.PlayersDB;

public class PlayersDBTest {

	@Test
	public void testPlayerCreation() {
		PlayersDB pdb = new PlayersDB();

		Player aaa = pdb.getPlayer("AAA", 1, "Aacc");
		assertNotNull(aaa);
		assertEquals(aaa.getName(),"AAA");
		assertEquals(aaa.getUid(),1);
		assertEquals(aaa.getAcc().getName(),"Aacc");
		
		Player bbb = pdb.getPlayer("BBB", null, null);
		assertNotNull(bbb);
		assertEquals(bbb.getName(),"BBB");
		assertEquals(bbb.getUid(),-1);
		assertNull(bbb.getAcc());
		
		Player ccc = pdb.getPlayer("CCC", 2, null);
		assertNotNull(ccc);
		assertEquals(ccc.getName(),"CCC");
		assertEquals(ccc.getUid(),2);
		assertNull(ccc.getAcc());
		
		Player ddd = pdb.getPlayer("DDD", null, "Dacc");
		assertNotNull(ddd);
		assertEquals(ddd.getName(),"DDD");
		assertEquals(ddd.getUid(),-1);
		assertEquals(ddd.getAcc().getName(),"Dacc");
		

		Player eee = pdb.getPlayer(null, 4, null);
		assertNotNull(eee);
		assertNull(eee.getName());
		assertEquals(eee.getUid(),4);
		assertNull(eee.getAcc());
	}
	
	@Test
	public void testPlayerLocating() {
		PlayersDB pdb = new PlayersDB();

		Player aaa = pdb.getPlayer("AAA", 1, "Aacc");
		Player bbb = pdb.getPlayer("BBB", 2, "Bacc");

		assertEquals(aaa, pdb.getPlayer(null, 1, null));
		assertEquals(aaa, pdb.getPlayer("AAA", 1, null));
		assertEquals(aaa, pdb.getPlayer("AAA", null, null));

		assertEquals(bbb, pdb.getPlayer(null, 2, "Bacc"));
		assertEquals(bbb, pdb.getPlayer("BBB", 2, "Bacc"));
		assertEquals(bbb, pdb.getPlayer("BBB", null, "Bacc"));
		

		Player ccc = pdb.getPlayer("CCC", null, null);
		Player ddd = pdb.getPlayer("DDD", null, "Dacc");
		Player eee = pdb.getPlayer("EEE", 3, null);

		assertEquals(ccc, pdb.getPlayer("CCC", null, null));

		assertEquals(ddd, pdb.getPlayer("DDD", null, null));
		assertEquals(ddd, pdb.getPlayer("DDD", null, "Dacc"));

		assertEquals(eee, pdb.getPlayer(null, 3, null));
		assertEquals(eee, pdb.getPlayer("EEE", 3, null));
		assertEquals(eee, pdb.getPlayer("EEE", null, null));
	}

	@Test
	public void testUpdatingInfo() {
		PlayersDB pdb = new PlayersDB();

		Player a = pdb.getPlayer("AAA", null, null);
		Player aa = pdb.getPlayer("AAA", 1, null);
		Player aaa = pdb.getPlayer("AAA", 1, "Aacc");

		assertEquals(a, aa);
		assertEquals(a, aaa);
		assertEquals(aaa.getName(),"AAA");
		assertEquals(aaa.getUid(),1);
		assertEquals(aaa.getAcc().getName(),"Aacc");
		
		Player b = pdb.getPlayer(null, 2, null);
		Player bb = pdb.getPlayer("BBB", 2, null);
		Player bbb = pdb.getPlayer("BBB", 2, "Bacc");
		
		assertEquals(b, bb);
		assertEquals(b, bbb);
		assertEquals(bbb.getName(),"BBB");
		assertEquals(bbb.getUid(),2);
		assertEquals(bbb.getAcc().getName(),"Bacc");
	}
	

	@Test
	public void testAccWithMultiplePlayes() {
		PlayersDB pdb = new PlayersDB();

		Player aaa = pdb.getPlayer("AAA", 1, "Aacc");
		Player abb = pdb.getPlayer("ABB", 2, "Aacc");
		Player acc = pdb.getPlayer("ACC", 3, "Aacc");

		assertNotSame(aaa, abb);
		assertNotSame(aaa, acc);

		assertEquals(aaa, pdb.getPlayer("AAA", null, null));
		assertEquals(abb, pdb.getPlayer("ABB", null, null));
		assertEquals(acc, pdb.getPlayer("ACC", null, null));
		

		assertEquals(aaa, pdb.getPlayer(null, 1, null));
		assertEquals(abb, pdb.getPlayer(null, 2, null));
		assertEquals(acc, pdb.getPlayer(null, 3, null));
	}

	@Test
	public void testSameNamedPlayer() {
		PlayersDB pdb = new PlayersDB();

		Player aaa = pdb.getPlayer("AAA", 1, "Aacc");
		Player aab = pdb.getPlayer("AAA", 2, "Bacc");
		Player aac = pdb.getPlayer("AAA", 3, "Cacc");
		Player aa  = pdb.getPlayer("AAA", null, null);
		
		assertNotSame(aaa, aab);
		assertNotSame(aaa, aac);
		assertNotSame(aaa, aa);
		assertNotSame(aab, aa);
		assertNotSame(aac, aa);
	}
}
