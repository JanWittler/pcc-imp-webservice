package edu.kit.informatik.pcc.service.videoprocessing.chain.anonymization;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.ICodec;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Josh Romanowski
 */
public class VideoPictureConverter {

    public void splitUp(File video, File picDir, double fps) {
        Reader reader = new Reader(video.getAbsolutePath(), picDir.getAbsolutePath(), fps);
        reader.splitVideo();
    }

    public void merge(File picDir, double fps, int width, int height, File output) {
        Writer writer = new Writer(picDir.getAbsolutePath(), fps, width, height, output.getAbsolutePath());
        writer.readImages();
    }

    private class Reader extends MediaListenerAdapter {
        private double secondsBetweenFrames;
        private long microSecondsBetweenFrames;
        private long mLastPtsWrite = Global.NO_PTS;
        private int mVideoStreamIndex = -1;
        private String videoPath;
        private String outputDir;

        public Reader(String videoPath, String outputDir, double fps) {
            this.outputDir = outputDir;
            this.videoPath = videoPath;

            secondsBetweenFrames = 1 / fps;
            microSecondsBetweenFrames = (long) (Global.DEFAULT_PTS_PER_SECOND * secondsBetweenFrames);
        }

        public void splitVideo() {
            IMediaReader reader = ToolFactory.makeReader(videoPath);
            reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
            reader.addListener(this);
            while (reader.readPacket() == null) ;
        }

        public void onVideoPicture(IVideoPictureEvent event) {
            try {
                if (event.getStreamIndex() != mVideoStreamIndex) {
                    if (-1 == mVideoStreamIndex)
                        mVideoStreamIndex = event.getStreamIndex();
                    else
                        return;
                }

                if (mLastPtsWrite == Global.NO_PTS)
                    mLastPtsWrite = event.getTimeStamp() - microSecondsBetweenFrames;


                saveImage(event.getImage());
                mLastPtsWrite += microSecondsBetweenFrames;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private boolean saveImage(BufferedImage image) {
            try {
                String fileName = outputDir + File.separator + System.currentTimeMillis() + ".png";
                ImageIO.write(image, "png", new File(fileName));
            } catch (IOException e) {
                return false;
            }
            return true;
        }
    }

    private class Writer {
        IMediaWriter writer;
        private String picLocation;
        private int timeStamp = 0;
        private int frameLength;
        private int width;
        private int heigth;
        private String outLocation;

        public Writer(String picLocation, double fps, int width, int height, String outLocation) {
            this.picLocation = picLocation;
            this.width = width;
            this.heigth = height;
            this.outLocation = outLocation;
            frameLength = (int) Math.round(1 / fps * 1000000);
        }

        public void readImages() {
            writer = ToolFactory.makeWriter(outLocation);
            writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, width, heigth);

            File picDir = new File(picLocation);

            File[] pics = picDir.listFiles();
            if (pics == null)
                return;

            for (File file : pics) {

                BufferedImage bgrScreen;
                try {
                    bgrScreen = ImageIO.read(file);
                } catch (IOException e) {
                    return;
                }
                writer.encodeVideo(0, bgrScreen, timeStamp, TimeUnit.MICROSECONDS);
                timeStamp += frameLength;
            }
            writer.close();
        }
    }
}
