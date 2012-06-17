package org.opencv.samples.fd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;
import facepoint.BlueMarks;
import facepoint.PointCluster;

class FdView extends SampleCvViewBase {
    private static final String TAG = "Sample::FdView";
	private static final int MINIMUM_REGIONS = 10;
	private static final int[][] REGIONS = { { 0, 1 }, { 2, 5 }, { 6, 9 }};
    private Mat                 mRgba;
    private Mat                 mGray;

    private CascadeClassifier   mCascade;

    public FdView(Context context) {
        super(context);

        try {
            InputStream is = context.getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (mCascade.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mCascade = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());

            cascadeFile.delete();
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder _holder, int format, int width, int height) {
        super.surfaceChanged(_holder, format, width, height);

        synchronized (this) {
            // initialize Mats before usage
            mGray = new Mat();
            mRgba = new Mat();
        }
    }

    @Override
    protected Bitmap processFrame(VideoCapture capture) {
        capture.retrieve(mRgba, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
        capture.retrieve(mGray, Highgui.CV_CAP_ANDROID_GREY_FRAME);

        if (mCascade != null) {
            int height = mGray.rows();
            int faceSize = Math.round(height * FdActivity.minFaceSize);
            MatOfRect faces = new MatOfRect();
            mCascade.detectMultiScale(mGray, faces, 1.1, 2, 2 // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    , new Size(faceSize, faceSize), new Size());

            for (Rect r : faces.toArray())
            {
                Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
//            	int y0, y1, x0, x1;
//            	x0 = Math.min(r.x, mRgba.cols());
//            	y0 = Math.min(r.y, mRgba.rows());
//            	x1 = Math.min(r.x+r.width, mRgba.cols());
//            	y1 = Math.min(r.y+r.height, mRgba.rows());
//            	
//            	Rect rect = null;
            	
            	BlueMarks blueMarks = new BlueMarks();
            	blueMarks.setBlueMarksValues(mRgba, 1, MINIMUM_REGIONS, r);
            	
    			List<PointCluster> cluster = blueMarks.firstLocate(); // auffinden der Punkte
    			
    			if (cluster != null) {
	    			
	    			//System.out.println("AFP: Test");
	    			// Sort cluster marks
	    			facepoint.SortRegions sortRegions = new facepoint.SortRegions(); //TODO Sortregion
	    			cluster = sortRegions.sortRegionsCalc(MINIMUM_REGIONS, REGIONS, cluster);
	    			
	    			//System.out.println("AFP: Test2");
	
	    			// Assign each cluster an permanent index-number after sorting.
	    			for (int n = 0; n < cluster.size(); n++) {
	    				cluster.get(n).nr = n;
	    			}
	    			
	    			paint(cluster);
    			
    			}
    		    


//                mRgba = mRgba.submat(y0, y1, x0, x1);
/*
            	try {
					FileOutputStream out;
					out = getContext().openFileOutput("temp.png", Context.MODE_PRIVATE);
					Bitmap b = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
					Utils.matToBitmap(mRgba, b);
					b.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
					
					new MoodDetector().detectMood(new File(getContext().getFilesDir(), "temp.png"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	*/            
            }
        }

        Bitmap bmp = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.RGB_565/*.ARGB_8888*/);

        try {
        	Utils.matToBitmap(mRgba, bmp);
            return bmp;
        } catch(Exception e) {
        	Log.e("org.opencv.samples.puzzle15", "Utils.matToBitmap() throws an exception: " + e.getMessage());
            bmp.recycle();
            return null;
        }
    }

    private void paint(List<PointCluster> regions) {
  		//System.out.println("test " + rcValue.length + ", " + rcValue[1].x + rcValue[1].y + rcValue[1].width + rcValue[1].height);
//  		for(int k = 0; k < rcValue.length; k++) {
//  			recValue[k] = new Rectangle(rcValue[k].x, rcValue[k].y, rcValue[k].width, rcValue[k].height);
//  		}

  		if (regions != null)
		      for (PointCluster reg : regions) {
		        Rect r = reg.getRectangle();

                Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
//		        g.drawRect(r.x, r.y, r.width, r.height);
//		        g.drawString("" + regions.indexOf(reg), r.x, r.y - 2);
		        
//				g.setColor(java.awt.Color.GREEN);
		        // draw Rect
		        //g.drawRect(recValue.x, recValue.y, recValue.width, recValue.height); 
//				for(int k = 0; k < recValue.length; k++) {//face //eyes
//					//faceDetection.searchOpenCVAreas(path, frame); //TODO
//					//System.out.println("IP: path:" + path + ", "+ seq + ", " + recValue.length);
//					//fD.searchOpenCVAreas(path, seq);TODO YYY
//					
//					//recValue = fD.getRecValue();
//					//recValue[k] = fD.getRecValue()[k]; TODO YYY
//					
//					//g.drawRect(fD.getRecValue()[k].x, fD.getRecValue()[k].y,fD.getRecValue()[k].width, fD.getRecValue()[k].height); 
//	                Core.rectangle(mRgba, r.tl(), r.br(), new Scalar(0, 255, 0, 255), 3);
//					g.drawRect(recValue[k].x, recValue[k].y, recValue[k].width, recValue[k].height); 		
//					//System.out.println("IP:" + recValue[k].x+","+ recValue[k].y);
//					
//				}
		      }
	}

	@Override
    public void run() {
        super.run();

        synchronized (this) {
            // Explicitly deallocate Mats
            if (mRgba != null)
                mRgba.release();
            if (mGray != null)
                mGray.release();

            mRgba = null;
            mGray = null;
        }
    }
}
