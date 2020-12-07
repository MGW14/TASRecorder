# TASRecorder
The TASRecorder Mod is an addition to the TASTickratechanger by <a href="https://github.com/ScribbleLP/TASTickratechanger">@ScribbleLP</a>.
It records at 60 fps depending on your Tickrate and then speeds it up without the use of an Video Editing software.

I will have to add Sound later

<h3>This requires FFMpeg</h3>
You have to put <a href="https://github.com/BtbN/FFmpeg-Builds/releases">ffmpeg gpl</a> to your Documents Folder
<img src="https://i.ibb.co/QcCzywx/image.png"></img>
<h3>How to install</h3>
Download Gradle or the Gradle Wrapper and run the task "shadow"
<h3>How to use</h3>
Once you are in your world, set the default tickrate and then set the quality of your TAS-Video by using /quality (low-ultra)
Quality Settings basically just set the bitrate of ffmpeg. I recommend Medium for Lower-End PC's and Low for short/8mb tas files.

Then type in /record to start the recording.
It will not record the Main Menu, but will automatically start after you join a world again.

While you are in the TAS, press ESC a few times, and all the images will be compressed.
That means, you will not have 1.5tb of images after an all advancements run (it's actually "only" 350 gb but still) and it will compress it down to... uhhh 15 gb? idunno.

Type /record again to stop recording.

Now you just need to run /generate and your Tas will appear under Documents/ffmpeg.
