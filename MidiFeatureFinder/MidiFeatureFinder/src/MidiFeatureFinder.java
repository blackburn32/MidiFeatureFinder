import org.jfugue.midi.MidiParser;
import org.jfugue.parser.ParserListenerAdapter;
import org.jfugue.theory.Chord;
import org.jfugue.theory.Note;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import java.io.File;
import java.io.IOException;


/**
 * You need to have jfugue-5.0.7 linked in order for the ParserListenerAdapter to work this way
 *
 * Should work on any midi files.
 */
public class MidiFeatureFinder {

    public static void main(String[] args) {
        File midiFile = new File("NonClassicalExamples/Christmas_Carols_-_12_Days_Of_Christmas.mid");
        MidiParser parser = new MidiParser();
        NoteCountParserListener noteListener = new NoteCountParserListener();
        InstrumentParserListener instrumentListener = new InstrumentParserListener();
        parser.addParserListener(noteListener);
        parser.addParserListener(instrumentListener);

        try {
            parser.parse(MidiSystem.getSequence(midiFile));
            System.out.println(noteListener.buildFeatureString()+instrumentListener.buildFeatureString());
        } catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}

/**
 * Tracks stats obtained by observing note key, type and duration
 */
class NoteCountParserListener extends ParserListenerAdapter {
    public int a=0, aflat=0, b=0, bflat=0, c=0, d=0, dflat=0, e=0, eflat=0, f=0, g=0, gflat=0, count=0; 
    public int w=0, h=0, q=0, hdot=0, qdot=0;

    @Override
    public void onNoteParsed(Note note)
    {
        switch(note.getDispositionedToneStringWithoutOctave(-1, note.getValue())){
            case("A"):a++; break;
            case("Ab"):aflat++;break;
            case("B"):b++;break;
            case("Bb"):bflat++;break;
            case("C"):c++;break;
            case("D"):d++;break;
            case("Db"):dflat++;break;
            case("E"):e++;break;
            case("Eb"):eflat++;break;
            case("F"):f++;break;
            case("G"):g++;break;
            case("Gb"):gflat++;break;
        }
        //System.out.println(Note.getDurationString(note.getDuration()));
        
        switch (Note.getDurationString(note.getDuration())) {
        	case("w"):w++;break;
        	case("h"):h++;break;
        	case("q"):q++;break;
        	case("h."):hdot++;break;
        	case("q."):qdot++;break;
        }
        
        count++;
    }

    // Doesn't look like this one is getting called, maybe some midi files don't store chords
    @Override
    public void onChordParsed(Chord chord)
    {
        System.out.println(chord.getBassNote().getOctave());
    }

    /**
     *  Order of features from Note Count statistics
     *  1 - has more flats than naturals
     *  2 - contains every type of note
     *  3 - has one type of note that is played a majority of the song
     */
    public String buildFeatureString()
    {
        StringBuilder featureString = new StringBuilder();

        if(moreFlatsThanNaturals())
            featureString.append(1);
        else
            featureString.append(0);

        if(hasEveryNote())
            featureString.append(1);
        else
            featureString.append(0);

        if(hasMajorityNote())
            featureString.append(1);
        else
            featureString.append(0);
        
        if (moreHalvesThanQuarters())
        	featureString.append(1);
        else
        	featureString.append(0);
  
        majorityNoteDurationAndSupermajority(featureString);
        
        return featureString.toString();
    }

    private boolean moreFlatsThanNaturals()
    {
        return (a + b + c + d + e + f + g) < (aflat + bflat + dflat + eflat + gflat);
    }

    private boolean hasEveryNote()
    {
        return (a > 0 && aflat > 0 && b > 0 && bflat > 0 && c > 0 && d > 0 && dflat > 0 && dflat > 0 && e > 0 && eflat > 0 && f > 0 && g > 0 && gflat > 0);
    }

    private boolean hasMajorityNote()
    {
        int major = count/2;
        return (a>major||aflat>major||b>major||bflat>major||c>major||d>major||dflat>major||e>major||eflat>major||f>major||g>major||gflat>major);
    }
    
    private int majorityNoteDuration() {
    	return Math.max(w, Math.max(h, Math.max(q,Math.max(qdot, hdot))));
    }
    
    private void majorityNoteDurationAndSupermajority(StringBuilder featureString) {
        // majority features go: w h q hdot qdot
    	// supermajority features (half or more): w h q hdot qdot
        int majorityNoteDurationCount = majorityNoteDuration();
        int maj = count/2;
        if (w==majorityNoteDurationCount) { 
        	featureString.append(1); featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(0);
        	if (w>=maj) {
        		featureString.append(1); featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(0);
        	}
        }
        else if (h==majorityNoteDurationCount) { 
        	featureString.append(0); featureString.append(1); featureString.append(0); featureString.append(0); featureString.append(0);
    		if (h>=maj) {
    			featureString.append(0); featureString.append(1); featureString.append(0); featureString.append(0); featureString.append(0);
    		}
    	}
        else if (q==majorityNoteDurationCount) { 
        	featureString.append(0); featureString.append(0); featureString.append(1); featureString.append(0); featureString.append(0);
    		if (q>=maj) {
    			featureString.append(0); featureString.append(0); featureString.append(1); featureString.append(0); featureString.append(0);
    		}
    	}
        else if (hdot==majorityNoteDurationCount) { 
        	featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(1); featureString.append(0);
    		if (hdot>=maj) {
    			featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(1); featureString.append(0);
    		}
    	}
        else { featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(1);
    		if (qdot>=maj) {
    			featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(0); featureString.append(1);
    			}
    	}
    }
    
    private boolean moreHalvesThanQuarters() {
    	return (h>q);
    }
}

/**
 * A list of the instruments can be found in MidiDictionary.class
 */
class InstrumentParserListener extends ParserListenerAdapter {

    boolean[] hasInstrument = new boolean[128];

    @Override
    public void onInstrumentParsed(byte instrument){
        hasInstrument[Byte.toUnsignedInt(instrument)] = true;
    }

    public String buildFeatureString()
    {
        StringBuilder featureString = new StringBuilder();
        for(boolean b : hasInstrument)
        {
            if(b)
                featureString.append(1);
            else
                featureString.append(0);
        }
        return featureString.toString();
    }
}
