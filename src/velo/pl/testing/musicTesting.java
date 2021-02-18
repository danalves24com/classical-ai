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
	    ChordProgression cp = new ChordProgression("I IV V");

	    Chord[] chords = cp.setKey("C").getChords();
	    for (Chord chord : chords) {
	      System.out.print("Chord "+chord+" has these notes: ");
	      Note[] notes = chord.getNotes();
	      for (Note note : notes) {
	        System.out.print(note+" ");
	      }
	      System.out.println();
	    }

	    Player player = new Player();
	    player.play(cp);
	  }
	

}
