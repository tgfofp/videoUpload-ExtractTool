package com.extractor;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;

/**
 * Using {@link IMediaReader}, takes a media container, finds the first video
 * stream, decodes that stream, and then writes video frames out to a PNG image
 * file every 10 seconds, based on the video presentation timestamps.
 */

public class DecodeAndCaptureFrames {

	// The number of seconds between frames
	public static final double SECONDS_BETWEEN_FRAMES = 1;

	// The number of micro-seconds between frames
	public static final long MICRO_SECONDS_BETWEEN_FRAMES = (long) (Global.DEFAULT_PTS_PER_SECOND
			* SECONDS_BETWEEN_FRAMES);

	// Time of last frame write
	private static long mLastPtsWrite = Global.NO_PTS;

	/*
	 * The video stream index, used to ensure we display frames from one and
	 * only one video stream from the media container.
	 */
	private static int mVideoStreamIndex = -1;

	private static String filePrefix;

	private static List<String> images;

	public List<String> getImages() {
		return images;
	}

	/**
	 * Construct a DecodeAndCaptureFrames which reads and captures frames from a
	 * video file.
	 * 
	 * @param uploadDir
	 *            the name of the media file to read
	 */

	public DecodeAndCaptureFrames(String filePath, String uploadDir) {

		filePrefix = uploadDir + File.separator + "image";

		// create a media reader for processing video
		IMediaReader mediaReader = ToolFactory.makeReader(filePath);

		mediaReader.setCloseOnEofOnly(true);

		// stipulate that we want BufferedImages created in BGR 24bit color
		// space
		mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

		// note that DecodeAndCaptureFrames is derived from
		// MediaReader.ListenerAdapter and thus may be added as a listener
		// to the MediaReader. DecodeAndCaptureFrames implements
		// onVideoPicture().
		mediaReader.addListener(new ImageSnapListener());

		// read out the contents of the media file, note that nothing else
		// happens here. action happens in the onVideoPicture() method
		// which is called when complete video pictures are extracted from
		// the media source
		while (mediaReader.readPacket() == null)
			;
	}

	private class ImageSnapListener extends MediaListenerAdapter {

		public void onVideoPicture(IVideoPictureEvent event) {

			if (event.getStreamIndex() != mVideoStreamIndex) {
				// if the selected video stream id is not yet set, go ahead an
				// select this lucky video stream
				if (mVideoStreamIndex == -1) {
					mVideoStreamIndex = event.getStreamIndex();
					images = new ArrayList<>();
					// no need to show frames from this video stream
				} else
					return;
			}

			// if uninitialized, back date mLastPtsWrite to get the very first
			// frame
			if (mLastPtsWrite == Global.NO_PTS)
				mLastPtsWrite = event.getTimeStamp() - MICRO_SECONDS_BETWEEN_FRAMES;

			// if it's time to write the next frame
			if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {

				String outputFilename = dumpImageToFile(event.getImage());
				images.add(outputFilename);

				// indicate file written
				double seconds = ((double) event.getTimeStamp()) / Global.DEFAULT_PTS_PER_SECOND;
				System.out.printf("at elapsed time of %6.3f seconds wrote: %s\n", seconds, outputFilename);

				// update last write time
				mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
			}

		}

		private String dumpImageToFile(BufferedImage image) {
			try {
				String outputFilename = filePrefix + System.currentTimeMillis() + ".png";
				ImageIO.write(image, "png", new File(outputFilename));
				return outputFilename;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

	}
}