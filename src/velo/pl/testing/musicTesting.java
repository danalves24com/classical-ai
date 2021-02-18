package velo.pl.testing;

import static org.junit.Assert.*;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Chord;
import org.jfugue.theory.ChordProgression;
import org.jfugue.theory.Note;
import org.junit.Test;
import org.staccato.ReplacementMapPreprocessor;
import org.staccato.maps.SolfegeReplacementMap;

public class musicTesting {

	@Test
	public void test() {

	    Player player = new Player();
	    player.play("V0 I[Piano] Eq Ch. | Eq Ch. | Dq Eq Dq Cq   V1 I[Flute] Rw | Rw | GmajQQQ CmajQ");
	  }
	

}
