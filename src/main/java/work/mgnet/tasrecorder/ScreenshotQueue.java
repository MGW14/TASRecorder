package work.mgnet.tasrecorder;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import work.mgnet.tasrecorder.utils.ScreenshotUtils;

public class ScreenshotQueue {

	public static class WorkImage {
		public ByteBuffer buffer;
		public String name;
		
		public WorkImage(ByteBuffer buffer, String name) {
			this.buffer = buffer;
			this.name = name;
		}
		
	}
	
	public static Timer scheduler = new Timer();
	
	public static Thread workerThread;
	public static TimerTask workerTask = new TimerTask() {
		
		@Override
		public void run() {
	 		ScreenshotQueue.toRecord.add(ScreenshotUtils.getScreenshotName());
		}
	};
	
	public static Queue<WorkImage> toConvert = new LinkedList<WorkImage>();
	public static Queue<String> toRecord = new LinkedList<String>();
}
