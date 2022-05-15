package com.store.app.departmentactivitymonitor.streaming;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

import com.store.app.departmentactivitymonitor.AppService;
import com.store.app.departmentactivitymonitor.config.ActivityMonitorConfig;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;

public class ActivityMonitorService implements AppService {

    private final ActivityMonitorConfig config;
    private final Logger logger;
    private final AvroProducer producer;
    private final AtomicBoolean lock = new AtomicBoolean();

    private JFrame frame;
    private JLabel label;

    public ActivityMonitorService(ActivityMonitorConfig config,
                                  Logger logger,
                                  AvroProducer producer) {
        this.config = config;
        this.logger = logger;
        this.producer = producer;

        nu.pattern.OpenCV.loadShared();
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
        System.setProperty("java.awt.headless", "false");

        frame = new JFrame();
        label = new JLabel();
        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        frame.getContentPane().add(label);
        frame.setVisible(true);
    }

    @Override
    public void run() {

        VideoCapture camera = new VideoCapture(0);
        // force webcam resolution
        camera.set(3, config.getDepartmentWidth());
        camera.set(4, config.getDepartmentHeight());

        Mat referenceFrame = null;
        int previousNumContours = 0;

        //The webcam may need some time / captured frames to adapt to ambience lighting. For this reason, some frames are grabbed and discarded.
        for(int i = 0; i < 20; i++){
            Mat image = new Mat();
            camera.read(image);
        }

        lock.set(true);
        while(lock.get()){
            int totalContours = 0;
            for(int frame = 0; frame < config.getNumFramesToAverage(); frame++) {
                Mat image = new Mat();
                if (!camera.read(image)) {
                    continue;
                }

                Mat modifiedImage = new Mat();
                Imgproc.cvtColor(image, modifiedImage, Imgproc.COLOR_BGR2GRAY);
                Imgproc.GaussianBlur(modifiedImage, modifiedImage, new Size(21, 21), 0);

                if (referenceFrame == null) {
                    referenceFrame = modifiedImage;
                }

                // Background subtraction and image binarization
                Mat frameDelta = new Mat();
                Core.absdiff(referenceFrame, modifiedImage, frameDelta);
                Mat frameThreshold = new Mat();
                Imgproc.threshold(frameDelta, frameThreshold, config.getBinarizationThreshold(), 255, Imgproc.THRESH_BINARY);

                // Dilate image and find all the contours
                Imgproc.dilate(frameThreshold, frameThreshold, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)), new Point(-1, -1), 2);
                List<MatOfPoint> contours = new ArrayList<>();
                Imgproc.findContours(frameThreshold, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                int numContours = 0;
                for (int i = 0; i < contours.size(); i++) {
                    MatOfPoint c = contours.get(i);
                    if (Imgproc.contourArea(c) < config.getMinContourArea()) {
                        continue;
                    }

                    numContours += 1;

                    // get rectangle area of object
                    Rect rect = Imgproc.boundingRect(c);
                    Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);

                    // find object's centroid
                    int coordXCentroid = (rect.x + rect.x + rect.width) / 2;
                    int coordYCentroid = (rect.y + rect.y + rect.height) / 2;
                    Point objectCentroid = new Point(coordXCentroid, coordYCentroid);
                    Imgproc.circle(image, objectCentroid, 1, new Scalar(0, 0, 0), 5);
                }
                Imgproc.putText(image, "Num Customers: " + numContours, new Point(10, 50),
                                Core.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(250, 0, 1), 2);

                showResult(image);

                totalContours += numContours;

                try {
                    Thread.sleep(config.getDelayBetweenFrames());
                } catch (Exception e) {
                    // ignore for now
                }
            }

            int averageNumContours = Math.floorDiv(totalContours, config.getNumFramesToAverage());
            if (averageNumContours != previousNumContours) {
                previousNumContours = averageNumContours;
                produceEvent(averageNumContours);
            }
        }
        camera.release();
    }

    public void showResult(Mat img) {
        Imgproc.resize(img, img, new Size(config.getDepartmentWidth(), config.getDepartmentHeight()));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            label.setIcon(new ImageIcon(bufImage));
            frame.pack();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void produceEvent(int numContours){
        StoreSensorEvent event = new StoreSensorEvent();
        event.setAction("Updated");
        event.setEventLocation("store1-department15");
        event.setEventTime(Instant.now());
        event.setId("pi1");
        event.setSensorData(Integer.toString(numContours));
        event.setSensorName("camera");
        event.setSensorContext(new HashMap<>());
        producer.send("store1-department15-pi1-camera", event);
    }

    @Override
    public void close() {
        if (lock.compareAndSet(true, false)) {
            logger.info("closing activity monitor service");
        }
    }

    @Override
    public String serviceName() {
        return getClass().getSimpleName();
    }
}
