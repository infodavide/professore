package org.infodavid.professore.desktop;

import java.nio.file.Paths;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;

import org.infodavid.professore.core.midi.MidiPlayer;
import org.infodavid.professore.core.midi.ReceiverBridge;
import org.infodavid.professore.core.midi.SoundControllerAdapter;

public class Test {

    public static void main(final String[] args) {
        try (MidiPlayer player = new MidiPlayer(new SoundControllerAdapter())) {
            final MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
            MidiDevice device = null;
            for (final Info info : infos) {
                System.out.println(info.getName() + ", v= " + info.getVendor() +", desc= " + info.getDescription());

                if (info.getName().contains("[hw:1,0,0]")) {
                    device = MidiSystem.getMidiDevice(info);
                }
            }

            if (device != null) {
                device.open();

                device.getTransmitter().setReceiver(new ReceiverBridge(new SoundControllerAdapter(), null));
                Thread.sleep(10000);
            }

            player.play(Paths.get("/home/users/david/Documenti/MuseScore3/Spartiti/The_castle_in_the_sky.mid"));
            player.join();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
