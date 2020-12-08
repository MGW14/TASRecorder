package work.mgnet.tasrecorder;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.jcodec.api.SequenceEncoder;

public class ScreenshotQueue {
	
	public static Timer scheduler = new Timer();
	public static Thread workedThread;
	public static SequenceEncoder encoder;
	public static TimerTask workerTask;
	
	public static Queue<ByteBuffer> toConvert = new LinkedList<ByteBuffer>();
	public static int toRecord = 0;
	
}
