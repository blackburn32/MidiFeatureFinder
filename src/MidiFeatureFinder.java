import org.jfugue.midi.MidiParser;
import org.jfugue.parser.ParserListenerAdapter;
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
        File midiFile = new File("coriolan.mid");
        MidiParser parser = new MidiParser();
        NoteCountParserListener listener = new NoteCountParserListener();
        parser.addParserListener(listener);

        try {
            parser.parse(MidiSystem.getSequence(midiFile));
            System.out.println(listener.a + "  " + listener.b + " " + listener.bflat);
        } catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}

/**
 * I don't know if this excludes percussion hits, but we probably should
 */
class NoteCountParserListener extends ParserListenerAdapter {
    public int a=0, aflat=0, b=0, bflat=0, c=0, d=0, dflat=0, e=0, eflat=0, f=0, g=0, gflat=0;

    @Override
    public void onNoteParsed(Note note) {
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

    }

}
