package work.mgnet.tasrecorder;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import org.jcodec.api.SequenceEncoder;

public class ScreenshotQueue {

	public static class WorkImage {
		public ByteBuffer buffer;
		public int frame;
		
		public WorkImage(ByteBuffer buffer, int frame) {
			this.buffer = buffer;
			this.frame = frame;
		}
		
	}
	
	public static Timer scheduler = new Timer();
	public static Thread workedThread;
	public static SequenceEncoder encoder;
	
	public static TimerTask workerTask;
	
	public static Queue<WorkImage> toConvert = new LinkedList<WorkImage>();
	public static Queue<Integer> toRecord = new LinkedList<Integer>();
	
}
